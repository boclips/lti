import React, { useEffect, useState } from 'react';
import SelectFilter from '@bit/boclips.boclips-ui.components.select';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import c from 'classnames';
import s from './style.module.less';
import { Filters } from '../../types/filters';
import DurationConverter from './converters/DurationConverter';

interface Props {
  facets: VideoFacets | undefined;
  onApply: (filters: Filters) => void;
  subjects?: Subject[];
  sources?: Channel[];
  hidePanel?: boolean;
}

const FilterPanel = ({
  facets, onApply, subjects, sources, hidePanel
}: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();
  const [durationFilter, setDurationFilter] = useState<string[]>();
  const [subjectFilter, setSubjectFilter] = useState<string[]>();
  const [sourceFilter, setSourceFilter] = useState<string[]>();

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

  useEffect(() => {
    if (subjectFilter) {
      onApply({ subjects: subjectFilter });
    }
  }, [subjectFilter]);

  useEffect(() => {
    if (sourceFilter) {
      onApply({ source: sourceFilter });
    }
  }, [sourceFilter]);

  const ageRangeArray: string[] = Object.keys(facets?.ageRanges!);
  const ageRangeOptions: SelectOption[] = ageRangeArray.map((it) => ({
    id: it,
    label: it,
    count: 0,
  }));

  const subjectOptions = subjects?.map((it) => ({
    id: it.id,
    label: it.name,
    count: 0,
  }));

  const sourceOptions = sources?.map((it) => ({
    id: it.name,
    label: it.name,
    count: 0,
  }));

  return (
    <>
      <div className={c({
        [s.filters]: true,
        [s.hideFilters]: hidePanel
      })}>
        <div className={s.filtersTitle}>FILTER BY:</div>
        <div className={s.filtersWrapper}>
          <SelectFilter
            options={ageRangeOptions}
            title="Age"
            onApply={setAgeRangeFilter}
          />
          <SelectFilter
            options={DurationConverter.toSelectOptions(facets?.durations!!)}
            title="Duration"
            onApply={setDurationFilter}
          />
          <SelectFilter
            options={subjectOptions || []}
            title="Subject"
            onApply={setSubjectFilter}
            searchPlaceholder="Search for subject"
            allowSearch
          />
          <SelectFilter
            options={sourceOptions || []}
            title="Source"
            onApply={setSourceFilter}
            searchPlaceholder="Search for source"
            allowSearch
          />
        </div>
      </div>
    </>
  );
};

export default FilterPanel;
