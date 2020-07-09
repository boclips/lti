import { VideoWithBoclipsProjectionFactory as VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import ApiClient from '../client/ApiClient';
import VideoService from './VideoService';

describe('VideoService', () => {
  const apiClientPromise = new ApiClient(
    'https://api.example.com',
  ).getClient() as Promise<FakeBoclipsClient>;

  it('Can search videos by query', async () => {
    const fakeApiClient = (await apiClientPromise) as FakeBoclipsClient;

    fakeApiClient.videos.insertVideo(
      VideoFactory.sample({
        title: 'TED1',
        id: 'ted1',
        channel: 'ted1',
      }),
    );
    fakeApiClient.videos.insertVideo(VideoFactory.sample({ title: 'TED2' }));

    const service = new VideoService(fakeApiClient);
    // Right now the fake only allows searching by cp or id
    const videos = await service.searchVideos({
      channel: ['ted1'],
    });

    expect(videos.page.length).toEqual(1);
    expect(videos.page[0].id).toEqual('ted1');
  });
});
