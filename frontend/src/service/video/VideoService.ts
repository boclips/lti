import { BoclipsClient } from 'boclips-api-client';
import { VideoSearchRequest } from 'boclips-api-client/dist/sub-clients/videos/model/VideoSearchRequest';
import Pageable from 'boclips-api-client/dist/sub-clients/common/model/Pageable';
import { Video } from '@bit/dev-boclips.boclips-ui.types.video/index';
import { Video as ClientVideo } from 'boclips-api-client/dist/sub-clients/videos/model/Video';
import convertApiClientVideo from '../../service/video/convertVideoFromApi';

class VideoService {
  // In other webapps we pass a promise to the service
  // Let's keep it simple for now, but if it gets annoying to set up we may want to think about
  // adding a promise here
  constructor(private client: BoclipsClient) {}

  public searchVideos(
    searchRequest: VideoSearchRequest,
  ): Promise<Pageable<Video>> {
    return this.client.videos.search(searchRequest).then((response: Pageable<ClientVideo>) => ({
      page: response.page.map(convertApiClientVideo),
      pageSpec: response.pageSpec
    }));
  }
}
export default VideoService;
