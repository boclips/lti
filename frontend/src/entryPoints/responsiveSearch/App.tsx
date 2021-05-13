import { hot } from 'react-hot-loader/root';
import React from 'react';
import { VideoCardV3 } from '@boclips-ui/video-card-v3';
import { Video } from '@boclips-ui/video';
import { BoclipsClient } from 'boclips-api-client';
import ResponsiveSearchView from '../../views/responsiveSearchView';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';
import { FiltersProvider } from '../../hooks/useFilters';
import getPlayer from '../../Player/getPlayer';
import useBreakpoint from 'antd/lib/grid/hooks/useBreakpoint';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import { useDebouncedEffect } from '../../hooks/useDebouncedEffect';

export const renderResponsiveBaseVideoCard = (video: Video, query: string) => (
  <VideoCardV3
    duration={video.playback.duration.format('mm:ss')}
    title={video.title}
    video={video}
    videoPlayer={getPlayer(query!, video)}
  />
);

interface Props {
  apiClient: BoclipsClient;
}

const App = ({ apiClient }: Props) => {
  const currentBreakpoint = useMediaBreakPoint();

  React.useEffect(() => {
    apiClient.events.trackPageRendered({ url: window.location.href });
  }, [apiClient]);

  useDebouncedEffect(
    (isFirstRender) => {
      if (isFirstRender) {
        return;
      }

      apiClient.events.trackPageRendered({
        url: window.location.href,
        isResize: true,
      });
    },
    2000,
    [currentBreakpoint, apiClient],
  );

  return (
    <BoclipsClientProvider client={apiClient}>
      <FiltersProvider>
        <ResponsiveSearchView renderVideoCard={renderResponsiveBaseVideoCard} />
      </FiltersProvider>
    </BoclipsClientProvider>
  );
};

export default hot(App);
