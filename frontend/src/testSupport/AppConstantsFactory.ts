import ConfigurableConstants from '../types/AppConstants';

class AppConstantsFactory {
  public static sample(
    appConstants: Partial<ConfigurableConstants>,
  ): ConfigurableConstants {
    return {
      LTI_BASE_URL: 'http://localhost/auth/token',
      API_BASE_URL: 'http://localhost/v1',
      USER_ID: 'user-123',
      ...appConstants,
    };
  }
}

export default AppConstantsFactory;
