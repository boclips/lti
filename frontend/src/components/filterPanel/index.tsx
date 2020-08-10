import React, { useEffect, useState } from 'react';
import SelectFilter from '@bit/boclips.dev-boclips-ui.components.select';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import s from './style.module.less';
import { Filters } from '../../types/filters';

interface Props {
  facets: VideoFacets | undefined;
  onApply: (filters: Filters) => void;
  subjects: Subject[];
}

const FilterPanel = ({ facets, onApply, subjects }: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();
  const [subjectFilter, setSubjectFilter] = useState<string[]>();

  useEffect(() => {
    if (ageRangeFilter) {
      onApply({ ageRanges: ageRangeFilter });
    }
  }, [ageRangeFilter]);

  useEffect(() => {
    if (subjectFilter) {
      onApply({ subjects: subjectFilter });
    }
  }, [subjectFilter]);

  const ageRangeArray: string[] = Object.keys(facets?.ageRanges!);
  const ageRangeOptions: SelectOption[] = ageRangeArray.map((it) => ({
    id: it,
    label: it,
    count: 0,
  }));

  const subjectOptions = subjects.map((it) => ({
    id: it.id,
    label: it.name,
    count: 0,
  }));

  return (
    <>
      <div className={s.filters}>
        <div className={s.filtersTitle}>FILTER BY:</div>
        <div className={s.filtersWrapper}>
          <SelectFilter
            options={ageRangeOptions}
            title="Age"
            onApply={setAgeRangeFilter}
          />
          <SelectFilter
            options={subjectOptions}
            title="Subject"
            onApply={setSubjectFilter}
            searchPlaceholder="Search for subject"
            allowSearch

          />
        </div>
      </div>
    </>
  );
};

export default FilterPanel;
