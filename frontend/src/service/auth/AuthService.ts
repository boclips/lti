import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

declare const window: any;

export class AuthService {
  public static configureAxios(
    tokenFactory: (
      axios: AxiosInstance,
    ) => Promise<string> = AuthService.ltiTokenFactory,
  ): void {
    const nonModifiedAxiosInstance = axios.create();
    axios.interceptors.request.use((config: AxiosRequestConfig) => {
      return tokenFactory(nonModifiedAxiosInstance).then((token) => {
        config.headers['Authorization'] = `Bearer ${token}`;
        return config;
      });
    });
  }

  public static ltiTokenFactory(axiosInstance: AxiosInstance): Promise<string> {
    return axiosInstance
      .get(window.Environment.LTI_TOKEN_URL)
      .then((response: AxiosResponse<string>) => response.data)
      .catch(() => 'Cannot retrieve token');
  }
}
