import React, { useEffect, useState } from 'react';
import { SelectOption } from '@boclips-ui/select-option';
import SelectFilter, { DropdownAligment } from '@boclips-ui/select/src';
import {
  Facet,
  VideoFacets,
} from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import s from './style.module.less';
import DurationConverter from './converters/DurationConverter';
import { Filters } from '../../types/filters';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import AppliedFiltersPanel from '../appliedFiltersPanel';
import { useBoclipsClient } from '../../hooks/useBoclipsClient';

const MOBILE_BREAKPOINT = 'mobile';

interface Props {
  facets?: VideoFacets;
  onApply: (filters: Filters) => void;
  hidePanel?: boolean;
  channelsList: Channel[];
  subjectsList: Subject[];
}

const ResponsiveFilterPanel = ({ facets, onApply }: Props) => {
  const [channelsList, setChannelsList] = useState<Channel[]>([]);
  const [subjectsList, setSubjectsList] = useState<Subject[]>([]);
  const [ageRangeFilter, setAgeRangeFilter] = useState<string[]>();
  const [durationFilter, setDurationFilter] = useState<string[]>();
  const [subjectFilter, setSubjectFilter] = useState<string[]>();
  const [sourceFilter, setSourceFilter] = useState<string[]>();
  // const [filterTouched, setFilterTouched] = useState<boolean>(false);
  const breakpoints = useMediaBreakPoint();
  const mobileView = breakpoints.type === MOBILE_BREAKPOINT;

  const apiClient = useBoclipsClient();

  useEffect(() => {
    apiClient.subjects.getAll().then((subjects) => setSubjectsList(subjects));

    apiClient.channels.getAll().then((channels) => setChannelsList(channels));
  }, [setChannelsList, setSubjectsList, apiClient]);

  useEffect(() => {
    if (durationFilter || (durationFilter && durationFilter!.length === 0)) {
      onApply({
        duration: durationFilter,
      });
    }
  }, [durationFilter, onApply]);

  useEffect(() => {
    if (subjectFilter || (subjectFilter && subjectFilter!.length === 0)) {
      onApply({
        subjects: subjectFilter,
      });
    }
  }, [subjectFilter, onApply]);

  useEffect(() => {
    if (sourceFilter || (sourceFilter && sourceFilter!.length === 0)) {
      onApply({
        source: sourceFilter,
      });
    }
  }, [sourceFilter, onApply]);

  useEffect(() => {
    if (ageRangeFilter || (ageRangeFilter && ageRangeFilter!.length === 0)) {
      onApply({
        ageRanges: ageRangeFilter,
      });
    }
  }, [ageRangeFilter, onApply]);

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
        {/*<span className={s.filtersTitle}>FILTER BY:</span>*/}
        {/*{filterTouched && (*/}
        {/*  <Button className={s.clearAll} onClick={onClear} type="text">*/}
        {/*    CLEAR ALL*/}
        {/*  </Button>*/}
        {/*)}*/}
      </div>
      <div className={s.selectFilters}>
        <SelectFilter
          relativePositionFilters={mobileView}
          options={ageRangeOptions}
          title="Age"
          updatedSelected={ageRangeFilter}
          onApply={setAgeRangeFilter}
          // touched={setFilterTouched}
          showFacets
        />
        <SelectFilter
          relativePositionFilters={mobileView}
          options={durationOptions}
          title="Duration"
          updatedSelected={durationFilter}
          onApply={setDurationFilter}
          // touched={setFilterTouched}
          showFacets
        />
        <SelectFilter
          relativePositionFilters={mobileView}
          options={subjectOptions}
          title="Subject"
          onApply={setSubjectFilter}
          updatedSelected={subjectFilter}
          searchPlaceholder="Search for subject"
          allowSearch
          // touched={setFilterTouched}
          showFacets
        />
        <SelectFilter
          relativePositionFilters={mobileView}
          options={sourceOptions}
          title="Source"
          onApply={setSourceFilter}
          updatedSelected={sourceFilter}
          searchPlaceholder="Search for source"
          allowSearch
          // touched={setFilterTouched}
          showFacets
          dropdownAlignment={DropdownAligment.RIGHT}
        />
      </div>
      <AppliedFiltersPanel
        subjectsList={channelsList}
        channelsList={subjectsList}
        setSubjectFilter={setSubjectFilter}
        setSourceFilter={setSourceFilter}
        setAgeRangeFilter={setAgeRangeFilter}
        setDurationFilter={setDurationFilter}
        appliedFilters={{
          ageRanges: ageRangeFilter,
          duration: durationFilter,
          subjects: subjectFilter,
          source: sourceFilter,
        }}
      />
    </>
  );
};

export default ResponsiveFilterPanel;
