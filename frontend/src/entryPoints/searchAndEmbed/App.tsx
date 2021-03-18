import React from 'react';
import '../../index.less';
import { Video } from '@boclips-ui/video';
import SearchView from '../../views/searchView';
import EmbedVideoButton from '../../components/embedVideoButton/EmbedVideoButton';
import AxiosWrapper from '../../service/axios/AxiosWrapper';
import VideoCardWrapper from '../../components/videoCard/VideoCardWrapper';
import {User} from "boclips-api-client/dist/sub-clients/organisations/model/User";

const renderVideoCard = (video: Video, query: string, user: User | null) => (
  <VideoCardWrapper
    video={video}
    query={query}
    actions={[
      <EmbedVideoButton video={video} onSubmit={(form) => form?.submit()} />,
    ]}
  />
);

const App = () => (
  <SearchView
    collapsibleFilters
    renderVideoCard={renderVideoCard}
    closableHeader
    useFullWidth
  />
);

export default AxiosWrapper(App);
