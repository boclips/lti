import React, { useState } from 'react';
import AxiosService from './AxiosService';
import ErrorView from '../../views/errorView';

const AxiosWrapper = (WrappedComponent: any) => (props) => {
  // eslint-disable-next-line
  const [authenticated, setAuthenticated] = useState<boolean>(true);
  AxiosService.configureAxios(() => setAuthenticated(false));
  /* eslint-disable-next-line react/jsx-props-no-spreading */
  return authenticated ? <WrappedComponent {...props} /> : <ErrorView />;
};
export default AxiosWrapper;
