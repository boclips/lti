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
  facets?: VideoFacets;
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
    if (durationFilter) {
      onApply({ duration: durationFilter });
    }
    if (subjectFilter) {
      onApply({ subjects: subjectFilter });
    }
    if (sourceFilter) {
      onApply({ source: sourceFilter });
    }
  }, [ageRangeFilter, durationFilter, subjectFilter, sourceFilter]);
 
  const ageRangeOptions: SelectOption[] = Object.keys(facets?.ageRanges!).map((it) => ({
    id: it,
    label: it === '16-99' ? '16+' : it.replace('-', ' - '),
    count: facets?.ageRanges[it].hits,
  }));

  const subjectOptions:SelectOption[] = Object.keys(facets?.subjects!).map((it) => {
    const subject = subjects?.find((item) => item.id === it);
    return {
      id: subject?.id || it,
      label: subject?.name || it,
      count: facets?.subjects[it].hits,
    };
  });

  const durationOptions:SelectOption[] = DurationConverter.toSelectOptions(facets?.durations!);

  const sourceOptions:SelectOption[] = Object.keys(facets?.resourceTypes!)?.map((it) => {
    const source = sources?.find((item) => item.name === it);
    return {
      id: source?.id || it,
      label: source?.name || it,
      count: facets?.resourceTypes[it].hits,
    };
  });

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
            options={durationOptions}
            title="Duration"
            onApply={setDurationFilter}
          /> 
          <SelectFilter
            options={subjectOptions!}
            title="Subject"
            onApply={setSubjectFilter}
            searchPlaceholder="Search for subject"
            allowSearch
          />
          <SelectFilter
            options={sourceOptions!}
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
