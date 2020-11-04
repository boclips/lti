import React from 'react';
import { Col, Row } from 'antd';
import s from './style.module.less';
import AboutModal from '../sls/AboutModal';

interface TitleHeaderProps {
  title: string;
  showSlsTerms?: boolean
}

const TitleHeader = ({ title, showSlsTerms }: TitleHeaderProps) => (
  <Row data-qa="title-header" justify="space-between" className={s.title}>
    <Col>
      {title}
    </Col>
    {showSlsTerms && (<Col><AboutModal/></Col>)}
  </Row>
);

export default TitleHeader;
