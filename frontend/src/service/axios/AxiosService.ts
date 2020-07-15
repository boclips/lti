import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { AppConstants } from '../../types/AppConstants';

const apiAccessTokenSupportingInstance: AxiosInstance = axios.create();
const vanillaInstance: AxiosInstance = axios.create();

let hasBeenConfigured: boolean = false;

class AxiosService {
  public static configureAxios(
    tokenFactory: (
      axios: AxiosInstance,
    ) => Promise<string> = AxiosService.ltiTokenFactory,
  ): void {
    const nonModifiedAxiosInstance = axios.create();

    apiAccessTokenSupportingInstance.interceptors.request.use((config: AxiosRequestConfig) =>
      tokenFactory(nonModifiedAxiosInstance).then((token) => {
        config.headers.Authorization = `Bearer ${token}`;
        return config;
      }),
    );

    hasBeenConfigured = true;
  }

  public static ltiTokenFactory(axiosInstance: AxiosInstance): Promise<string> {
    return axiosInstance
      .get(`${AppConstants.LTI_BASE_URL}/auth/token`, {
        withCredentials: true,
      })
      .then((response: AxiosResponse<string>) => response.data)
      .catch((e) => `Cannot retrieve token ${e}`);
  }

  public static getApiAuthenticatedInstance(): AxiosInstance {
    if (!hasBeenConfigured) {
      throw Error('Axios has not been configured');
    }
    return apiAccessTokenSupportingInstance;
  }

  public static getVanillaInstance(): AxiosInstance {
    if (!hasBeenConfigured) {
      throw Error('Axios has not been configured');
    }
    return vanillaInstance;
  }
}

export default AxiosService;
