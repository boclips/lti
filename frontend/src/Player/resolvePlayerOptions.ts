import { PlayerOptions } from 'boclips-player';
import Axios from 'axios';
import { defaultAnalyticsOptions } from 'boclips-player/lib/Events/AnalyticsOptions';
import { AppConstants } from '../types/AppConstants';
import AxiosService from '../service/axios/AxiosService';

const resolvePlayerOptions = (query: string): Partial<PlayerOptions> => ({
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
    ratio: '16:9'
  },
  api: {
    tokenFactory: async () => AxiosService.ltiTokenFactory(Axios, () => {}),
    userIdFactory: AppConstants.USER_ID ? () => Promise.resolve(AppConstants.USER_ID) : undefined,
  },
  analytics: {
    ...defaultAnalyticsOptions,
    metadata: {
      query
    }
  }
});

export default resolvePlayerOptions;
