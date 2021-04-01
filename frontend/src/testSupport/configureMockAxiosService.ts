import axios, { AxiosInstance } from 'axios';
import MockAdapter from 'axios-mock-adapter';
import AxiosService from '../service/axios/AxiosService';

export interface MockAxiosInstances {
  baseInstance: AxiosInstance;
  baseMock: MockAdapter;
  vanillaInstance: AxiosInstance;
  vanillaMock: MockAdapter;
  apiInstance: AxiosInstance;
  apiMock: MockAdapter;
}

const getMockAxiosInstances = (): MockAxiosInstances => {
  const baseInstance = axios.create();
  const apiInstance = axios.create();
  const vanillaInstance = axios.create();

  return {
    baseInstance,
    baseMock: new MockAdapter(baseInstance),
    vanillaInstance,
    vanillaMock: new MockAdapter(vanillaInstance),
    apiInstance,
    apiMock: new MockAdapter(apiInstance),
  };
};

export const configureMockAxiosService = (
  tokenFactory: (
    axios: AxiosInstance,
    setAuthError: () => void,
  ) => Promise<string> = AxiosService.ltiTokenFactory,
): MockAxiosInstances => {
  const mockAxiosInstances = getMockAxiosInstances();

  AxiosService.configureAxios(
    jest.fn(),
    tokenFactory,
    mockAxiosInstances.baseInstance,
    mockAxiosInstances.apiInstance,
    mockAxiosInstances.vanillaInstance,
  );

  return mockAxiosInstances;
};
