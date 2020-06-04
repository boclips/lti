import React, { useState } from 'react';
import s from './styles.module.less';
import { Col, Layout, Row } from 'antd';
import { Header } from '../../components/Header';
import BoclipsLogo from '../../resources/images/boclips-logo.svg';
import Search from 'antd/es/input/Search';
import { ApiClient } from '../../service/client/ApiClient';
import { AppConstants } from '../../types/AppConstants';
import { VideoService } from '../../service/video/VideoService';

interface Props {
  children?: React.ReactNode;
}

export const LtiView = (props: Props) => {
  const [videos, setVideos] = useState();

  const onSearch = (value: string) => {
    new ApiClient(AppConstants.API_BASE_URL).getClient().then((client) => {
      new VideoService(client)
        .searchVideos({ query: value })
        .then((videos) => setVideos(videos));
    });
  };

  return (
    <Layout>
      <Header logo={<BoclipsLogo />}>
        <Search
          size="large"
          enterButton="Search"
          placeholder="Search for videos"
          onSearch={onSearch}
        />
      </Header>
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
