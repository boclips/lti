import React from 'react';
import '../../index.less';
import { Video } from '@bit/dev-boclips.boclips-ui.types.video';
import { VideoCard } from '@bit/dev-boclips.boclips-ui.components.video-card';
import { Player } from 'boclips-player-react';
import SearchView from '../../views/searchView';
import AxiosService from '../../service/axios/AxiosService';
import EmbedVideoButton from '../../components/embedVideoButton/EmbedVideoButton';

const renderVideoCard = (video: Video, loading: boolean) => (
  <VideoCard
    hideBadges
    key={video.id}
    video={video}
    loading={loading}
    videoActionButtons={
      <EmbedVideoButton
        videoId={video.id}
        onSubmit={(form) => form?.submit()}
      />
    }
    authenticated
    videoPlayer={<Player videoUri={video.links?.self?.getOriginalLink()} />}
  />
);

const App = () => {
  AxiosService.configureAxios();

  return <SearchView renderVideoCard={renderVideoCard} />;
};

export default App;
