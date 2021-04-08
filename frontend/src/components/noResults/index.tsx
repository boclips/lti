import s from './style.module.less';
import React from 'react';
import NoResultsView from '@boclips-ui/no-results';

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
