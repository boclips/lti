import { hot } from 'react-hot-loader/root';

import React, { ReactElement } from 'react';
import { Video } from '@bit/boclips.dev-boclips-ui.types.video';
import { VideoCard } from '@bit/boclips.dev-boclips-ui.components.video-card';
import { Player } from 'boclips-player-react';
import SearchView from '../../views/searchView';
import '../../index.less';
import playerOptions from '../../Player/playerOptions';
import CopyVideoLinkButton from '../../components/copyVideoLinkButton/CopyVideoLinkButton';
import AxiosWrapper from '../../service/axios/AxiosWrapper';

// document.documentElement.style.setProperty('--titleHeaderTextColor', '#fff')

const renderVideoCard = (video: Video, loading: boolean) => (
  <VideoCard
    key={video.id}
    video={video}
    loading={loading}
    authenticated
    hideAttachments
    hideBestFor
    theme="lti"
    videoActionButtons={[<CopyVideoLinkButton video={video} />]}
    videoPlayer={
      <Player
        options={playerOptions}
        videoUri={video.links?.self?.getOriginalLink()}
      />
    }
  />
);

const App = (): ReactElement =>
  <SearchView renderVideoCard={renderVideoCard}/>;
export default hot(AxiosWrapper(App));
