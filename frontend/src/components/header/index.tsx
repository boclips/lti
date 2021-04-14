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
export const MOBILE_BREAKPOINT = 'mobile';
export const TABLET_BREAKPOINT = 'tablet';

interface Props {
  onSearch: (query: string, page: number) => void;
  facets?: VideoFacets;
}

const Header = ({ onSearch, facets }: Props) => {
  const [showFilters, setShowFilters] = useState<boolean>(false);

  const currentBreakpoint = useMediaBreakPoint();
  const desktopView = currentBreakpoint.type === DESKTOP_BREAKPOINT;
  const mobileView = currentBreakpoint.type === MOBILE_BREAKPOINT;
  const tabletView = currentBreakpoint.type === TABLET_BREAKPOINT;

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
        <SearchBar
          onSearch={onSearch}
          iconOnlyButton={tabletView || mobileView}
        />
      </div>
      <div className={s.filterButtonWrapper}>
        <Button
          aria-label="filter"
          dataQa="filter-button"
          onClick={() => {
            setShowFilters(!showFilters);
          }}
          type="outline"
          icon={<FilterSVG />}
          iconOnly={mobileView}
          text={mobileView ? undefined : 'Filters'}
          width={mobileView ? '48px' : 'auto'}
          height="48px"
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
