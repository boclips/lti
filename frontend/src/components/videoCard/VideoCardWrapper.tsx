import React from 'react';
import { Video } from '@boclips-ui/video';
import ProviderBadge from '@boclips-ui/provider-badge';
import { VideoCardV2 } from '@boclips-ui/video-card-v2';
// @ts-ignore
import { VideoCardV3 } from '@boclips-ui/video-card-v3';
import c from 'classnames';
import getPlayer from '../../Player/getPlayer';
import s from './VideoCardWrapper.module.less';

interface Props {
  video: Video;
  query: string;
  actions?: any;
  showVideoCardV3: boolean;
}

const VideoCardWrapper = ({
  video,
  query,
  actions,
  showVideoCardV3,
}: Props) => (
  <div
    style={{ marginBottom: showVideoCardV3 ? '24px' : '4px' }}
    className={c({
      [s.videoCard]: !showVideoCardV3,
      [s.videoCardV3]: showVideoCardV3,
    })}
  >
    {showVideoCardV3 ? (
      <VideoCardV3
        duration={video.playback.duration.format('mm:ss')}
        title={video.title}
        key={video.id}
        video={video}
        actions={actions}
        videoPlayer={getPlayer(query, video)}
      />
    ) : (
      <VideoCardV2
        key={video.id}
        video={video}
        topBadge={
          <div>
            {video?.playback?.type === 'YOUTUBE' ? (
              <ProviderBadge
                key="provider-badge"
                customClassName={s.providerBadge}
              />
            ) : null}
          </div>
        }
        actions={actions}
        videoPlayer={getPlayer(query, video)}
      />
    )}
  </div>
);

export default VideoCardWrapper;
