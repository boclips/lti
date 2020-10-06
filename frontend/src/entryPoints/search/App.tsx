import { hot } from 'react-hot-loader/root';

import React, { ReactElement } from 'react';
import { Video } from '@boclips-ui/video';
import { VideoCard } from '@boclips-ui/video-card';
import { Player } from 'boclips-player-react';
import SearchView from '../../views/searchView';
import '../../index.less';
import playerOptions from '../../Player/playerOptions';
import CopyVideoLinkButton from '../../components/copyVideoLinkButton/CopyVideoLinkButton';
import AxiosWrapper from '../../service/axios/AxiosWrapper';

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
