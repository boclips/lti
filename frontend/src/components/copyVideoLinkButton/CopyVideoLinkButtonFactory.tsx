import React from 'react';
import { Video } from '@boclips-ui/video';
import { tryExtractSingleUrlParameter } from '../../service/extractUrlParameter';
import CopyVideoLinkButton from './CopyVideoLinkButton';

class CopyVideoLinkButtonFactory {
  static getButton = (video: Video) => {
    const showCopyLink =
      tryExtractSingleUrlParameter('show_copy_link') === 'true';

    const embeddableVideoUrl = tryExtractSingleUrlParameter(
      'embeddable_video_url',
    );

    return (showCopyLink && embeddableVideoUrl)
      ? <CopyVideoLinkButton video={video}/>
      : null;
  };
}

export default CopyVideoLinkButtonFactory;
