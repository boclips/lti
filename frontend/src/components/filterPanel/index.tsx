import React, { useEffect, useState } from 'react';
import SelectFilter from '@bit/boclips.boclips-ui.components.select';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import s from './style.module.less';
import { Filters } from '../../types/filters';

interface Props {
  facets: VideoFacets | undefined;
  onApply: (filters: Filters) => void;
}

const FilterPanel = ({ facets, onApply }: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>([]);

  useEffect(() => {
    onApply({ ageRanges: ageRangeFilter });
  }, [ageRangeFilter]);

  return (
    <>
      <div className={s.filters}>
        <div className={s.filtersTitle}>
          FILTER BY:
        </div>
        <div className={s.filtersWrapper}>
          {facets?.ageRanges && (
            <SelectFilter
              options={facets?.ageRanges!}
              title="Age"
              onApply={setAgeRangeFilter}
            />
          )}
        </div>
      </div>
    </>);
};

export default FilterPanel;
