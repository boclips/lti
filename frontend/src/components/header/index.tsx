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
import AppliedFiltersPanel from '../appliedFiltersPanel';

export const DESKTOP_BREAKPOINT = 'desktop';

interface Props {
  onSearch: (query: string, page: number) => void;
  facets?: VideoFacets;
  filters: { [key: string]: string[] };
  setSingleFilter: any;
}

const Header = ({ onSearch, facets, setSingleFilter, filters }: Props) => {
  const [showFilters, setShowFilters] = useState<boolean>(false);

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
            <div> Filters</div>
          </div>
          <div className={s.filters}>
            <ResponsiveFilterPanel facets={facets} onApply={setSingleFilter} />
          </div>
        </div>
      )}
    </div>
  );
};

export default Header;
