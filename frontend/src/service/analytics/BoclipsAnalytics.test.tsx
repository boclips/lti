import { Link } from '@boclips-ui/link';
import AnalyticsFactory from './AnalyticsFactory';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

describe('Boclips analytics', () => {
  it('logInteraction', async () => {
    const { baseMock, apiMock } = configureMockAxiosService();
    baseMock.onGet('http://example.com/auth/token').reply(200, 'valid-token');
    apiMock.onPost().reply(200);

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

    expect(apiMock.history.post[0].url).toEqual(
      '/v1/videos/id-1/events?logVideoInteraction=true&type=copied-to-google-classroom',
    );
  });
});
