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
    <Col xs={showSlsTerms ? 20 : 24}>
      {title}
    </Col>
    {showSlsTerms && (<Col xs={4}><AboutModal/></Col>)}
  </Row>
);

export default TitleHeader;
