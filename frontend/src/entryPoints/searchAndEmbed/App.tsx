import { hot } from 'react-hot-loader/root';
import React from 'react';
import VideoCardV3 from '@boclips-ui/video-card-v3';
import { Video } from '@boclips-ui/video';
import { BoclipsClient } from 'boclips-api-client';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import ResponsiveSearchView from '../../views/responsiveSearchView';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';
import { FiltersProvider } from '../../hooks/useFilters';
import getPlayer from '../../Player/getPlayer';
import { EmbedVideoButton } from '../../components/embedVideoButton/embedVideoButton';
import { useDebouncedEffect } from '../../hooks/useDebouncedEffect';

const renderVideoCard = (video: Video, query: string) => (
  <VideoCardV3
    duration={video.playback.duration.format('mm:ss')}
    title={video.title}
    video={video}
    videoPlayer={getPlayer(query, video)}
    actions={[
      <EmbedVideoButton video={video} onSubmit={(form) => form?.submit()} />,
    ]}
  />
);

interface Props {
  apiClient: BoclipsClient;
}

const App = ({ apiClient }: Props) => {
  const currentBreakpoint = useMediaBreakPoint();

  useDebouncedEffect(
    (isFirstRender) => {
      apiClient.events.trackPageRendered({
        url: window.location.href,
        isResize: !isFirstRender,
      });
    },
    1000,
    [currentBreakpoint, apiClient],
  );

  return (
    <BoclipsClientProvider client={apiClient}>
      <FiltersProvider>
        <ResponsiveSearchView renderVideoCard={renderVideoCard} />
      </FiltersProvider>
    </BoclipsClientProvider>
  );
};

export default hot(App);
