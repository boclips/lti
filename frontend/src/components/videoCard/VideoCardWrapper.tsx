import React, {ReactElement} from 'react';
import {Video} from '@boclips-ui/video';
import ProviderBadge from '@boclips-ui/provider-badge';
import {VideoCardV2} from '@boclips-ui/video-card-v2';
import getPlayer from '../../Player/getPlayer';
import s from './VideoCardWrapper.module.less';
import {User} from "boclips-api-client/dist/sub-clients/organisations/model/User";

interface Props {
  video: Video;
  query: string;
  actions: ReactElement[];
  user: User | null;
}

const VideoCardWrapper = ({video, query, actions}: Props) => (
  <div style={{marginBottom: '4px'}} className={s.videoCard}>
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

export default VideoCardWrapper;
