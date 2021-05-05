import ReactDOM from 'react-dom';
import React from 'react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import App from '../searchAndEmbed/App';

const apiClient = new FakeBoclipsClient();
apiClient.videos.insertVideo(VideoFactory.sample({ title: 'Minute Physics' }));

ReactDOM.render(
  <React.StrictMode>
    <App apiClient={apiClient} />
  </React.StrictMode>,
  document.getElementById('root'),
);
