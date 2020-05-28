import { Layout, Row, Col } from 'antd';
import React from 'react';
import { TopNavbar } from './TopNavbar';
import './PageLayout.less';

interface Props {
  children: React.ReactNode;
}

const { Header, Content } = Layout;

export const PageLayout = (props: Props): React.ReactElement => {
  return (
    <Layout>
      <Header className="page-layout page-layout__header page-layout__header--fixed">
        <TopNavbar />
      </Header>
      <Content className="page-layout page-layout__content  ">
        <Row>
          <Col sm={{ span: 24 }} md={{ span: 24 }}>
            {props.children}
          </Col>
        </Row>
      </Content>
    </Layout>
  );
};
