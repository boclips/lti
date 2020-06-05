import React, { useState } from 'react';
import { Col, Layout, Row } from 'antd';
import Search from 'antd/es/input/Search';
import { VideoCard } from '@bit/boclips.boclips-ui.components.video-card';
import { Video } from '@bit/boclips.boclips-ui.types.video';
import { Player } from 'boclips-player-react';
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

  const convertVideos = (videosResponse) => {
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
            {videos.map((it: Video) => (
              <VideoCard
                key={it.id}
                video={it}
                loading={loading}
                authenticated
                videoPlayer={
                  <Player videoUri={it.links.self.getOriginalLink()} />
                }
                // videoActionButtons={
                //   <VideoButtons video={video} mode={'card'} />
                // }
                // analytics={() => emitVideoLinkClickEvent(video)}
              />
            ))}
          </Col>
        </Row>
      </Layout.Content>
    </Layout>
  );
};

export default LtiView;
