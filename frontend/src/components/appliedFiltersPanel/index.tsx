import React from 'react';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
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
  channels: Channel[];
  allSubjects: Subject[];
}

const AppliedFiltersPanel = ({
  appliedFilters: { ageRanges, source, duration, subjects },
  setAgeRangeFilter,
  setDurationFilter,
  setSourceFilter,
  setSubjectFilter,
  allSubjects,
  channels,
}: AppliedFiltersPanelProps) => {
  const AgeBadgeOptions = ageRanges?.map((filter) => ({
    displayValue: filter,
    key: filter,
  }));

  const durationBadgeOptions = duration?.map((durationISO) => ({
    displayValue: DurationConverter.getLabelFromIso(durationISO),
    key: durationISO,
  }));

  const sourceBadgeOptions = source?.map((selectedChannelId) => {
    const facet = channels.find((channel) => channel.id === selectedChannelId);
    return {
      displayValue: facet!.name,
      key: facet!.id,
    };
  });

  const subjectBadgeOptions = subjects?.map((subjectId) => {
    const facet = allSubjects.find((subject) => subject.id === subjectId);
    return {
      displayValue: facet!.name,
      key: facet!.id,
    };
  });

  const isAnyFilterApplied = () =>
    Boolean(
      subjects?.length ||
        ageRanges?.length ||
        source?.length ||
        duration?.length,
    );

  const appliedFilterBadges = () => {
    const ageBadges = FilterBadgeFactory.produce({
      badgeType: 'Age',
      badges: AgeBadgeOptions || [],
      updateFilters: setAgeRangeFilter,
    });

    const durationBadges = FilterBadgeFactory.produce({
      badgeType: 'Duration',
      badges: durationBadgeOptions || [],
      updateFilters: setDurationFilter,
    });

    const subjectBadges = FilterBadgeFactory.produce({
      badgeType: 'Subject',
      badges: subjectBadgeOptions || [],
      updateFilters: setSubjectFilter,
    });

    const sourcesBadges = FilterBadgeFactory.produce({
      badgeType: 'Source',
      badges: sourceBadgeOptions || [],
      updateFilters: setSourceFilter,
    });

    return [
      ...ageBadges,
      ...durationBadges,
      ...subjectBadges,
      ...sourcesBadges,
    ];
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
