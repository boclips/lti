import React from 'react';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import { Filters } from '../../types/filters';
import DurationConverter from '../filterPanel/converters/DurationConverter';
import FilterBadgeFactory, { BadgeOption } from './FilterBadgeFactory';

interface AppliedFiltersPanelProps {
  appliedFilters: Filters;
  setAgeRangeFilter: (filter: string[]) => void;
  setDurationFilter: (filter: string[]) => void;
  setSourceFilter: (filter: string[]) => void;
  setSubjectFilter: (filter: string[]) => void;
  subjectList: Subject[];
}

const AppliedFiltersPanel = ({
  appliedFilters: {
    ageRanges, source, duration, subjects
  },
  setAgeRangeFilter,
  setDurationFilter,
  setSourceFilter,
  setSubjectFilter,
  subjectList
}: AppliedFiltersPanelProps) => {
  const appliedFilterBadges = () => {
    const AgeBadgeOptions = ageRanges?.map((filter) => ({
      displayValue: filter,
      key: filter
    }));

    const durationBadgeOptions = duration?.map((durationISO) => ({
      displayValue: DurationConverter.getLabelFromIso(durationISO),
      key: durationISO
    }));

    const sourceBadgeOptions = source?.map((filter) => ({
      displayValue: filter,
      key: filter
    }));

    const subjectBadgeOptions = subjects?.map((subjectId) => {
      const subject = subjectList?.find((item) => item.id === subjectId)!!;

      return {
        displayValue: subject!.name,
        key: subject!.id
      };
    });

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
    <span>
      <div>Filters applied</div>
      {appliedFilterBadges()}
    </span>
  );
};

export default AppliedFiltersPanel;
