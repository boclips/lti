import { Layout, Row, Col } from 'antd';
import React from 'react';
import cx from 'classnames';
import { Header } from '../../bits/components/header';
import { TopNavbar } from './TopNavbar';
import s from './pageLayout.module.less';

interface Props {
  children: React.ReactNode;
}

export const PageLayout = (props: Props): React.ReactElement => (
    <Layout>
      <Layout.Header className={cx(s.header, s.content, s.headerFixed)}>
        <TopNavbar />
      </Layout.Header>
      <Layout.Content className="page-layout page-layout__content  ">
        <Row>
          <Col sm={{ span: 24 }} md={{ span: 24 }}>
            {props.children}
          </Col>
          <Header />
        </Row>
      </Layout.Content>
    </Layout>
  );
