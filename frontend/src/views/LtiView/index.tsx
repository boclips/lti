import React, { useState } from 'react';
import {
  Col, Layout, List, Row 
} from 'antd';
import { VideoCard } from '@bit/boclips.boclips-ui.components.video-card';
import { Video } from '@bit/boclips.boclips-ui.types.video';
import { Player } from 'boclips-player-react';
import { Video as ClientVideo } from 'boclips-api-client/dist/sub-clients/videos/model/Video';
import Pageable from 'boclips-api-client/dist/sub-clients/common/model/Pageable';
import HeaderWithLogo from '@bit/boclips.boclips-ui.components.header-with-logo';
import c from 'classnames';
import ApiClient from '../../service/client/ApiClient';
import { AppConstants } from '../../types/AppConstants';
import VideoService from '../../service/video/VideoService';
import convertApiClientVideo from '../../service/video/convertVideoFromApi';
import s from './styles.module.less';
import SearchBar from '../../components/SearchBar';
import EmptySVG from '../../resources/images/empty.svg';

const LtiView = () => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const convertVideos = (videosResponse: Pageable<ClientVideo>) => {
    const convertedVideos = videosResponse.page.map((v) =>
      convertApiClientVideo(v),
    );
    setVideos(convertedVideos);
    setLoading(false);
  };

  const onSearch = (query: string) => {
    setLoading(true);
    new ApiClient(AppConstants.API_BASE_URL).getClient().then((client) => {
      new VideoService(client)
        .searchVideos({ query })
        .then((videosResponse) => convertVideos(videosResponse));
    });
  };

  const EmptyList = () => (
    <div className={s.emptyWrapper}>
      <div className={s.svgWrapper}>
        <EmptySVG />
      </div>
      <div className={s.emptyWelcome}>
        Welcome to <br /> BoClips Video Library
      </div>
      <div className={s.emptyInfo}>
        Use the search on top to find interesting videos
      </div>
    </div>
  );

  return (
    <Layout>
      <HeaderWithLogo>
        <SearchBar onSearch={onSearch} />
      </HeaderWithLogo>
      <Layout.Content>
        <Row className={s.videoCardWrapper}>
          <Col sm={{ span: 24 }} md={{ span: 24 }}>
            <List
              itemLayout="vertical"
              size="large"
              locale={{ emptyText: EmptyList() }}
              pagination={{
                pageSize: 10,
                className: c(s.pagination, {
                  [s.paginationEmpty]: !videos.length,
                }),
                showSizeChanger: false,
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
          </Col>
        </Row>
      </Layout.Content>
    </Layout>
  );
};

export default LtiView;
