import React from 'react';
import '../../index.less';
import { Video } from '@boclips-ui/video';
import { VideoCard } from '@boclips-ui/video-card';
import { Player } from 'boclips-player-react';
import SearchView from '../../views/searchView';
import EmbedVideoButton from '../../components/embedVideoButton/EmbedVideoButton';
import resolvePlayerOptions from '../../Player/resolvePlayerOptions';
import AxiosWrapper from '../../service/axios/AxiosWrapper';

const renderVideoCard = (video: Video, loading: boolean, query: string) => (
  <VideoCard
    hideBadges
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
    videoPlayer={
      <Player
        options={resolvePlayerOptions(query)}
        videoUri={video.links?.self?.getOriginalLink()}
      />
    }
  />
);

const App = () => (
  <SearchView
    collapsibleFilters
    renderVideoCard={renderVideoCard}
    closableHeader
  />
);

export default AxiosWrapper(App);
