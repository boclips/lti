import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import App from './App';
import ApiClient from "../../service/client/ApiClient";
import {FakeBoclipsClient} from "boclips-api-client/dist/test-support";
import {VideoFactory} from "boclips-api-client/dist/test-support/VideosFactory";
import AxiosService from "../../service/axios/AxiosService";

test('renders learn react link', () => {
  const { getByPlaceholderText } = render(<App />);
  const pElement = getByPlaceholderText(/search for videos/i);
  expect(pElement).toBeInTheDocument();
});

let fakeApiClient: Promise<FakeBoclipsClient>;

beforeAll(() => {

  AxiosService.configureAxios();

  fakeApiClient = new ApiClient(
    'https://api.example.com',
  ).getClient() as Promise<FakeBoclipsClient>;

});

test("search query is reported to the backend with playback event", async () => {

  const appComponent = render(<App />);
  const searchTextInput = appComponent.getByPlaceholderText("Search for videos...");

  fireEvent.change(searchTextInput, { target: { value: 'cats' } });
  expect(await appComponent.findByDisplayValue('cats')).toBeInTheDocument();

  (await fakeApiClient).videos.insertVideo(
    VideoFactory.sample({ id: '123', title: 'The Cat number 1', description: 'for cats query' }),
  );
  (await fakeApiClient).videos.insertVideo(
    VideoFactory.sample({ id: '124', title: 'The Cat number 2', description: 'for cats query' }),
  );

  const searchButton = appComponent.getByText("Search").closest("button");
  fireEvent.click(searchButton!!);

  expect(await appComponent.findByText("results found:")).toBeInTheDocument();
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
