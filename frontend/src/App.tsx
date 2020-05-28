import React from 'react';
import './App.css';
import { AuthService } from './service/auth/AuthService';
import { ApiClient } from './service/client/ApiClient';
import { VideoService } from './service/video/VideoService';

function App(): React.ReactElement {
  AuthService.configureAxios();

  test();

  return (
    <div className="App">
      <header className="App-header">
        <p>Hello, World!</p>
      </header>
    </div>
  );
}

const test = () => {
  new ApiClient((window as any).Environment.API_BASE_URL)
    .getClient()
    .then((client) => {
      new VideoService(client).searchVideos({ query: 'hello' });
    });
};

export default App;
