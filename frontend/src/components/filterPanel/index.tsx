import React, { useEffect, useState } from 'react';
import { SelectOption } from '@boclips-ui/select-option';
import SelectFilter, { DropdownAligment } from '@boclips-ui/select';
import c from 'classnames';
import { Button } from 'antd';
import {
  Facet,
  VideoFacets,
} from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import s from './style.module.less';
import DurationConverter from './converters/DurationConverter';
import AppliedFiltersPanel from '../appliedFiltersPanel';
import { Filters } from '../../types/filters';

interface Props {
  facets?: VideoFacets;
  onApply: (filters: Filters) => void;
  hidePanel?: boolean;
}

const FilterPanel = ({ facets, onApply, hidePanel }: Props) => {
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();
  const [durationFilter, setDurationFilter] = useState<string[]>();
  const [subjectFilter, setSubjectFilter] = useState<string[]>();
  const [sourceFilter, setSourceFilter] = useState<string[]>();
  const [filterTouched, setFilterTouched] = useState<boolean>(false);

  useEffect(() => {
    if (durationFilter || (durationFilter && durationFilter!.length === 0)) {
      onApply({ duration: durationFilter });
    }
  }, [durationFilter, onApply]);

  useEffect(() => {
    if (subjectFilter || (subjectFilter && subjectFilter!.length === 0)) {
      onApply({ subjects: subjectFilter });
    }
  }, [onApply, subjectFilter]);

  useEffect(() => {
    if (sourceFilter || (sourceFilter && sourceFilter!.length === 0)) {
      onApply({ source: sourceFilter });
    }
  }, [onApply, sourceFilter]);

  useEffect(() => {
    if (ageRangeFilter || (ageRangeFilter && ageRangeFilter!.length === 0)) {
      onApply({ ageRanges: ageRangeFilter });
    }
  }, [ageRangeFilter, onApply]);

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
            options={subjectOptions}
            title="Subject"
            onApply={setSubjectFilter}
            updatedSelected={subjectFilter}
            searchPlaceholder="Search for subject"
            allowSearch
            touched={setFilterTouched}
            showFacets
          />
          <SelectFilter
            options={sourceOptions}
            title="Source"
            onApply={setSourceFilter}
            updatedSelected={sourceFilter}
            searchPlaceholder="Search for source"
            allowSearch
            touched={setFilterTouched}
            showFacets
            dropdownAlignment={DropdownAligment.RIGHT}
          />
        </div>
        {filterTouched && (
          <AppliedFiltersPanel
            setSubjectFilter={setSubjectFilter}
            setSourceFilter={setSourceFilter}
            setAgeRangeFilter={setAgeRangeFilter}
            setDurationFilter={setDurationFilter}
            facets={facets}
            appliedFilters={{
              ageRanges: ageRangeFilter,
              duration: durationFilter,
              subjects: subjectFilter,
              source: sourceFilter,
            }}
          />
        )}
      </div>
    </>
  );
};

export default FilterPanel;
