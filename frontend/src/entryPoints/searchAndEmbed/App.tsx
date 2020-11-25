import React from 'react';
import '../../index.less';
import { Video } from '@boclips-ui/video';
import { VideoCard } from '@boclips-ui/video-card';
import SearchView from '../../views/searchView';
import EmbedVideoButton from '../../components/embedVideoButton/EmbedVideoButton';
import AxiosWrapper from '../../service/axios/AxiosWrapper';
import getPlayer from '../../Player/getPlayer';

const renderVideoCard = (video: Video, loading: boolean, query: string) => (
  <VideoCard
    key={video.id}
    video={video}
    loading={loading}
    videoActionButtons={[
      <EmbedVideoButton
        videoId={video.id}
        onSubmit={(form) => form?.submit()}
      />,
    ]}
    authenticated
    hideBestFor
    hideAttachments
    videoPlayer={getPlayer(query, video)}
  />);

const App = () => (
  <SearchView
    collapsibleFilters
    renderVideoCard={renderVideoCard}
    closableHeader
    useFullWidth
  />
);

export default AxiosWrapper(App);
