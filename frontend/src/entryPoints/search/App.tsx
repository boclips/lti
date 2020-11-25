import { hot } from 'react-hot-loader/root';
import React, { ReactElement } from 'react';
import { Video } from '@boclips-ui/video';
import { VideoCard } from '@boclips-ui/video-card';
import SearchView from '../../views/searchView';
import '../../index.less';
import AxiosWrapper from '../../service/axios/AxiosWrapper';
import CopyVideoLinkButtonFactory from '../../components/copyVideoLinkButton/CopyVideoLinkButtonFactory';
import getPlayer from '../../Player/getPlayer';

const renderVideoCard = (video: Video, loading: boolean, query: string) => {
  const getVideoActionButtons = () => {
    const copyLinkButton = CopyVideoLinkButtonFactory.getButton(video);
    return copyLinkButton ? [copyLinkButton] : [];
  };

  return (
    <div style={{ marginBottom: '4px' }}>
      <VideoCard
        key={video.id}
        video={video}
        loading={loading}
        authenticated
        hideAttachments
        hideBestFor
        theme="lti"
        videoActionButtons={getVideoActionButtons()}
        videoPlayer={getPlayer(query, video)}
      />
    </div>
  );
};

const App = (): ReactElement =>
  <SearchView renderVideoCard={renderVideoCard}/>;
export default hot(AxiosWrapper(App));
