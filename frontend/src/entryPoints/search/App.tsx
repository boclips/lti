import { hot } from 'react-hot-loader/root';
import React, { ReactElement } from 'react';
import { Video } from '@boclips-ui/video';
import { VideoCard } from '@boclips-ui/video-card';
import { Player } from 'boclips-player-react';
import SearchView from '../../views/searchView';
import '../../index.less';
import resolvePlayerOptions from '../../Player/resolvePlayerOptions';
import AxiosWrapper from '../../service/axios/AxiosWrapper';
import CopyVideoLinkButtonFactory from '../../components/copyVideoLinkButton/CopyVideoLinkButtonFactory';

const renderVideoCard = (video: Video, loading: boolean, query: string) => {
  const getVideoActionButtons = () => {
    const copyLinkButton = CopyVideoLinkButtonFactory.getButton(video);
    return copyLinkButton ? [copyLinkButton] : [];
  };

  return (
    <VideoCard
      key={video.id}
      video={video}
      loading={loading}
      authenticated
      hideAttachments
      hideBestFor
      theme="lti"
      videoActionButtons={getVideoActionButtons()}
      videoPlayer={
        <Player
          options={resolvePlayerOptions(query)}
          videoUri={video.links?.self?.getOriginalLink()}
        />
      }
    />
  );
};

const App = (): ReactElement =>
  <SearchView renderVideoCard={renderVideoCard}/>;
export default hot(AxiosWrapper(App));
