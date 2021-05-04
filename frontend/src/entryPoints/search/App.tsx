import { hot } from 'react-hot-loader/root';
import React, { ReactElement } from 'react';
import { Video } from '@boclips-ui/video';
import { BoclipsClient } from 'boclips-api-client';
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

interface Props {
  apiClient: BoclipsClient;
}

const App = ({ apiClient }: Props): ReactElement => {
  React.useEffect(() => {
    apiClient.events.trackPageRendered({ url: window.location.href });
  }, [apiClient]);

  return <SearchView apiClient={apiClient} renderVideoCard={renderVideoCard} />;
};

export default hot(AxiosWrapper(App));
