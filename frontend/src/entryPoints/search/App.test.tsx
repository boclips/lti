import React from 'react';
import { fireEvent, render } from '@testing-library/react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { PlayerFactory } from 'boclips-player';
import { mocked } from 'ts-jest/utils';
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
    // TODO let's also test that filters are added to the query
  });
});

/*
1. user types in a query in the search bar
2. the 'search' button is clicked
3. the results are displayed
4. the user clicks 'play' icon on the video
5. the user clicks 'pause' icon

expected:
'query' field is send to the backend with the value of the typed search query

 */
