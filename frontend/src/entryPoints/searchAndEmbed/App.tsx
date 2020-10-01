import React from 'react';
import '../../index.less';
import { Video } from '@bit/boclips.dev-boclips-ui.types.video';
import { VideoCard } from '@bit/boclips.dev-boclips-ui.components.video-card';
import { Player } from 'boclips-player-react';
import SearchView from '../../views/searchView';
import EmbedVideoButton from '../../components/embedVideoButton/EmbedVideoButton';
import playerOptions from '../../Player/playerOptions';
import AxiosWrapper from '../../service/axios/AxiosWrapper';

const renderVideoCard = (video: Video, loading: boolean) => (
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
        options={playerOptions}
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
