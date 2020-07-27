import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { AppConstants } from '../../types/AppConstants';

class AuthService {
  public static configureAxios(
    tokenFactory: (
      axios: AxiosInstance,
    ) => Promise<string> = AuthService.ltiTokenFactory,
  ): void {
    const nonModifiedAxiosInstance = axios.create();

    axios.interceptors.request.use((config: AxiosRequestConfig) =>
      tokenFactory(nonModifiedAxiosInstance).then((token) => {
        config.headers.Authorization = `Bearer ${token}`;
        return config;
      }),
    );
  }

  public static ltiTokenFactory(axiosInstance: AxiosInstance): Promise<string> {
    return axiosInstance
      .get(AppConstants.LTI_TOKEN_URL, {
        withCredentials: true,
      })
      .then((response: AxiosResponse<string>) => response.data)
      .catch(() => 'Cannot retrieve token');
  }
}

export default AuthService;
