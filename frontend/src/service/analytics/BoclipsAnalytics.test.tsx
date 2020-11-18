import AnalyticsFactory from './AnalyticsFactory';
import AxiosService from '../axios/AxiosService';

describe('Boclips analytics', () => {
  it('logInteraction rejects when link is missing', async () => {
    AxiosService.configureAxios();

    const video = {
      id: 'id-1',
      links: {},
    };

    const result = AnalyticsFactory.getInstance().trackVideoInteraction(
      video,
      'copied-to-google-classroom',
    );

    await result.catch((e) => {
      expect(e.message).toEqual('Video id-1 has no logInteraction link');
    });
  });
});
