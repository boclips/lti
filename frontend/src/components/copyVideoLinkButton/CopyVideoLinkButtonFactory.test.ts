import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import setupInitialLocation from '../../testSupport/setupInitialLocation';
import convertApiClientVideo from '../../service/video/convertVideoFromApi';
import CopyVideoLinkButtonFactory from './CopyVideoLinkButtonFactory';

const sampleVideo = convertApiClientVideo(
  VideoFactory.sample({ id: 'sample-video' }),
);

describe('CopyVideoLinkButton', () => {
  it('shows button when show_copy_link and embeddable_video_url are present', () => {
    setupInitialLocation(
      'http://dummy.com?show_copy_link=true&embeddable_video_url=http://video.com/{id}',
    );

    expect(CopyVideoLinkButtonFactory.getButton(sampleVideo)).not.toBeNull();
  });

  it('does not show button when show_copy_link is set to false', () => {
    setupInitialLocation(
      'http://dummy.com?show_copy_link=false&embeddable_video_url=http://video.com/{id}',
    );

    expect(CopyVideoLinkButtonFactory.getButton(sampleVideo)).toBeNull();
  });

  it('does not show button when embeddable_video_url is missing', () => {
    setupInitialLocation('http://dummy.com?show_copy_link=true');

    expect(CopyVideoLinkButtonFactory.getButton(sampleVideo)).toBeNull();
  });
});
