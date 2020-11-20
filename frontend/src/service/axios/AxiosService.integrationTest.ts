import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import ConfigurableConstants, { AppConstants } from '../../types/AppConstants';
import AxiosService from './AxiosService';
import AppConstantsFactory from '../../testSupport/AppConstantsFactory';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

jest.mock('../../types/AppConstants', () => ({
  get AppConstants(): ConfigurableConstants {
    return AppConstantsFactory.sample({
      LTI_BASE_URL: 'https://lti/token',
      USER_ID: 'boclips-user-id-test'
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
    it('appends the bearer token in Authorization header', async () => {
      const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
      const { apiInstance, apiMock } = configureMockAxiosService(tokenFactory);
      
      apiMock
        .onGet('https://api.example.com/v1/resource')
        .reply(200, JSON.stringify({}));

      const response = await apiInstance.get(
        'https://api.example.com/v1/resource',
      );

      expect(tokenFactory).toHaveBeenCalled();
      expect(response.config.headers.Authorization).toEqual(
        'Bearer i-am-a-token',
      );
    });
  });

  describe('using the vanilla instance', () => {
    it('does not append a bearer token to the requests', async () => {
      const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
      const { vanillaMock } = configureMockAxiosService(tokenFactory);

      vanillaMock.onGet().reply(200);
      await AxiosService.getVanillaInstance().get('https://google.com/');

      expect(vanillaMock.history.get[0].headers.Authorization).toBeUndefined();
    });
  });

  describe('external user', () => {
    it('sets the boclips-user-id header with the external user id', async () => {
      const tokenFactory = jest.fn(() => Promise.resolve('i-am-a-token'));
      const { apiMock } = configureMockAxiosService(tokenFactory);

      apiMock
        .onGet('https://api.example.com/v1/resource')
        .reply(200, JSON.stringify({}));

      const response = await AxiosService.getApiAuthenticatedInstance().get(
        'https://api.example.com/v1/resource',
      );

      const requestHeaders = response.config.headers;
      expect(requestHeaders.Authorization).toEqual('Bearer i-am-a-token');
      expect(requestHeaders['Boclips-User-Id']).toEqual('boclips-user-id-test');
    });
  });

  describe('lti token factory', () => {
    it('does not get into an infinite loop trying to fetch a token', async () => {
      const factory = AxiosService.ltiTokenFactory;
      configureMockAxiosService();

      const freshAxiosInstance = axios.create();
      const axiosMock = new MockAdapter(freshAxiosInstance);
      axiosMock.onGet('https://lti/token').reply(200, 'valid-jwt-token');

      await factory(freshAxiosInstance, () => {});

      expect(axiosMock.history.get[0].headers.Authorization).toBeUndefined();
    });

    it('returns the token from specified endpoint', async () => {
      const axiosInstance = axios.create();
      const axiosMock = new MockAdapter(axiosInstance);
      const setErrorSpy = jest.fn();

      axiosMock
        .onGet(`${AppConstants.LTI_BASE_URL}/auth/token`)
        .reply(200, 'valid-jwt-token');
      const token = await AxiosService.ltiTokenFactory(
        axiosInstance,
        setErrorSpy,
      );

      expect(token).toEqual('valid-jwt-token');
      expect(setErrorSpy).not.toHaveBeenCalled();
    });

    it('throws when not able to retrieve token', async () => {
      const axiosInstance = axios.create();
      const axiosMock = new MockAdapter(axiosInstance);
      const setErrorSpy = jest.fn();

      axiosMock.onGet('https://lti/token').reply(401, '<div>ERROR</div>');
      try {
        await AxiosService.ltiTokenFactory(axiosInstance, setErrorSpy);
      } catch (e) {
        expect(e).toEqual('Cannot retrieve token');
        expect(setErrorSpy).toHaveBeenCalledWith(true);
      }
    });
  });
});
