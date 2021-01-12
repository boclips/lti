import React from 'react';
import { Video } from '@boclips-ui/video';
import getPlayer from '../../Player/getPlayer';
import ProviderBadge from '@boclips-ui/provider-badge';
import { VideoCardV2 } from '@boclips-ui/video-card-v2';
import s from './VideoCardWrapper.module.less';

interface Props {
  video: Video;
  query: string;
  actions: Array<React.ReactNode>;
}

export const VideoCardWrapper = ({ video, query, actions }: Props) => {
  return (
    <div style={{ marginBottom: '4px' }} className={s.videoCard}>
      <VideoCardV2
        key={video.id}
        video={video}
        topBadge={
          <div>
            <ProviderBadge
              isLicensed={video?.playback?.type === 'STREAM'}
              key="provider-badge"
            />
          </div>
        }
        actions={actions}
        videoPlayer={getPlayer(query, video)}
      />
    </div>
  );
};
