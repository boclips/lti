import React from 'react';
import { render, screen } from '@testing-library/react';
import { Video } from '@bit/dev-boclips.boclips-ui.types.video/index';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { fireEvent } from '@testing-library/dom';
import ApiClient from '../../service/client/ApiClient';
import LtiView from './index';

describe('LTI test', () => {
  it('render search header', async () => {
    render(<LtiView renderVideoCard={() => <div />} />);

    expect(await screen.findByTestId('header-with-logo')).toBeInTheDocument();
    expect(await screen.findByTitle('Boclips logo')).toBeInTheDocument();
    expect(await screen.findByTestId('search-input')).toBeInTheDocument();
  });

  it('displays empty render with welcome message', async () => {
    render(<LtiView renderVideoCard={() => <div />} />);

    expect(
      await screen.findByText('Welcome to BoClips Video Library'),
    ).toBeInTheDocument();
    expect(
      await screen.findByText(
        'Use the search on top to find interesting videos',
      ),
    ).toBeInTheDocument();
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
});