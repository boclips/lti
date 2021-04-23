import { List } from 'antd';
import React from 'react';
import { Video } from '@boclips-ui/video';
import VideoCardsPlaceholder from '@boclips-ui/video-card-placeholder';
import c from 'classnames';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import s from './style.module.less';
import { MOBILE_BREAKPOINT } from '../header';
import { PaginationButtons } from '../responsivePagination';
import HappyGuySVG from '../../resources/images/happy-guy.svg';

interface Props {
  onSearch: (query: string, page: number) => void;
  setCurrentPage: (page: number) => void;
  results: Video[];
  searchQuery: string;
  totalVideoElements: number;
  currentPage: number;
  loading: boolean;
  renderVideoCard: (video: Video, query: string) => React.ReactNode;
}

const SearchResults = ({
  onSearch,
  setCurrentPage,
  results,
  searchQuery,
  totalVideoElements,
  currentPage,
  loading,
  renderVideoCard,
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
          locale={{
            emptyText: (
              <div className={s.empty}>
                <HappyGuySVG />
              </div>
            ),
          }}
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
            <div className={s.videoCardWrapper} key={video.id}>
              {renderVideoCard(video, searchQuery!)}
            </div>
          )}
        />
      )}
    </div>
  );
};

export default SearchResults;
