import React from 'react';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import { Filters } from '../../types/filters';
import DurationConverter from '../filterPanel/converters/DurationConverter';
import FilterBadgeFactory from './FilterBadgeFactory';
import s from './style.module.less';

interface AppliedFiltersPanelProps {
  appliedFilters: Filters;
  subjectsList: Channel[];
  channelsList: Subject[];
  setSubjectFilter: (any) => void;
  setSourceFilter: (any) => void;
  setAgeRangeFilter: (any) => void;
  setDurationFilter: (any) => void;
  setFilter: (filter: string[]) => void;
}

const AppliedFiltersPanel = ({
  appliedFilters: { ageRanges, source, duration, subjects },
  setFilter,
  channelsList,
  subjectsList,
  setSubjectFilter,
  setSourceFilter,
  setAgeRangeFilter,
  setDurationFilter,
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
    const facet = subjectsList.find(
      (channel) => channel.id === selectedChannelId,
    );
    return {
      displayValue: facet!.name,
      key: facet!.id,
    };
  });

  const subjectBadgeOptions = subjects?.map((subjectId) => {
    const facet = channelsList.find((subject) => subject.id === subjectId);
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
      filterName: 'ageRanges',
      badgeType: 'Age',
      badges: AgeBadgeOptions || [],
      updateFilters: setAgeRangeFilter,
    });

    const durationBadges = FilterBadgeFactory.produce({
      filterName: 'duration',
      badgeType: 'Duration',
      badges: durationBadgeOptions || [],
      updateFilters: setDurationFilter,
    });

    const subjectBadges = FilterBadgeFactory.produce({
      filterName: 'subjects',
      badgeType: 'Subject',
      badges: subjectBadgeOptions || [],
      updateFilters: setSubjectFilter,
    });

    const sourcesBadges = FilterBadgeFactory.produce({
      filterName: 'source',
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
