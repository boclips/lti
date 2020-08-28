import queryString from 'query-string';

export const extractSingleUrlParameter = (name: string): string => {
  const { query } = queryString.parseUrl(window.location.href);
  const parameterValue = query[name];

  if (!parameterValue || Array.isArray(parameterValue)) {
    throw new Error(`Expected a single value for parameter ${name}`);
  }

  return parameterValue;
};

export const tryExtractSingleUrlParameter = (name: string) => {
  let param;
  try {
    param = extractSingleUrlParameter(name);
  } catch (e) {
    param = null;
  }
  return param;
};
