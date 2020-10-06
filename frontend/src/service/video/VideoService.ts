import { BoclipsClient } from 'boclips-api-client';
import { VideoSearchRequest } from 'boclips-api-client/dist/sub-clients/videos/model/VideoSearchRequest';
import Pageable from 'boclips-api-client/dist/sub-clients/common/model/Pageable';
import { Video } from '@boclips-ui/video';
import { Video as ClientVideo } from 'boclips-api-client/dist/sub-clients/videos/model/Video';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import { User } from 'boclips-api-client/dist/sub-clients/organisations/model/User';
import convertApiClientVideo from './convertVideoFromApi';

export interface ExtendedClientVideo<T> extends Pageable<T> {
  facets?: VideoFacets;
}

class VideoService {
  // In other webapps we pass a promise to the service
  // Let's keep it simple for now, but if it gets annoying to set up we may want to think about
  // adding a promise here
  constructor(private client: BoclipsClient) {}

  public searchVideos(
    searchRequest: VideoSearchRequest,
  ): Promise<Pageable<Video>> {
    return this.client.videos
      .search(searchRequest)
      .then((response: ExtendedClientVideo<ClientVideo>) => ({
        page: response.page.map(convertApiClientVideo),
        pageSpec: response.pageSpec,
        facets: response.facets,
      }));
  }

  public getSubjects(): Promise<Subject[]> {
    return this.client.subjects.getAll();
  }

  public getChannels(): Promise<Channel[]> {
    return this.client.channels.getAll();
  }

  public getCurrentUser(): Promise<User | null> {
    return this.client.users.getCurrentUser();
  }
}

export default VideoService;
