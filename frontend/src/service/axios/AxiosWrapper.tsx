import React, { useState } from 'react';
import AxiosService from './AxiosService';
import ErrorView from '../../views/errorView';

export const AxiosWrapper = (WrappedComponent: any) => (props) => {
  // eslint-disable-next-line
  const [authenticated, setAuthenticated] = useState<boolean>(true);
  AxiosService.configureAxios(() => setAuthenticated(false));
  /* eslint-disable-next-line react/jsx-props-no-spreading */
  return authenticated ? <WrappedComponent {...props} /> : <ErrorView />;
};

// export const useConfiguredAxios = () => {
//   const [authenticated, setAuthenticated] = useState<boolean>(true);
//   AxiosService.configureAxios(() => setAuthenticated(false));
// };

export default AxiosWrapper;
