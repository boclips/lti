import React, { ReactElement } from 'react';
import { Video } from '@bit/dev-boclips.boclips-ui.types.video';
import { VideoCard } from '@bit/dev-boclips.boclips-ui.components.video-card';
import { Player } from 'boclips-player-react';
import AuthService from '../../service/auth/AuthService';
import SearchView from '../../views/searchView';
import '../../index.less';

const renderVideoCard = (video: Video, loading: boolean) => (
  <VideoCard
    hideBadges
    key={video.id}
    video={video}
    loading={loading}
    authenticated
    videoPlayer={
      <Player videoUri={video.links?.self?.getOriginalLink()} />
    }
  />
);

const App = (): ReactElement => {
  AuthService.configureAxios();

  return (
    <SearchView renderVideoCard={renderVideoCard}/>  
  );
};

export default App;
