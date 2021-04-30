import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import {VideoFactory} from "boclips-api-client/dist/test-support/VideosFactory";

const apiClient = new FakeBoclipsClient();
apiClient.videos.insertVideo(VideoFactory.sample({title: "Minute Physics"}))

ReactDOM.render(
  <React.StrictMode>
    <App apiClient={apiClient} />
  </React.StrictMode>,
  document.getElementById('root'),
);
