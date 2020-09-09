import React, { useState } from 'react';
import AxiosService from './AxiosService';
import ErrorView from '../../views/errorView';

// export const AxiosWrapper = (application: any) => {
//   const [authenticated, setAuthenticated] = useState<Boolean>(true);
//   AxiosService.configureAxios(() => setAuthenticated(false));
//
//   return authenticated ? application : <ErrorView />;
// };

export const AxiosWrapper = (WrappedComponent: any) => {
  return (props) => {
    const [authenticated, setAuthenticated] = useState<Boolean>(true);
    AxiosService.configureAxios(() => setAuthenticated(false));

    return authenticated ? <WrappedComponent {...props}/> : <ErrorView />;
  }
};
