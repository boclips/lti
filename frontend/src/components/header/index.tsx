import SearchBar from '@boclips-ui/search-bar';
import Button from '@boclips-ui/button';
import React, { useEffect, useState } from 'react';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import ResponsiveFilterPanel from '../responsiveFilterPanel';
import ArrowBackSVG from '../../resources/images/arrow-back.svg';
import FilterSVG from '../../resources/images/filter-icon.svg';
import s from './style.module.less';

export const DESKTOP_BREAKPOINT = 'desktop';

interface Props {
  onSearch: (query: string, page: number) => void;
  facets?: VideoFacets;
}

const Header = ({ onSearch, facets }: Props) => {
  const [showFilters, setShowFilters] = useState<boolean>(false);

  const breakpoints = useMediaBreakPoint();
  const desktopView = breakpoints.type === DESKTOP_BREAKPOINT;

  useEffect(() => {
    if (showFilters && !desktopView) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }, [desktopView, showFilters]);

  return (
    <div className={s.header}>
      <div className={s.searchBarWrapper}>
        <SearchBar theme="publishers" onSearch={onSearch} />
      </div>
      <div className={s.filterButtonWrapper}>
        <Button
          dataQa="filter-button"
          onClick={() => {
            setShowFilters(!showFilters);
          }}
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
              role="presentation"
              className={s.backButton}
              onClick={() => setShowFilters(!showFilters)}
            >
              <ArrowBackSVG />
            </div>
            <div> Filters</div>
          </div>
          <div className={s.filters}>
            <ResponsiveFilterPanel facets={facets} />
          </div>
        </div>
      )}
    </div>
  );
};

export default Header;
