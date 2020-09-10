import { PlayerOptions } from 'boclips-player';
import Axios from 'axios';
import { AppConstants } from '../types/AppConstants';
import AxiosService from '../service/axios/AxiosService';

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
    tokenFactory: async () => AxiosService.ltiTokenFactory(Axios, () => {}),
    userIdFactory: AppConstants.USER_ID ? () => Promise.resolve(AppConstants.USER_ID) : undefined,
  },
};

export default playerOptions;
