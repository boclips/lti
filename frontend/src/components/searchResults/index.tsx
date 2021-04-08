import { List } from 'antd';
import React from 'react';
import EmptyList from '../EmptyList';
import { Video } from '@boclips-ui/video';
import getPlayer from '../../Player/getPlayer';
import { VideoCardV3 } from '@boclips-ui/video-card-v3';
import s from './style.module.less';
import VideoCardsPlaceholder from '@boclips-ui/video-card-placeholder';

interface Props {
  onSearch: (query: string, page: number) => void;
  setCurrentPage: (page: number) => void;
  results: Video[];
  searchQuery: string;
  totalVideoElements: number;
  currentPage: number;
  loading: boolean;
}

const SearchResults = ({
  onSearch,
  setCurrentPage,
  results,
  searchQuery,
  totalVideoElements,
  currentPage,
  loading,
}: Props) => {
  const scrollToTop = () => {
    window.scrollTo(0, 0);
  };

  return (
    <div className={s.body}>
      {loading ? (
        <VideoCardsPlaceholder />
      ) : (
        <List
          itemLayout="vertical"
          size="large"
          locale={{ emptyText: <EmptyList theme="lti" /> }}
          pagination={{
            total: totalVideoElements,
            pageSize: 10,
            showSizeChanger: false,
            onChange: (page) => {
              scrollToTop();
              onSearch(searchQuery, page - 1);
              setCurrentPage(page);
            },
            current: currentPage,
          }}
          dataSource={results}
          renderItem={(video: Video) => (
            <div className={s.videoCardWrapper}>
              <VideoCardV3
                duration={video.playback.duration.format('mm:ss')}
                title={video.title}
                key={video.id}
                video={video}
                videoPlayer={getPlayer(searchQuery!, video)}
              />
            </div>
          )}
        />
      )}
    </div>
  );
};

export default SearchResults;
