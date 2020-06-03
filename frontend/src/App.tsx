import React from 'react';
import { AuthService } from './service/auth/AuthService';
import { ApiClient } from './service/client/ApiClient';
import { VideoService } from './service/video/VideoService';
import { configureStore, getDefaultMiddleware } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { AppConstants } from './types/AppConstants';
import { LtiView } from './views/LtiView';
import './index.less';
const store = configureStore({
  reducer: {},
  middleware: [...getDefaultMiddleware()],
});

const App = () => {
  AuthService.configureAxios();

  test();

  return (
    <Provider store={store}>
      <LtiView />
    </Provider>
  );
};

const test = () => {
  new ApiClient(AppConstants.API_BASE_URL).getClient().then((client) => {
    new VideoService(client).searchVideos({ query: 'hello' });
  });
};

export default App;
