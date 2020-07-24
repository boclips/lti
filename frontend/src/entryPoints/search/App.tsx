import React, { ReactElement } from 'react';
import { Video } from '@bit/boclips.boclips-ui.types.video';
import { VideoCard } from '@bit/boclips.boclips-ui.components.video-card';
import { Player } from 'boclips-player-react';
import AxiosService from '../../service/axios/AxiosService';
import SearchView from '../../views/searchView';
import '../../index.less';
import playerOptions from '../../Player/playerOptions';

const renderVideoCard = (video: Video, loading: boolean) => (
  <VideoCard
    hideBadges
    key={video.id}
    video={video}
    loading={loading}
    authenticated
    videoPlayer={
      <Player
        options={playerOptions}
        videoUri={video.links?.self?.getOriginalLink()}
      />
    }
  />
);

const App = (): ReactElement => {
  AxiosService.configureAxios();

  return <SearchView renderVideoCard={renderVideoCard} />;
};

export default App;
