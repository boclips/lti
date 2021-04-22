import { List } from 'antd';
import React from 'react';
import { Video } from '@boclips-ui/video';
import { VideoCardV3 } from '@boclips-ui/video-card-v3';
import VideoCardsPlaceholder from '@boclips-ui/video-card-placeholder';
import c from 'classnames';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import EmptyList from '../EmptyList';
import getPlayer from '../../Player/getPlayer';
import s from './style.module.less';
import { ResponsiveEmbedVideoButton } from '../responsiveEmbedVideoButton/responsiveEmbedVideoButton';
import { MOBILE_BREAKPOINT } from '../header';
import { PaginationButtons } from '../responsivePagination';

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

  const currentBreakpoint = useMediaBreakPoint();
  const mobileView = currentBreakpoint.type === MOBILE_BREAKPOINT;

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
            className: c(s.pagination, {
              [s.paginationEmpty]: !results.length,
            }),
            pageSize: 10,
            showSizeChanger: false,
            onChange: (page) => {
              scrollToTop();
              onSearch(searchQuery, page - 1);
              setCurrentPage(page);
            },
            current: currentPage,
            itemRender: (current, type, _originalElement) =>
              PaginationButtons(
                current,
                type,
                _originalElement,
                mobileView,
                currentPage,
              ),
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
                actions={[
                  <ResponsiveEmbedVideoButton
                    video={video}
                    onSubmit={(form) => form?.submit()}
                  />,
                ]}
              />
            </div>
          )}
        />
      )}
    </div>
  );
};

export default SearchResults;
