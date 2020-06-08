import React, { ReactElement } from 'react';
import { Col, Layout, Row } from 'antd';
import s from './styles.module.less';

interface Props {
  children: React.ReactNode;
  logo?: ReactElement;
}

const Header = ({ logo, children }: Props) => (
  <Layout.Header className={s.header}>
    <Row align="middle" className={s.headerBody}>
      <Col span={4} className={s.headerContent}>
        {logo}
      </Col>
      <Col span={16} className={s.headerContent}>
        {children}
      </Col>
    </Row>
  </Layout.Header>
);

export default Header;
