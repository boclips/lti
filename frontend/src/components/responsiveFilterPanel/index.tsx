import React from 'react';
import { SelectOption } from '@boclips-ui/select-option';
import SelectFilter, { DropdownAligment } from '@boclips-ui/select';
import {
  Facet,
  VideoFacets,
} from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import s from './style.module.less';
import DurationConverter from './converters/DurationConverter';
import { useFilters } from '../../hooks/useFilters';

const MOBILE_BREAKPOINT = 'mobile';

interface Props {
  facets?: VideoFacets;
  // hidePanel?: boolean;
  // channelsList: Channel[];
  // subjectsList: Subject[];
}

const ResponsiveFilterPanel = ({ facets }: Props) => {
  // const [filterTouched, setFilterTouched] = useState<boolean>(false);
  const breakpoints = useMediaBreakPoint();
  const mobileView = breakpoints.type === MOBILE_BREAKPOINT;
  const { filters, setFilters } = useFilters();

  // const onClear = () => {
  //   setSubjectFilter([]);
  //   setDurationFilter([]);
  //   setAgeRangeFilter([]);
  //   setSourceFilter([]);
  //   onApply({
  //     ageRanges: [],
  //     source: [],
  //     subjects: [],
  //     duration: [],
  //   });
  // };

  const convertToSelectOptions = (rawFacets: Facet[] = []): SelectOption[] =>
    rawFacets.map((facet) => ({
      id: facet.id || '',
      label: facet.name || '',
      count: facet.hits,
    })) || [];

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

  return (
    <>
      <div className={s.filtersHeader}>
        {/* <span className={s.filtersTitle}>FILTER BY:</span> */}
        {/* {filterTouched && ( */}
        {/*  <Button className={s.clearAll} onClick={onClear} type="text"> */}
        {/*    CLEAR ALL */}
        {/*  </Button> */}
        {/* )} */}
      </div>
      <div className={s.selectFilters}>
        <SelectFilter
          relativePositionFilters={mobileView}
          options={ageRangeOptions}
          title="Age"
          updatedSelected={filters.ageRanges}
          onApply={(ageRanges) =>
            setFilters({
              ...filters,
              ageRanges,
            })
          }
          // touched={setFilterTouched}
          showFacets
        />
        <SelectFilter
          relativePositionFilters={mobileView}
          options={durationOptions}
          title="Duration"
          updatedSelected={filters.duration}
          onApply={(duration) =>
            setFilters({
              ...filters,
              duration,
            })
          }
          // touched={setFilterTouched}
          showFacets
        />
        <SelectFilter
          relativePositionFilters={mobileView}
          options={subjectOptions}
          title="Subject"
          updatedSelected={filters.subjects}
          onApply={(subjects) =>
            setFilters({
              ...filters,
              subjects,
            })
          }
          searchPlaceholder="Search for subject"
          allowSearch
          // touched={setFilterTouched}
          showFacets
        />
        <SelectFilter
          relativePositionFilters={mobileView}
          options={sourceOptions}
          title="Source"
          updatedSelected={filters.source}
          onApply={(source) =>
            setFilters({
              ...filters,
              source,
            })
          }
          searchPlaceholder="Search for source"
          allowSearch
          // touched={setFilterTouched}
          showFacets
          dropdownAlignment={DropdownAligment.RIGHT}
        />
      </div>
    </>
  );
};

export default ResponsiveFilterPanel;
