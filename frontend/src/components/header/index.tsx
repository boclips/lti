import s from './style.module.less';
import SearchBar from '@boclips-ui/search-bar';
import Button from '@boclips-ui/button';
import FilterSVG from '../../resources/images/filter-icon.svg';
import ArrowBackSVG from '../../resources/images/arrow-back.svg';
import React, { useEffect, useState } from 'react';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import ResponsiveFilterPanel from '../responsiveFilterPanel';
import { useBoclipsClient } from '../../hooks/useBoclipsClient';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';

const DESKTOP_BREAKPOINT = 'desktop';

interface Props {
  onSearch: (query: string, page: number) => void;
  facets?: VideoFacets;
  setSingleFilter: any;
}

const Header = ({ onSearch, facets, setSingleFilter }: Props) => {
  const [channelsList, setChannelsList] = useState<Channel[]>([]);
  const [subjectsList, setSubjectsList] = useState<Subject[]>([]);

  const [showFilters, setShowFilters] = useState<boolean>(false);

  const apiClient = useBoclipsClient();

  useEffect(() => {
    apiClient.subjects.getAll().then((subjects) => setSubjectsList(subjects));

    apiClient.channels.getAll().then((channels) => setChannelsList(channels));
  }, [setChannelsList, setSubjectsList, apiClient]);

  const breakpoints = useMediaBreakPoint();
  const desktopView = breakpoints.type === DESKTOP_BREAKPOINT;

  useEffect(() => {
    if (showFilters && !desktopView) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }, [showFilters]);

  return (
    <div className={s.header}>
      <div className={s.searchBarWrapper}>
        <SearchBar theme="publishers" onSearch={onSearch} />
      </div>
      <div className={s.filterButtonWrapper}>
        <Button
          dataQa="filter-button"
          onClick={() => setShowFilters(!showFilters)}
          type="outline"
          icon={<FilterSVG />}
          iconOnly={!desktopView}
          text={desktopView ? 'Filters' : undefined}
          width={desktopView ? 'auto' : '48px'}
          height="100%"
          disabled={facets === undefined}
        />
      </div>
      {showFilters && (
        <div className={s.filtersWrapper}>
          <div className={s.mobileHeader}>
            <div
              className={s.backButton}
              onClick={() => setShowFilters(!showFilters)}
            >
              <ArrowBackSVG />
            </div>
            <div> Filters </div>
          </div>
          <div className={s.filters}>
            {channelsList && subjectsList && (
              <ResponsiveFilterPanel
                channelsList={channelsList}
                subjectsList={subjectsList}
                facets={facets}
                onApply={setSingleFilter}
              />
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Header;
