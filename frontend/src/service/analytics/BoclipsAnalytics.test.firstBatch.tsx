import { Link } from '@boclips-ui/link';
import MockAdapter from 'axios-mock-adapter';
import AnalyticsFactory from './AnalyticsFactory';
import AxiosService from '../axios/AxiosService';

describe('Boclips analytics', () => {
  it('logInteraction', async () => {
    AxiosService.configureAxios();
    const axiosInstance = AxiosService.getApiAuthenticatedInstance();
    const axiosMock = new MockAdapter(axiosInstance);

    axiosMock.onPost().reply(200);

    const video = {
      id: 'id-1',
      links: {
        logInteraction: new Link({
          href: '/v1/videos/id-1/events?logVideoInteraction=true&type={type}',
        }),
      },
    };

    await AnalyticsFactory.getInstance().trackVideoInteraction(
      video,
      'copied-to-google-classroom',
    );

    expect(axiosMock.history.post[0].url).toEqual(
      '/v1/videos/id-1/events?logVideoInteraction=true&type=copied-to-google-classroom',
    );
  });
});
