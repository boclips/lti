import React from 'react';
import ReactDOM from 'react-dom';
import { ApiBoclipsClient } from 'boclips-api-client';
import App from './App';
import AxiosService from '../../service/axios/AxiosService';
import { AppConstants } from '../../types/AppConstants';

async function startApp() {
  AxiosService.configureAxios();

  const apiClient = await ApiBoclipsClient.create(
    AxiosService.getApiAuthenticatedInstance(),
    AppConstants.API_BASE_URL,
  );

  ReactDOM.render(
    <React.StrictMode>
      <App apiClient={apiClient} />
    </React.StrictMode>,
    document.getElementById('root'),
  );
}

startApp();
