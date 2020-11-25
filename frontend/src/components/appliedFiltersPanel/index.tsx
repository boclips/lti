import React from 'react';
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
  
  const sourceBadgeOptions = source?.map((selectedChannelId) => {
    const facet = facets!!.channels!![selectedChannelId];
    return {
      displayValue: facet!!.name!!,
      key: facet!!.id!!
    };
  });

  const subjectBadgeOptions = subjects?.map((subjectId) => {
    const subject = facets!!.subjects!![subjectId];

    return {
      displayValue: subject!!.name!!,
      key: subject!!.id!!
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
