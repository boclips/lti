import React from 'react';
import { fireEvent, render } from '@testing-library/react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { PlayerFactory } from 'boclips-player';
import { mocked } from 'ts-jest/utils';
import { SubjectFactory } from 'boclips-api-client/dist/test-support/SubjectsFactory';
import { AttachmentFactory } from 'boclips-api-client/dist/test-support/AttachmentsFactory';
import App from './App';
import ApiClient from '../../service/client/ApiClient';
import AxiosService from '../../service/axios/AxiosService';

jest.mock('boclips-player');

describe('Search view', () => {
  let fakeApiClient: Promise<FakeBoclipsClient>;

  beforeAll(() => {
    AxiosService.configureAxios();

    fakeApiClient = new ApiClient(
      'https://api.example.com',
    ).getClient() as Promise<FakeBoclipsClient>;
  });

  it('renders search bar', () => {
    const appComponent = render(<App />);
    expect(appComponent.getByPlaceholderText(/search for videos/i)).toBeInTheDocument();
  });

  it("search query is added to the player's AnalyticsOptions so it can be sent with events", async () => {
    const appComponent = render(<App />);
    const videosClient = (await fakeApiClient).videos;
    videosClient.insertVideo(VideoFactory.sample({ id: '1', title: 'cats 1' }));

    const searchTextInput = appComponent.getByPlaceholderText('Search for videos...');
    fireEvent.change(searchTextInput, { target: { value: 'cats' } });
    expect(await appComponent.findByDisplayValue('cats')).toBeInTheDocument();

    const searchButton = appComponent.getByText('Search').closest('button');
    fireEvent.click(searchButton!!);
    expect(await appComponent.findByText('FILTER BY:')).toBeInTheDocument();

    expect(await appComponent.findByText('1 result found:')).toBeInTheDocument();

    const optionsPassed = mocked(PlayerFactory.get).mock.calls[0][1];
    expect(optionsPassed!!.analytics!!.metadata).toEqual(
      expect.objectContaining({ query: 'cats' }),
    );
  });

  it('loads the video card with the correct badges', async () => {
    const appComponent = render(<App />);
    const videosClient = (await fakeApiClient).videos;
    videosClient.insertVideo(VideoFactory.sample({
      id: '1',
      title: 'goats',
      subjects: [SubjectFactory.sample({ name: 'Design' })],
      attachments: [AttachmentFactory.sample({ id: 'i am attachment' })],
      ageRange: {
        min: 3,
        max: 5,
      },
      bestFor: [{
        label: 'Hook'
      }]

    }));

    const searchTextInput = appComponent.getByPlaceholderText('Search for videos...');
    fireEvent.change(searchTextInput, { target: { value: 'goats' } });

    const searchButton = appComponent.getByText('Search').closest('button');
    fireEvent.click(searchButton!!);

    expect(await appComponent.findByText('Subject:')).toBeVisible();
    expect(await appComponent.findByText('Design')).toBeVisible();

    expect(await appComponent.queryByText('Best for:')).not.toBeInTheDocument();
    expect(await appComponent.queryByText('Hook')).not.toBeInTheDocument();

    expect(await appComponent.findByText('Ages:')).toBeVisible();
    expect(await appComponent.findByText('3-5')).toBeVisible();

    expect(await appComponent.queryByTestId('attachment-badge')).not.toBeInTheDocument();
  });
});
