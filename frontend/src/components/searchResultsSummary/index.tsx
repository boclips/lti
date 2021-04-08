import React from 'react';
import s from './style.module.less';

interface Props {
  totalVideoElements: number;
  results: number;
}

const SearchResultsSummary = ({ totalVideoElements, results }: Props) => {
  return results > 1 ? (
    <div className={s.summary}>
      {`${totalVideoElements > 500 ? '500+' : totalVideoElements} result${
        results > 1 ? 's' : ''
      } found:`}
    </div>
  ) : null;
};

export default SearchResultsSummary;
