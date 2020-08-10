import React, { useEffect, useState } from 'react';
import SelectFilter from '@bit/boclips.boclips-ui.components.select';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import s from './style.module.less';
import { Filters } from '../../types/filters';
import DurationConverter from './converters/DurationConverter';
import Range from '../../types/range';

const durationFilterOptions: Range[] = [
    { min: 0, max: 120 },
    { min: 120, max: 300 },
    { min: 300, max: 600 },
    { min: 600, max: 1200 },
    { min: 1200, max: 86400 },
  ];

interface Props {
  facets: VideoFacets | undefined;
  onApply: (filters: Filters) => void;
}

const FilterPanel = ({ facets, onApply }: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();
  const [durationFilter, setDurationFilter] = useState<string[]>();

  useEffect(() => {
    if (ageRangeFilter) {
      onApply({ ageRanges: ageRangeFilter });
    }
  }, [ageRangeFilter]);

  useEffect(() => {
    if (durationFilter) {
      onApply({ duration: durationFilter });
    }
  }, [durationFilter]);

  const ageRangeArray: string[] = Object.keys(facets?.ageRanges!);
  const ageRangeOptions: SelectOption[] = ageRangeArray.map((it) => ({
    id: it,
    label: it,
    count: 0
  }));

  return (
    <>
      <div className={s.filters}>
        <div className={s.filtersTitle}>
          FILTER BY:
        </div>
        <div className={s.filtersWrapper}>
          <SelectFilter
            options={ageRangeOptions}
            title="Age"
            onApply={setAgeRangeFilter}
          />
          <SelectFilter
            options={DurationConverter.convertToSelectOptions(
              facets?.durations!!,
              durationFilterOptions
            )}
            title="Duration"
            onApply={setDurationFilter}
          />
        </div>
      </div>
    </>);
};

export default FilterPanel;