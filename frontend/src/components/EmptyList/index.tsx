import React from 'react';
import s from './style.module.less';
import EmptySVG from '../../resources/images/empty.svg';

const EmptyList = () => (
  <div className={s.emptyWrapper}>
    <div className={s.svgWrapper}>
      <EmptySVG />
    </div>
    <div className={s.emptyWelcome}>
      Welcome to <br /> BoClips Video Library
    </div>
    <div className={s.emptyInfo}>
      Use the search on top to find interesting videos
    </div>
  </div>
);

export default EmptyList;
