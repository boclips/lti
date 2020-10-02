import React from 'react';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Filters } from '../../types/filters';
import DurationConverter from '../filterPanel/converters/DurationConverter';
import FilterBadgeFactory from './FilterBadgeFactory';
import s from './style.module.less';

interface AppliedFiltersPanelProps {
  appliedFilters: Filters;
  setAgeRangeFilter: (filter: string[]) => void;
  setDurationFilter: (filter: string[]) => void;
  setSourceFilter: (filter: string[]) => void;
  setSubjectFilter: (filter: string[]) => void;
  subjectList: Subject[];
  facets: VideoFacets | undefined;
}

const AppliedFiltersPanel = ({
  appliedFilters: {
    ageRanges, source, duration, subjects
  },
  setAgeRangeFilter,
  setDurationFilter,
  setSourceFilter,
  setSubjectFilter,
  subjectList,
  facets
}: AppliedFiltersPanelProps) => {
  const AgeBadgeOptions = ageRanges?.map((filter) => ({
    displayValue: filter,
    key: filter
  }));

  const durationBadgeOptions = duration?.map((durationISO) => ({
    displayValue: DurationConverter.getLabelFromIso(durationISO),
    key: durationISO
  }));

  const sourceBadgeOptions = facets?.channels && Object.entries(facets.channels)
    .filter(([, facet]) => source && facet.id && source.indexOf(facet.id) !== -1)
    .map(([channelName, facet]) => (
      {
        displayValue: channelName,
        key: facet.id || ''
      }
    ));

  const subjectBadgeOptions = subjects?.map((subjectId) => {
    const subject = subjectList?.find((item) => item.id === subjectId)!!;

    return {
      displayValue: subject!.name,
      key: subject!.id
    };
  });
  
  const isAnyFilterApplied = () => 
    Boolean(subjects?.length || ageRanges?.length || source?.length || duration?.length);

  const appliedFilterBadges = () => {
    const ageBadges = FilterBadgeFactory.produce({
      badgeType: 'Age',
      badges: AgeBadgeOptions || [],
      updateFilters: setAgeRangeFilter
    });

    const durationBadges = FilterBadgeFactory.produce({
      badgeType: 'Duration',
      badges: durationBadgeOptions || [],
      updateFilters: setDurationFilter
    });

    const subjectBadges = FilterBadgeFactory.produce({
      badgeType: 'Subject',
      badges: subjectBadgeOptions || [],
      updateFilters: setSubjectFilter
    });

    const sourcesBadges = FilterBadgeFactory.produce({
      badgeType: 'Source',
      badges: sourceBadgeOptions || [],
      updateFilters: setSourceFilter
    });

    return [...ageBadges, ...durationBadges, ...subjectBadges, ...sourcesBadges];
  };
  return (
    <div>
      {isAnyFilterApplied() && (
        <span className={s.filtersAppliedWrapper}>
          <div className={s.filtersAppliedText}>Filters applied:</div>
          {appliedFilterBadges()}
        </span>
      )}
    </div>
  );
};

export default AppliedFiltersPanel;
