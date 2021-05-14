import React, { useEffect, useState } from 'react';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import DurationConverter from '../../converters/DurationConverter';
import FilterBadgeFactory from './FilterBadgeFactory';
import s from './style.module.less';
import { useBoclipsClient } from '../../hooks/useBoclipsClient';
import { useFilters } from '../../hooks/useFilters';

const AppliedFiltersPanel = () => {
  const apiClient = useBoclipsClient();
  const [channelsList, setChannelsList] = useState<Channel[]>([]);
  const [subjectsList, setSubjectsList] = useState<Subject[]>([]);
  const { filters, setFilters, clearFilters } = useFilters();
  const { source, subjects, ageRanges, duration } = filters;

  useEffect(() => {
    apiClient.subjects
      .getAll()
      .then((subjectsApi) => setSubjectsList(subjectsApi));

    apiClient.channels
      .getAll()
      .then((channelsApi) => setChannelsList(channelsApi));
  }, [setChannelsList, setSubjectsList, apiClient]);

  const AgeBadgeOptions = ageRanges?.map((filter) => ({
    displayValue: filter,
    key: filter,
  }));

  const durationBadgeOptions = duration?.map((durationISO) => ({
    displayValue: DurationConverter.getLabelFromIso(durationISO),
    key: durationISO,
  }));

  const sourceBadgeOptions =
    channelsList.length > 0 &&
    source?.map((selectedChannelId) => {
      const facet = channelsList.find(
        (channel) => channel.id === selectedChannelId,
      );

      return {
        displayValue: facet!.name,
        key: facet!.id,
      };
    });

  const subjectBadgeOptions =
    subjectsList.length > 0 &&
    subjects?.map((subjectId) => {
      const facet = subjectsList.find((subject) => subject.id === subjectId);
      return {
        displayValue: facet!.name,
        key: facet!.id,
      };
    });

  const clearAll = (
    <div
      role="presentation"
      onClick={() => clearFilters()}
      className={s.clearAll}
    >
      Clear All
    </div>
  );

  const appliedFilterBadges = () => {
    const ageBadges = FilterBadgeFactory.produce({
      badgeType: 'Age',
      badges: AgeBadgeOptions || [],
      updateFilters: (f) => setFilters({ ...filters, ageRanges: f }),
    });

    const durationBadges = FilterBadgeFactory.produce({
      badgeType: 'Duration' || [],
      badges: durationBadgeOptions || [],
      updateFilters: (f) => setFilters({ ...filters, duration: f }),
    });

    const subjectBadges = FilterBadgeFactory.produce({
      badgeType: 'Subject',
      badges: subjectBadgeOptions || [],
      updateFilters: (f) => setFilters({ ...filters, subjects: f }),
    });

    const sourcesBadges = FilterBadgeFactory.produce({
      badgeType: 'Source',
      badges: sourceBadgeOptions || [],
      updateFilters: (f) => setFilters({ ...filters, source: f }),
    });

    const badges = [
      ...ageBadges,
      ...durationBadges,
      ...subjectBadges,
      ...sourcesBadges,
    ];

    if (badges.length > 1) {
      badges.push(clearAll);
    }

    return [...badges];
  };
  return <div className={s.filtersAppliedWrapper}>{appliedFilterBadges()}</div>;
};
export default AppliedFiltersPanel;
