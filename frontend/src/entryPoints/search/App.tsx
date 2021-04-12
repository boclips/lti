import { hot } from 'react-hot-loader/root';
import React, { ReactElement } from 'react';
import { Video } from '@boclips-ui/video';
import SearchView from '../../views/searchView';
import '../../index.less';
import { AxiosWrapper } from '../../service/axios/AxiosWrapper';
import VideoCardWrapper from '../../components/videoCard/VideoCardWrapper';
import CopyVideoLinkButtonFactory from '../../components/copyVideoLinkButton/CopyVideoLinkButtonFactory';

const renderVideoCard = (
  video: Video,
  query: string,
  showVideoCardV3: boolean,
) => {
  const getVideoActionButtons = () => {
    const copyLinkButton = CopyVideoLinkButtonFactory.getButton(video);
    return copyLinkButton ? [copyLinkButton] : [];
  };

  return (
    <VideoCardWrapper
      video={video}
      query={query}
      actions={getVideoActionButtons()}
      showVideoCardV3={showVideoCardV3}
    />
  );
};

const App = (): ReactElement => (
  <SearchView renderVideoCard={renderVideoCard} />
);
export default hot(AxiosWrapper(App));
