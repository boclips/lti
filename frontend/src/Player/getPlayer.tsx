import { Player } from 'boclips-player-react';
import React from 'react';
import './getPlayer.less';
import { Video } from '@boclips-ui/video';
import resolvePlayerOptions from './resolvePlayerOptions';

const getPlayer = (query: string, video: Video) => (
  <Player
    options={resolvePlayerOptions(query)}
    videoUri={video.links?.self?.getOriginalLink()}
  />
);

export default getPlayer;
