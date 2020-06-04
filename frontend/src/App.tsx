import React, { ReactElement } from 'react';
import { AuthService } from './service/auth/AuthService';
import { configureStore, getDefaultMiddleware } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { LtiView } from './views/LtiView';
import './index.less';
const store = configureStore({
  reducer: {},
  middleware: [...getDefaultMiddleware()],
});

const App = (): ReactElement => {
  AuthService.configureAxios();

  return (
    <Provider store={store}>
      <LtiView />
    </Provider>
  );
};

export default App;
