import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import ConfigurableConstants, { AppConstants } from '../../types/AppConstants';
import AxiosService from './AxiosService';
import AppConstantsFactory from '../../testSupport/AppConstantsFactory';

jest.mock('../../types/AppConstants', () => ({
  get AppConstants(): ConfigurableConstants {
    return AppConstantsFactory.sample({
      LTI_BASE_URL: 'https://lti/token',
    });
  },
}));

describe('AxiosService', () => {
  describe('accessing instances before configuring', () => {
    it('throws when service has not been configured yet', () => {
      expect(() => AxiosService.getApiAuthenticatedInstance()).toThrow();
      expect(() => AxiosService.getVanillaInstance()).toThrow();
    });
  });

  describe('using the API authenticated instance', () => {
    it('axios calls invoke given token factory on requests', async () => {
      const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
      AxiosService.configureAxios(tokenFactory);

      const axiosInstance = AxiosService.getApiAuthenticatedInstance();

      try {
        await axiosInstance.get('https://google.com/');
      } catch (e) {
        // We just care about executing the call
      }

      expect(tokenFactory).toHaveBeenCalled();
    });

    it('appends the bearer token in Authorization header', async () => {
      const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
      AxiosService.configureAxios(tokenFactory);

      const axiosInstance = AxiosService.getApiAuthenticatedInstance();

      const axiosMock = new MockAdapter(axiosInstance);
      axiosMock
        .onGet('https://api.example.com/v1/resource')
        .reply(200, JSON.stringify({}));

      const response = await axiosInstance.get(
        'https://api.example.com/v1/resource',
      );

      expect(response.config.headers.Authorization).toEqual(
        'Bearer i-am-a-token',
      );
    });
  });

  describe('using the vanilla instance', () => {
    it('does not append a bearer token to the requests', async () => {
      const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
      AxiosService.configureAxios(tokenFactory);

      const axiosInstance = AxiosService.getVanillaInstance();

      const axiosMock = new MockAdapter(axiosInstance);
      try {
        await axiosInstance.get('https://google.com/');
      } catch (e) {
        // We just care about executing the call
      }

      expect(axiosMock.history.get[0].headers.Authorization).toBeUndefined();
    });
  });

  describe('lti token factory', () => {
    const axiosInstance = axios.create();

    it('does not get into an infinite loop trying to fetch a token', async () => {
      const factory = AxiosService.ltiTokenFactory;
      AxiosService.configureAxios(factory);

      const freshAxiosInstance = axios.create();
      const axiosMock = new MockAdapter(freshAxiosInstance);
      axiosMock.onGet('https://lti/token').reply(200, 'valid-jwt-token');

      await factory(freshAxiosInstance);

      expect(axiosMock.history.get[0].headers.Authorization).toBeUndefined();
    });

    it('returns the token from specified endpoint', async () => {
      const axiosMock = new MockAdapter(axiosInstance);
      axiosMock
        .onGet(`${AppConstants.LTI_BASE_URL}/auth/token`)
        .reply(200, 'valid-jwt-token');

      const token = await AxiosService.ltiTokenFactory(axiosInstance);

      expect(token).toEqual('valid-jwt-token');
    });

    it('throws when not able to retrieve token', async () => {
      const axiosMock = new MockAdapter(axiosInstance);
      axiosMock.onGet('https://lti/token').reply(401, '<div>ERROR</div>');

      try {
        await AxiosService.ltiTokenFactory(axiosInstance);
      } catch (e) {
        expect(e).toEqual('Cannot retrieve token');
      }
    });
  });
});
