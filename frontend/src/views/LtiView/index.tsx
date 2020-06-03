import React from 'react';
import s from './styles.module.less';
import { Col, Layout, Row } from 'antd';
import { Header } from '../../components/Header';
import BoclipsLogo from '../../resources/images/boclips-logo.svg';

interface Props {
  children: React.ReactNode;
}

export const LtiView = (props: Props) => {
  return (
    <Layout>
      <Header logo={<BoclipsLogo />} />
      <Layout.Content className="page-layout page-layout__content  ">
        <Row>
          <Col sm={{ span: 24 }} md={{ span: 24 }}>
            {props.children}
          </Col>
        </Row>
      </Layout.Content>
    </Layout>
  );
};
