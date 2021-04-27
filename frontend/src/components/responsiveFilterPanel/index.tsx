import React, { Dispatch, ReactElement, SetStateAction } from 'react';
import { SelectOption } from '@boclips-ui/select-option';
import SelectFilter, { DropdownAligment } from '@boclips-ui/select';
import {
  Facet,
  VideoFacets,
} from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import Button from '@boclips-ui/button';
import s from './style.module.less';
import DurationConverter from './converters/DurationConverter';
import { useFilters } from '../../hooks/useFilters';
import InputPrefixIcon from '../../resources/images/search-icon.svg';
import useFeatureFlags from '../../hooks/useFeatureFlags';

const MOBILE_BREAKPOINT = 'mobile';
const TABLET_BREAKPOINT = 'tablet';

interface Props {
  facets?: VideoFacets;
  setShowFilters: Dispatch<SetStateAction<boolean>>;
}

const convertToSelectOptions = (rawFacets: Facet[] = []): SelectOption[] =>
  rawFacets?.map((facet) => ({
    id: facet.id || '',
    label: facet.name || '',
    count: facet.hits,
  })) || [];

const ResponsiveFilterPanel = ({
  facets,
  setShowFilters,
}: Props): ReactElement => {
  const breakpoints = useMediaBreakPoint();
  const mobileView = breakpoints.type === MOBILE_BREAKPOINT;
  const tabletView = breakpoints.type === TABLET_BREAKPOINT;

  const relativePositionFilters = mobileView || tabletView;

  const { filters, setFilters, areFiltersApplied, clearFilters } = useFilters();

  const ageRangeOptions: SelectOption[] =
    facets?.ageRanges?.map((facet) => ({
      id: facet.id,
      label: facet.name === '16-99' ? '16+' : facet.name.replace('-', ' - '),
      count: facet.hits,
    })) || [];

  const subjectOptions: SelectOption[] =
    facets?.subjects?.map((facet) => ({
      id: facet.id,
      label: facet.name,
      count: facet.hits,
    })) || [];

  const durationOptions: SelectOption[] = DurationConverter.toSelectOptions(
    // eslint-disable-next-line
    facets?.durations!,
  );

  const sourceOptions: SelectOption[] = convertToSelectOptions(
    facets?.channels,
  );

  const showAgeFilter = useFeatureFlags()?.LTI_AGE_FILTER;

  return (
    <>
      <div className={s.selectFilters}>
        {showAgeFilter && (
          <SelectFilter
            relativePositionFilters={relativePositionFilters}
            options={ageRangeOptions}
            title="Age"
            updatedSelected={filters.ageRanges}
            onApply={(ageRanges) =>
              setFilters({
                ...filters,
                ageRanges,
              })
            }
            showFacets
          />
        )}
        <SelectFilter
          relativePositionFilters={relativePositionFilters}
          options={durationOptions}
          title="Duration"
          updatedSelected={filters.duration}
          onApply={(duration) =>
            setFilters({
              ...filters,
              duration,
            })
          }
          showFacets
        />
        <SelectFilter
          relativePositionFilters={relativePositionFilters}
          options={subjectOptions}
          title="Subject"
          updatedSelected={filters.subjects}
          onApply={(subjects) =>
            setFilters({
              ...filters,
              subjects,
            })
          }
          searchPlaceholder="Search..."
          allowSearch
          showFacets
          inputPrefixIcon={<InputPrefixIcon />}
        />
        <SelectFilter
          relativePositionFilters={relativePositionFilters}
          options={sourceOptions}
          title="Source"
          updatedSelected={filters.source}
          onApply={(source) =>
            setFilters({
              ...filters,
              source,
            })
          }
          searchPlaceholder="Search..."
          allowSearch
          showFacets
          dropdownAlignment={DropdownAligment.RIGHT}
          inputPrefixIcon={<InputPrefixIcon />}
        />
      </div>
      {areFiltersApplied && (
        <div className={s.buttonsWrapper}>
          <Button
            type="outline"
            text="Clear filters"
            onClick={() => clearFilters()}
          />

          <Button text="Apply" onClick={() => setShowFilters(false)} />
        </div>
      )}
    </>
  );
};

export default ResponsiveFilterPanel;
