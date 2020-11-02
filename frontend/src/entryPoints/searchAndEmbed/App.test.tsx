import React from 'react';
import { fireEvent, render } from '@testing-library/react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { SubjectFactory } from 'boclips-api-client/dist/test-support/SubjectsFactory';
import App from './App';
import ApiClient from '../../service/client/ApiClient';
import AxiosService from '../../service/axios/AxiosService';

jest.mock('boclips-player');

describe('Search and embed view', () => {
  let fakeApiClient: Promise<FakeBoclipsClient>;

  beforeAll(() => {
    AxiosService.configureAxios();

    fakeApiClient = new ApiClient(
      'https://api.example1.com',
    ).getClient() as Promise<FakeBoclipsClient>;
  });

  it('renders the badges on video cards', async () => {
    const appComponent = render(<App />);
    const videosClient = (await fakeApiClient).videos;
    videosClient.insertVideo(
      VideoFactory.sample({
        id: '1',
        title: 'dogs 1',
        subjects: [SubjectFactory.sample({ name: 'Design' })]
      }));

    const searchTextInput = appComponent.getByPlaceholderText('Search for videos...');
    fireEvent.change(searchTextInput, { target: { value: 'dogs' } });

    const searchButton = appComponent.getByText('Search').closest('button');
    fireEvent.click(searchButton!!);

    expect(await appComponent.findByText('Subject:')).toBeVisible();
    expect(await appComponent.findByText('Design')).toBeVisible();
  });
});
