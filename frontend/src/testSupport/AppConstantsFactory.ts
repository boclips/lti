import ConifgurableConstants from '../types/AppConstants';

class AppConstantsFactory {
  public static sample(
    appConstants: Partial<ConifgurableConstants>,
  ): ConifgurableConstants {
    return {
      LTI_TOKEN_URL: 'http://localhost/auth/token',
      API_BASE_URL: 'http://localhost/v1',
      ...appConstants,
    };
  }
}

export default AppConstantsFactory;
