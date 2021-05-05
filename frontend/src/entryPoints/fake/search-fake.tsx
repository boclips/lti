import React from 'react';
import ReactDOM from 'react-dom';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import {
  FacetFactory,
  FacetsFactory,
} from 'boclips-api-client/dist/test-support/FacetsFactory';
import { SubjectFactory } from 'boclips-api-client/dist/test-support/SubjectsFactory';
import App from '../search/App';

const apiClient = new FakeBoclipsClient();
apiClient.videos.insertVideo(
  VideoFactory.sample({
    title: 'Minute Biology',
    subjects: [SubjectFactory.sample({ id: 'Biology', name: 'Biology' })],
    ageRange: { ids: ['2-4'], label: '12 - 14', max: 14, min: 12 },
  }),
);
apiClient.subjects.insertSubject(
  SubjectFactory.sample({ id: 'Biology', name: 'Biology' }),
);
apiClient.subjects.insertSubject(
  SubjectFactory.sample({ id: 'Physics', name: 'Physics' }),
);
apiClient.videos.insertVideo(
  VideoFactory.sample({
    title: 'Minute Physics',
    subjects: [SubjectFactory.sample({ id: 'Physics', name: 'Physics' })],
    ageRange: { ids: ['5-12'], label: '5 - 12', max: 12, min: 5 },
  }),
);
apiClient.channels.insertFixture({
  id: 'channel-1',
  pedagogyInformation: {
    subjects: ['blah'],
    ageRanges: { ids: ['5-12'], label: '5 - 12', max: 12, min: 5 },
    bestForTags: [],
  },
});
apiClient.videos.setFacets(
  FacetsFactory.sample({
    subjects: [
      FacetFactory.sample({ id: 'Physics', name: 'Physics', hits: 1 }),
      FacetFactory.sample({ id: 'Biology', name: 'Biology', hits: 1 }),
    ],
    ageRanges: [
      FacetFactory.sample({ id: '5-12', name: '5 - 12', hits: 1 }),
      FacetFactory.sample({ id: '12-14', name: '12 - 14', hits: 1 }),
    ],
  }),
);

ReactDOM.render(
  <React.StrictMode>
    <App apiClient={apiClient} />
  </React.StrictMode>,
  document.getElementById('root'),
);
