import { hot } from 'react-hot-loader/root';
import React from 'react';
import { VideoCardV3 } from '@boclips-ui/video-card-v3';
import { Video } from '@boclips-ui/video';
import ResponsiveSearchView from '../../views/responsiveSearchView';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';
import { FiltersProvider } from '../../hooks/useFilters';
import getPlayer from '../../Player/getPlayer';

export const renderResponsiveBaseVideoCard = (video: Video, query: string) => (
  <VideoCardV3
    duration={video.playback.duration.format('mm:ss')}
    title={video.title}
    video={video}
    videoPlayer={getPlayer(query!, video)}
  />
);

const App = ({ apiClient }) => {
  return (
    <BoclipsClientProvider client={apiClient}>
      <FiltersProvider>
        <ResponsiveSearchView renderVideoCard={renderResponsiveBaseVideoCard} />
      </FiltersProvider>
    </BoclipsClientProvider>
  );
};

export default hot(App);
