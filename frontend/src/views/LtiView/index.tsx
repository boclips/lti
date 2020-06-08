import React, { useState } from 'react';
import { Col, Layout, List, Row } from 'antd';
import { VideoCard } from '@bit/boclips.boclips-ui.components.video-card';
import { Video } from '@bit/boclips.boclips-ui.types.video';
import { Player } from 'boclips-player-react';
import Search from 'antd/lib/input/Search';
import { Video as ClientVideo } from 'boclips-api-client/dist/sub-clients/videos/model/Video';
import Pageable from 'boclips-api-client/dist/sub-clients/common/model/Pageable';
import Header from '../../components/Header';
import BoclipsLogo from '../../resources/images/boclips-logo.svg';
import ApiClient from '../../service/client/ApiClient';
import { AppConstants } from '../../types/AppConstants';
import VideoService from '../../service/video/VideoService';
import convertApiClientVideo from '../../service/video/convertVideoFromApi';
// import s from './styles.module.less';

const LtiView = () => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const convertVideos = (videosResponse: Pageable<ClientVideo>) => {
    const convertedVideos = videosResponse.page.map((v) =>
      convertApiClientVideo(v),
    );
    setVideos(convertedVideos);
  };

  const onSearch = (value: string) => {
    setLoading(true);
    new ApiClient(AppConstants.API_BASE_URL)
      .getClient()
      .then((client) => {
        new VideoService(client)
          .searchVideos({ query: value })
          .then((videosResponse) => convertVideos(videosResponse));
      })
      .then(() => setLoading(false));
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
      <Layout.Content>
        <Row>
          <Col sm={{ span: 24 }} md={{ span: 24 }}>
            {videos.length > 0 ? (
              <List
                itemLayout="vertical"
                size="large"
                pagination={{
                  pageSize: 10,
                }}
                dataSource={videos}
                loading={loading}
                renderItem={(video: Video) => (
                  <VideoCard
                    key={video.id}
                    video={video}
                    loading={loading}
                    authenticated
                    videoPlayer={
                      <Player videoUri={video.links?.self?.getOriginalLink()} />
                    }
                  />
                )}
              />
            ) : null}
          </Col>
        </Row>
      </Layout.Content>
    </Layout>
  );
};

export default LtiView;
