import React from 'react';
import s from './style.module.less';

interface Props {
  value: string;
  label: string;
  onClick: (value: string) => any;
}

export const FilterBadge = ({value, label, onClick}: Props) => (
  <span className={s.badge}>
    {label && <div className={s.label}>{label}</div>}
    {value && <div className={s.value}>{value}</div>}
    <div onClick={(_)=>onClick(value)}>{'X'}</div>
  </span>
);

