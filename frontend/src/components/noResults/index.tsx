import React from 'react';
import NoResultsView from '@boclips-ui/no-results';
import s from './style.module.less';

interface Props {
  searchQuery: string;
  filtersApplied: boolean;
}

const NoResults = ({ searchQuery, filtersApplied }: Props) => {
  return (
    <div className={s.noResults}>
      <NoResultsView
        searchQuery={searchQuery}
        filtersApplied={filtersApplied}
      />
    </div>
  );
};

export default NoResults;
