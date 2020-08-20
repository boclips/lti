import { PlayerOptions } from 'boclips-player';
import { AppConstants } from '../types/AppConstants';

const playerOptions: Partial<PlayerOptions> = {
  interface: {
    controls: [
      'play-large',
      'play',
      'progress',
      'current-time',
      'mute',
      'volume',
      'captions',
      'fullscreen',
      'settings',
    ],
  },
  api: {
    userIdFactory: AppConstants.USER_ID ? () => Promise.resolve(AppConstants.USER_ID) : undefined,
  },
};

export default playerOptions;
