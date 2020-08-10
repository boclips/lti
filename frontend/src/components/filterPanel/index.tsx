import React, { useEffect, useState } from 'react';
import SelectFilter from '@bit/boclips.boclips-ui.components.select';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import s from './style.module.less';
import { Filters } from '../../types/filters';

interface Props {
  facets: VideoFacets | undefined;
  onApply: (filters: Filters) => void;
}

const FilterPanel = ({ facets, onApply }: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();

  useEffect(() => {
    if (ageRangeFilter) {
      onApply({ ageRanges: ageRangeFilter });
    }
  }, [ageRangeFilter]);

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
        </div>
      </div>
    </>);
};

export default FilterPanel;
