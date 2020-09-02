import React, { useEffect, useState } from 'react';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import SelectFilter from '@bit/boclips.boclips-ui.components.select';
import c from 'classnames';
import { Button } from 'antd';
import s from './style.module.less';
import DurationConverter from './converters/DurationConverter';
import AppliedFiltersPanel from '../appliedFiltersPanel';
import { Filters } from '../../types/filters';

interface Props {
  facets?: VideoFacets;
  onApply: (filters: Filters) => void;
  subjects?: Subject[];
  hidePanel?: boolean;
}

const FilterPanel = ({
  facets,
  onApply,
  subjects,
  hidePanel,
}: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();
  const [durationFilter, setDurationFilter] = useState<string[]>();
  const [subjectFilter, setSubjectFilter] = useState<string[]>();
  const [sourceFilter, setSourceFilter] = useState<string[]>();
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

  const onClear = () => {
    setFilterTouched(false);
    setSubjectFilter([]);
    setDurationFilter([]);
    setAgeRangeFilter([]);
    setSourceFilter([]);
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

  const sourceOptions:SelectOption[] = Object.keys(facets?.channels || {}).map((it) => ({
    id: it,
    label: it,
    count: facets?.channels && facets?.channels[it].hits,
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
            updatedSelected={ageRangeFilter}
            onApply={setAgeRangeFilter}
            touched={setFilterTouched}
            showFacets
          />
          <SelectFilter
            options={durationOptions}
            title="Duration"
            updatedSelected={durationFilter}
            onApply={setDurationFilter}
            touched={setFilterTouched}
            showFacets
          />
          <SelectFilter
            options={subjectOptions!}
            title="Subject"
            onApply={setSubjectFilter}
            updatedSelected={subjectFilter}
            searchPlaceholder="Search for subject"
            allowSearch
            touched={setFilterTouched}
            showFacets
          />
          <SelectFilter
            options={sourceOptions!}
            title="Source"
            onApply={setSourceFilter}
            updatedSelected={sourceFilter}
            searchPlaceholder="Search for source"
            allowSearch
            touched={setFilterTouched}
            showFacets
          />
        </div>
        {filterTouched && (
          <AppliedFiltersPanel 
            subjectList={subjects || []} 
            setSubjectFilter={setSubjectFilter}
            setSourceFilter={setSourceFilter}
            setAgeRangeFilter={setAgeRangeFilter}
            setDurationFilter={setDurationFilter}
            appliedFilters={{
              ageRanges: ageRangeFilter,
              duration: durationFilter, 
              subjects: subjectFilter,
              source: sourceFilter
            }}
          />)}
      </div>
    </>
  );
};

export default FilterPanel;
