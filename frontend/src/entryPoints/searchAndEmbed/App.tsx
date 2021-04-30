import React from 'react';
import '../../index.less';
import { Video } from '@boclips-ui/video';
import SearchView from '../../views/searchView';
import EmbedVideoButton from '../../components/embedVideoButton/EmbedVideoButton';
import { AxiosWrapper } from '../../service/axios/AxiosWrapper';
import VideoCardWrapper from '../../components/videoCard/VideoCardWrapper';

const renderVideoCard = (
  video: Video,
  query: string,
  showVideoCardV3: boolean,
) => (
  <VideoCardWrapper
    video={video}
    query={query}
    actions={[
      <EmbedVideoButton video={video} onSubmit={(form) => form?.submit()} />,
    ]}
    showVideoCardV3={showVideoCardV3}
  />
);

const App = ({ apiClient }) => {
  return (
    <SearchView
      apiClient={apiClient}
      collapsibleFilters
      renderVideoCard={renderVideoCard}
      closableHeader
      useFullWidth
    />
  );
};

export default AxiosWrapper(App);
