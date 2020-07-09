import React, { useEffect, useState, useMemo } from 'react';
import {
  Col, Layout, List, Row 
} from 'antd';
import { Video } from '@bit/dev-boclips.boclips-ui.types.video';
import { Video as ClientVideo } from 'boclips-api-client/dist/sub-clients/videos/model/Video';
import Pageable from 'boclips-api-client/dist/sub-clients/common/model/Pageable';
import HeaderWithLogo from '@bit/dev-boclips.boclips-ui.components.header-with-logo';
import c from 'classnames';
import SearchBar from '@bit/dev-boclips.boclips-ui.components.search-bar';
import ApiClient from '../../service/client/ApiClient';
import { AppConstants } from '../../types/AppConstants';
import VideoService from '../../service/video/VideoService';
import convertApiClientVideo from '../../service/video/convertVideoFromApi';
import s from './styles.module.less';
import EmptySVG from '../../resources/images/empty.svg';

interface Props {
  renderVideoCard: (video: Video, isLoading: boolean) => React.ReactNode;
}

const LtiView = ({ renderVideoCard }: Props) => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>();
  const [searchPageNumber, setPageNumber] = useState<number>(0);
  const videoServicePromise = useMemo(
    () =>
      new ApiClient(AppConstants.API_BASE_URL)
        .getClient()
        .then((client) => new VideoService(client)),
    [],
  );

  const [totalVideoElements, setTotalVideoElements] = useState<number>(0);

  const convertVideos = (videosResponse: Pageable<ClientVideo>) => {
    setTotalVideoElements(videosResponse.pageSpec.totalElements);
    const convertedVideos = videosResponse.page.map((v) =>
      convertApiClientVideo(v),
    );
    setVideos(convertedVideos);
    setLoading(false);
  };

  useEffect(() => {
    if (searchQuery || searchPageNumber) {
      videoServicePromise.then((videoService) =>
        videoService
          .searchVideos({
            query: searchQuery,
            page: searchPageNumber,
            size: 10,
          })
          .then((videosResponse) => convertVideos(videosResponse)),
      );
    }
  }, [searchQuery, searchPageNumber]);

  const onSearch = (query?: string, page: number = 0) => {
    if (query) {
      setSearchQuery(query);
      setPageNumber(page!!);
      setLoading(true);
    }
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

  const scrollToTop = () => {
    window.scrollTo(0, 0);
  };

  return (
    <Layout className={s.layout}>
      <HeaderWithLogo>
        <SearchBar onSearch={onSearch} placeholder="Search for videos..." />
      </HeaderWithLogo>
      <Layout.Content>
        <Row gutter={[16, 16]} className={s.videoCardWrapper}>
          <Col sm={{ span: 24 }} md={{ span: 16, offset: 4 }}>
            {videos.length > 0 && (
              <section className={s.numberOfResults}>
                <span>
                  {`${
                    totalVideoElements > 500 ? '500+' : totalVideoElements
                  } result${videos.length > 1 ? 's' : ''} found:`}
                </span>
              </section>
            )}
            <List
              itemLayout="vertical"
              size="large"
              className={s.listWarpper}
              locale={{ emptyText: EmptyList() }}
              pagination={{
                total: totalVideoElements,
                pageSize: 10,
                className: c(s.pagination, {
                  [s.paginationEmpty]: !videos.length,
                }),
                showSizeChanger: false,
                onChange: (page) => {
                  scrollToTop();
                  onSearch(searchQuery, page - 1);
                },
              }}
              dataSource={videos}
              loading={{
                wrapperClassName: s.spinner,
                spinning: loading,
              }}
              renderItem={(video: Video) => renderVideoCard(video, loading)}
            />
          </Col>
        </Row>
      </Layout.Content>
    </Layout>
  );
};

export default LtiView;
