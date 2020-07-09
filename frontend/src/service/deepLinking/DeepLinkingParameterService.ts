import queryString from 'query-string';

const extractParameter = (name: string): string => {
  const { query } = queryString.parseUrl(window.location.href);
  const parameterValue = query[name];

  if (!parameterValue || Array.isArray(parameterValue)) {
    throw new Error(`Expected a single value for parameter ${name}`);
  }

  return parameterValue;
};

const DeepLinkingParameterService = {
  getReturnUrl() {
    return extractParameter('deep_link_return_url');
  },
  getData() {
    return extractParameter('data');
  },
  getDeploymentId() {
    return extractParameter('deployment_id');
  },
};

export default DeepLinkingParameterService;
