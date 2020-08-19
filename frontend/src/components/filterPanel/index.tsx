import React, { useEffect, useState } from 'react';
import SelectFilter from '@bit/boclips.boclips-ui.components.select';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import c from 'classnames';
import { Button } from 'antd';
import s from './style.module.less';
import { Filters } from '../../types/filters';
import DurationConverter from './converters/DurationConverter';
import { AppliedFilters } from '../appliedFilters';

interface Props {
  facets?: VideoFacets;
  onApply: (filters: Filters) => void;
  subjects?: Subject[];
  sources?: Channel[];
  hidePanel?: boolean;
}

const FilterPanel = ({
  facets,
  onApply,
  subjects,
  sources,
  hidePanel,
}: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();
  const [durationFilter, setDurationFilter] = useState<string[]>();
  const [subjectFilter, setSubjectFilter] = useState<string[]>();
  const [sourceFilter, setSourceFilter] = useState<string[]>();
  const [clearFilterCount, setClearFilterCount] = useState<boolean>(false);
  const [filterTouched, setFilterTouched] = useState<boolean>(false);

  useEffect(() => {
    if (durationFilter || (durationFilter && durationFilter!.length === 0)) {
      onApply({ duration: durationFilter });
    }
  }, [durationFilter]);

  useEffect(() => {
    if (subjectFilter || (subjectFilter && subjectFilter!.length === 0)) {
      onApply({ subjects: subjectFilter });
    }
  }, [subjectFilter]);

  useEffect(() => {
    if (sourceFilter || (sourceFilter && sourceFilter!.length === 0)) {
      onApply({ source: sourceFilter });
    }
  }, [sourceFilter]);

  useEffect(() => {
    if (ageRangeFilter || (ageRangeFilter && ageRangeFilter!.length === 0)) {
      onApply({ ageRanges: ageRangeFilter });
    }
  }, [ageRangeFilter]);

  useEffect(() => {
    if (clearFilterCount) {
      setClearFilterCount(!clearFilterCount);
    }
  }, [clearFilterCount]);

  const onClear = () => {
    setFilterTouched(false);
    setClearFilterCount(true);
    onApply({
      ageRanges: [],
      source: [],
      subjects: [],
      duration: [],
    });
  };

  const ageRangeOptions: SelectOption[] = Object.keys(facets?.ageRanges!).map(
    (it) => ({
      id: it,
      label: it === '16-99' ? '16+' : it.replace('-', ' - '),
      count: facets?.ageRanges[it].hits,
    }),
  );

  const subjectOptions: SelectOption[] = Object.keys(facets?.subjects!).map(
    (it) => {
      const subject = subjects?.find((item) => item.id === it);
      return {
        id: subject?.id || it,
        label: subject?.name || it,
        count: facets?.subjects[it].hits,
      };
    },
  );

  const durationOptions: SelectOption[] = DurationConverter.toSelectOptions(
    facets?.durations!,
  );

  const getSubjectOptions = (subjectId:String) => {
    const subject = subjects?.find((item) => item.id === subjectId);
    return subject?.name;
  };

  const subjectFilterLabels = (subjectFilter!)?.map((subjectId) => getSubjectOptions(subjectId));

  const durationLabels = (durationFilter!)?.map((duration) =>
    DurationConverter.getLabelFromIso(duration));

  const sourceOptions:SelectOption[] = sources?.map((it) => ({
    id: it.name,
    label: it.name,
    count: 1,
  })) || [];

  return (
    <>
      <div
        className={c({
          [s.filters]: true,
          [s.hideFilters]: hidePanel,
        })}
      >
        <div className={s.filtersHeader}>
          <span className={s.filtersTitle}>FILTER BY:</span>
          {filterTouched && (
            <Button className={s.clearAll} onClick={onClear} type="text">
              CLEAR ALL
            </Button>
          )}
        </div>
        <div className={s.filtersWrapper}>
          <SelectFilter
            options={ageRangeOptions}
            title="Age"
            onApply={setAgeRangeFilter}
            clearCount={clearFilterCount}
            touched={setFilterTouched}
            showFacets
          />
          <SelectFilter
            options={durationOptions}
            title="Duration"
            onApply={setDurationFilter}
            clearCount={clearFilterCount}
            touched={setFilterTouched}
            showFacets
          />
          <SelectFilter
            options={subjectOptions!}
            title="Subject"
            onApply={setSubjectFilter}
            clearCount={clearFilterCount}
            searchPlaceholder="Search for subject"
            allowSearch
            touched={setFilterTouched}
            showFacets
          />
          <SelectFilter
            options={sourceOptions!}
            title="Source"
            clearCount={clearFilterCount}
            onApply={setSourceFilter}
            searchPlaceholder="Search for source"
            allowSearch
            touched={setFilterTouched}
          />
        </div>
        {filterTouched && (
          <>
            <div>Filters applied</div>
            {ageRangeFilter && <AppliedFilters type="Ages" values={ageRangeFilter} onClick={(value) => setAgeRangeFilter(value)} />}
            {subjectFilter && <AppliedFilters type="Subjects" values={subjectFilterLabels} onClick={(value) => setSubjectFilter(value)} />}
            {sourceFilter && <AppliedFilters type="Sources" values={sourceFilter} onClick={(value) => setSourceFilter(value)} />}
            {durationFilter && <AppliedFilters type="Durations" values={durationLabels} onClick={(value) => setDurationFilter(value)} />}
          </>)}
      </div>
    </>
  );
};

export default FilterPanel;
