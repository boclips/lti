declare global {
  interface Window {
    Environment: any;
  }
}

export default interface ConifgurableConstants {
  LTI_TOKEN_URL: string;
  API_BASE_URL: string;
}

export const AppConstants: ConifgurableConstants = {
  LTI_TOKEN_URL: window.Environment.LTI_TOKEN_URL,
  API_BASE_URL: window.Environment.API_BASE_URL,
};
