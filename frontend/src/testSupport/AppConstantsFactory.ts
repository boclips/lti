import ConfigurableConstants from '../types/AppConstants';

class AppConstantsFactory {
  public static sample(
    appConstants: Partial<ConfigurableConstants>,
  ): ConfigurableConstants {
    return {
      LTI_BASE_URL: 'http://localhost/auth/token',
      API_BASE_URL: 'http://localhost/v1',
      ...appConstants,
    };
  }
}

export default AppConstantsFactory;
