import React from 'react';
import s from './style.module.less';

interface TitleHeaderProps {
  title: string;
}

const TitleHeader = ({ title }: TitleHeaderProps) => (
  <div data-qa="title-header" className={s.title}> {title} </div>
);

export default TitleHeader;
