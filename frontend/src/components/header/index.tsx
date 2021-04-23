import SearchBar from '@boclips-ui/search-bar';
import Button from '@boclips-ui/button';
import React, { useEffect, useState } from 'react';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import c from 'classnames';
import ResponsiveFilterPanel from '../responsiveFilterPanel';
import ArrowBackSVG from '../../resources/images/arrow-back.svg';
import FilterSVG from '../../resources/images/filter-icon.svg';
import s from './style.module.less';
import { useFilters } from '../../hooks/useFilters';

export const DESKTOP_BREAKPOINT = 'desktop';
export const MOBILE_BREAKPOINT = 'mobile';
export const TABLET_BREAKPOINT = 'tablet';

interface Props {
  onSearch: (query: string, page: number) => void;
  facets?: VideoFacets;
}

const Header = ({ onSearch, facets }: Props) => {
  const [showFilters, setShowFilters] = useState<boolean>(false);
  const [displayFilterButton, setDisplayFilterButton] = useState<boolean>(
    false,
  );
  const { filters } = useFilters();

  const currentBreakpoint = useMediaBreakPoint();
  const desktopView = currentBreakpoint.type === DESKTOP_BREAKPOINT;
  const mobileView = currentBreakpoint.type === MOBILE_BREAKPOINT;
  const tabletView = currentBreakpoint.type === TABLET_BREAKPOINT;

  const numberOfFiltersApplied = Object.entries(filters)
    .map(([, value]) => value)
    .flat().length;

  useEffect(() => {
    if (showFilters && !desktopView) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }, [desktopView, showFilters]);

  const handleSearch = (query, page) => {
    setDisplayFilterButton(true);
    onSearch(query, page);
  };

  return (
    <div className={s.header}>
      <div className={s.searchBarWrapper}>
        <SearchBar
          onSearch={handleSearch}
          iconOnlyButton={tabletView || mobileView}
        />
      </div>
      {displayFilterButton && (
        <div
          className={c(s.filterButtonWrapper, {
            [s.activeButton]: showFilters,
          })}
        >
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
            suffix={
              numberOfFiltersApplied > 0 ? (
                <div className={s.numberOfFilters}>
                  {numberOfFiltersApplied}
                </div>
              ) : undefined
            }
          />
        </div>
      )}
      <div className={c(s.filtersWrapper, { [s.hideFilters]: !showFilters })}>
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

        <ResponsiveFilterPanel
          facets={facets}
          setShowFilters={setShowFilters}
        />
      </div>
    </div>
  );
};

export default Header;
