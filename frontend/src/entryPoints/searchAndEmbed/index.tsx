import ReactDOM from 'react-dom';
import React from 'react';
import { ApiBoclipsClient } from 'boclips-api-client';
import App from './App';
import AxiosService from '../../service/axios/AxiosService';
import { AppConstants } from '../../types/AppConstants';
import ErrorView from '../../views/errorView';

AxiosService.configureAxios();

ApiBoclipsClient.create(
  AxiosService.getApiAuthenticatedInstance(),
  AppConstants.API_BASE_URL,
)
  .then((apiClient) => {
    ReactDOM.render(
      <React.StrictMode>
        <App apiClient={apiClient} />
      </React.StrictMode>,
      document.getElementById('root'),
    );
  })
  .catch(() => {
    ReactDOM.render(
      <React.StrictMode>
        <ErrorView />
      </React.StrictMode>,
      document.getElementById('root'),
    );
  });
