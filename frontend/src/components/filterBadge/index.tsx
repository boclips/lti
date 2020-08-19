import React from 'react';
import s from './style.module.less';
import CloseIcon from './resources/close-icon.svg';

interface Props {
  value: string;
  label: string;
  onClick: (value: string) => any;
}

export const FilterBadge = ({ value, label, onClick }: Props) => (
  <span className={s.badge}>
    {label && <div className={s.label}>{label}</div>}
    {value && <div className={s.value}>{value}</div>}
    <div onClick={(_) => onClick(value)}>
      <CloseIcon/>
    </div>
  </span>
);
