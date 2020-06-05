import { FakeVideosClient } from 'boclips-api-client/dist/sub-clients/videos/client/FakeVideosClient';
import { VideoWithBoclipsProjectionFactory as VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import ApiClient from '../client/ApiClient';
import VideoService from './VideoService';

describe('VideoService', () => {
  const apiClientPromise = new ApiClient('https://api.example.com').getClient();

  it('Can search videos by query', async () => {
    const apiVideos = [];
    apiVideos.push(
      VideoFactory.sample({
        title: 'TED1',
        id: 'ted1',
        contentPartner: 'ted1',
      }),
    );
    apiVideos.push(VideoFactory.sample({ title: 'TED2' }));

    const fakeApiClient = (await apiClientPromise) as FakeBoclipsClient;
    const service = new VideoService(fakeApiClient);

    const fakeVideosClient: FakeVideosClient = fakeApiClient.videos;
    apiVideos.forEach((video) => fakeVideosClient.insertVideo(video));

    // Right now the fake only allows searching by cp or id
    const videos = await service.searchVideos({
      content_partner: ['ted1'],
    });

    expect(videos.page.length).toEqual(1);
    expect(videos.page[0].id).toEqual('ted1');
  });
});
