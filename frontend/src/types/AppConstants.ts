declare global {
  interface Window {
    Environment: any;
  }
}

export default interface ConfigurableConstants {
  LTI_BASE_URL: string;
  API_BASE_URL: string;
  USER_ID: string;
}

export const AppConstants: ConfigurableConstants = {
  LTI_BASE_URL: window.Environment.LTI_BASE_URL,
  API_BASE_URL: window.Environment.API_BASE_URL,
  USER_ID: window.Environment.USER_ID,
};
