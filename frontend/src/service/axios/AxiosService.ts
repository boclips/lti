import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { AppConstants } from '../../types/AppConstants';

let hasBeenConfigured = false;

class AxiosService {
  static apiAccessTokenSupportingInstance: AxiosInstance;

  static vanillaInstance: AxiosInstance;

  public static configureAxios(
    setAuthError: () => void = () => {},
    tokenFactory: (
      axios: AxiosInstance,
      setAuthError: () => void,
    ) => Promise<string> = AxiosService.ltiTokenFactory,
    baseAxiosInstance: AxiosInstance = axios.create(),
    apiAxiosInstance: AxiosInstance = axios.create(),
    vanillaAxiosInstance: AxiosInstance = axios.create(),
  ): void {
    this.apiAccessTokenSupportingInstance = apiAxiosInstance;
    this.vanillaInstance = vanillaAxiosInstance;

    this.apiAccessTokenSupportingInstance.interceptors.request.use(
      (config: AxiosRequestConfig) => {
        if (AppConstants.USER_ID) {
          config.headers['Boclips-User-Id'] = AppConstants.USER_ID;
        }

        return tokenFactory(baseAxiosInstance, setAuthError).then((token) => {
          config.headers.Authorization = `Bearer ${token}`;
          return config;
        });
      },
    );

    hasBeenConfigured = true;
  }

  public static ltiTokenFactory(
    axiosInstance: AxiosInstance,
    setAuthError: () => void,
  ): Promise<string> {
    return axiosInstance
      .get(`${AppConstants.LTI_BASE_URL}/auth/token`, {
        withCredentials: true,
      })
      .then((response: AxiosResponse<string>) => response.data)
      .catch((e) => {
        setAuthError();
        return `Cannot retrieve token ${e}`;
      });
  }

  public static getApiAuthenticatedInstance(): AxiosInstance {
    if (!hasBeenConfigured) {
      throw Error('Axios has not been configured');
    }
    return this.apiAccessTokenSupportingInstance;
  }

  public static getVanillaInstance(): AxiosInstance {
    if (!hasBeenConfigured) {
      throw Error('Axios has not been configured');
    }
    return this.vanillaInstance;
  }
}

export default AxiosService;
