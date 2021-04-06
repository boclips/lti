import React, { useEffect } from 'react';
import { useBoclipsClient } from '../../hooks/useBoclipsClient';
import useFeatureFlags from '../../hooks/useFeatureFlags';
import s from './style.module.less';

const ResponsiveSearchView = () => {
  const client = useBoclipsClient();
  const featureFlags = useFeatureFlags();

  useEffect(() => {
    client.subjects.getAll();
  }, [client.subjects]);

  return (
    <div className={`${s.grid} ${s.container}`}>
      <div className={s.header}>123</div>
      <div className={s.body}></div>
    </div>
  );
};

export default ResponsiveSearchView;
