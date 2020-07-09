import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import ConfigurableConstants from '../../types/AppConstants';
import AuthService from './AuthService';
import AppConstantsFactory from '../../testSupport/AppConstantsFactory';

jest.mock('../../types/AppConstants', () => ({
  get AppConstants(): ConfigurableConstants {
    return AppConstantsFactory.sample({
      LTI_BASE_URL: 'https://lti/token',
    });
  },
}));

describe('AuthService', () => {
  it('axios calls invoke given token factory on requests', async () => {
    const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
    AuthService.configureAxios(tokenFactory);

    try {
      await axios.get('https://google.com/');
    } catch (e) {
      // We just care about executing the call
    }

    expect(tokenFactory).toHaveBeenCalled();
  });

  it('appends the bearer token in Authorization header', async () => {
    const axiosMock = new MockAdapter(axios);
    axiosMock
      .onGet('https://api.example.com/v1/resource')
      .reply(200, JSON.stringify({}));

    const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
    AuthService.configureAxios(tokenFactory);

    const response = await axios.get('https://api.example.com/v1/resource');

    expect(response.config.headers.Authorization).toEqual(
      'Bearer i-am-a-token',
    );
  });

  it('does not get into an infinite loop trying to fetch a token', async () => {
    const factory = AuthService.ltiTokenFactory;
    AuthService.configureAxios(factory);

    const axiosInstance = axios.create();
    const axiosMock = new MockAdapter(axiosInstance);
    axiosMock.onGet('https://lti/token').reply(200, 'valid-jwt-token');

    await factory(axiosInstance);

    expect(axiosMock.history.get[0].headers.Authorization).toBeUndefined();
  });

  describe('ltiTokenFactory', () => {
    const axiosInstance = axios.create();

    it('returns the token from specified endpoint', async () => {
      const axiosMock = new MockAdapter(axiosInstance);
      axiosMock.onGet('https://lti/token').reply(200, 'valid-jwt-token');

      const token = await AuthService.ltiTokenFactory(axiosInstance);

      expect(token).toEqual('valid-jwt-token');
    });

    it('throws when not able to retrieve token', async () => {
      const axiosMock = new MockAdapter(axiosInstance);
      axiosMock.onGet('https://lti/token').reply(401, '<div>ERROR</div>');

      try {
        await AuthService.ltiTokenFactory(axiosInstance);
      } catch (e) {
        expect(e).toEqual('Cannot retrieve token');
      }
    });
  });
});
