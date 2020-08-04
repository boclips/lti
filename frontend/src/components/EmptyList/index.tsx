import React from 'react';
import c from 'classnames';
import s from './style.module.less';
import EmptySVG from './resources/empty-search.svg';

interface Props {
  theme: 'lti' | 'backoffice' | 'custom';
}

const EmptyList = ({ theme = 'lti' }: Props) => (
  <div
    className={c(s.emptyWrapper, {
      [s.lti]: theme === 'lti',
      [s.backoffice]: theme === 'backoffice',
      [s.custom]: theme === 'custom',
    })}
  >
    <div className={s.svgWrapper}>
      <EmptySVG />
    </div>
    <div className={s.emptyInfo}>
      Use the search on top to discover inspiring videos
    </div>
  </div>
);

export default EmptyList;
