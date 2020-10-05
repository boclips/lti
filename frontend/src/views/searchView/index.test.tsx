import React from 'react';
import { render, screen } from '@testing-library/react';
import { Video } from '@bit/boclips.dev-boclips-ui.types.video/index';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { fireEvent } from '@testing-library/dom';
import { UserFactory } from 'boclips-api-client/dist/test-support/UserFactory';
import ApiClient from '../../service/client/ApiClient';
import LtiView from './index';
import AxiosService from '../../service/axios/AxiosService';

describe('LTI test', () => {
  beforeEach(() => {
    AxiosService.configureAxios();
  });
  const searchFor = (query: string) => {
    const searchBar = screen.getByTestId('search-input');
    fireEvent.change(searchBar, { target: { value: query } });

    const searchButton = screen.getByText('Search');
    fireEvent.click(searchButton);
  };
  
  it('displays empty render with welcome message', async () => {
    const fakeApiClient = (await new ApiClient(
      'https://api.example.com',
    ).getClient()) as FakeBoclipsClient;

    fakeApiClient.users.insertCurrentUser(UserFactory.sample());

    render(<LtiView renderVideoCard={() => <div/>}/>);

    expect(
      await screen.findByText('Use the search on top to discover inspiring videos'),
    ).toBeInTheDocument();
  });

  it('displays SLS terms if user has that feature turned on', async () => {
    const fakeApiClient = (await new ApiClient(
      'https://api.example.com',
    ).getClient()) as FakeBoclipsClient;

    fakeApiClient.users.insertCurrentUser(UserFactory.sample({
      features: { LTI_SLS_TERMS_BUTTON: true }
    }));

    render(<LtiView renderVideoCard={() => <div />}/>);

    const aboutButton = await screen.findByText('About the app and services');

    expect(aboutButton).toBeVisible();
    fireEvent.click(aboutButton);
    expect(await screen.findByText('How does it work?')).toBeVisible();
  });

  it('doesn\'t show SLS terms if user should not see it', async () => {
    const fakeApiClient = (await new ApiClient(
      'https://api.example.com',
    ).getClient()) as FakeBoclipsClient;

    fakeApiClient.users.insertCurrentUser(UserFactory.sample({
      features: { }
    }));

    render(<LtiView renderVideoCard={() => <div />}/>);

    expect(await screen.queryByText('About the app and services')).toBeNull();
  });

  it('uses provided function to render videos on search', async () => {
    const fakeApiClient = (await new ApiClient(
      'https://api.example.com',
    ).getClient()) as FakeBoclipsClient;

    fakeApiClient.videos.insertVideo(
      VideoFactory.sample({ id: '123', title: 'Hi' }),
    );
    fakeApiClient.videos.insertVideo(
      VideoFactory.sample({ id: '456', title: 'Hi' }),
    );

    render(
      <LtiView
        renderVideoCard={(video: Video) => <div>Hello, video {video.id}</div>}
      />,
    );

    const searchBar = screen.getByTestId('search-input');
    fireEvent.change(searchBar, { target: { value: 'Hi' } });

    const searchButton = screen.getByText('Search');
    fireEvent.click(searchButton);

    await screen.findByText('Hello, video 123');
    await screen.findByText('Hello, video 456');
  });

  it('show filter panel when no results found but filters were applied', async () => {
    const fakeApiClient = (await new ApiClient(
      'https://api.example.com',
    ).getClient()) as FakeBoclipsClient;

    fakeApiClient.videos.insertVideo(
      VideoFactory.sample({ id: '123', title: 'Hi' }),
    );
    fakeApiClient.videos.setFacets({
      durations: {},
      resourceTypes: {},
      subjects: {},
      ageRanges: {
        '3-5': {
          hits: 3,
        },
      }
    });

    const view = render(
      <LtiView
        renderVideoCard={(video: Video) => <div>Hello, video {video.id}</div>}
      />,
    );

    searchFor('Hi');
    expect(await view.findByText('FILTER BY:')).toBeInTheDocument();

    await fireEvent.mouseDown(view.getByText('Age'));
    await fireEvent.click(view.getByTitle('3 - 5'));
    await fireEvent.click(view.getByText('APPLY'));

    searchFor('nothing');

    expect(await view.findByText('Try again using different keywords or change the filters'));
    expect(view.getByText('FILTER BY:')).toBeVisible();
  });

  it('does not show filter panel when no results found & no filters were applied', async () => {
    const view = render(
      <LtiView
        renderVideoCard={(video: Video) => <div>Hello, video {video.id}</div>}
      />,
    );

    searchFor('find nothing');

    expect(await view.findByText('Try different words that mean the same thing')).toBeVisible();
    expect(await view.findByText("Sorry, we couldn't find any results for")).toBeVisible();
    expect(await view.findByText('"find nothing"')).toBeVisible();
    expect(await view.findByText('Check your spelling')).toBeVisible();
    expect(await view.findByText('Try more general words')).toBeVisible();
    expect(view.queryByText('FILTER BY:')).toBeNull();
  });

  it('clicking filters button toggles buttons label', async () => {
    const fakeApiClient = (await new ApiClient(
      'https://api.example.com',
    ).getClient()) as FakeBoclipsClient;

    fakeApiClient.videos.insertVideo(
      VideoFactory.sample({ id: '123', title: 'Hi' }),
    );

    render(
      <LtiView
        collapsibleFilters
        renderVideoCard={(video: Video) => <div>Hello, video {video.id}</div>}
      />,
    );
    searchFor('Hi');

    const showFiltersButton = await screen.findByText('SHOW FILTERS');
    expect(showFiltersButton).toBeInTheDocument();
    fireEvent.click(showFiltersButton);

    const hideFiltersButton = await screen.findByText('HIDE FILTERS');
    expect(hideFiltersButton).toBeInTheDocument();
    expect(screen.queryByText('SHOW FILTERS')).toBeNull();
  });
});
