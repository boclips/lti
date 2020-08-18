import React, { useEffect, useMemo, useState } from 'react';
import {
  Col, Layout, List, Row
} from 'antd';
import { Video } from '@bit/boclips.boclips-ui.types.video';
import c from 'classnames';
import SearchBar from '@bit/boclips.boclips-ui.components.search-bar';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Channel, Subject } from 'boclips-api-client/dist/types';
import ApiClient from '../../service/client/ApiClient';
import { AppConstants } from '../../types/AppConstants';
import VideoService, { ExtendedClientVideo, } from '../../service/video/VideoService';
import s from './styles.module.less';
import EmptyList from '../../components/EmptyList';
import TitleHeader from '../../components/TitleHeader';
import NoResults from '../../components/NoResults/NoResults';
import FilterPanel from '../../components/filterPanel';
import FiltersButton from '../../components/filterButton';

interface Props {
  renderVideoCard: (video: Video, isLoading: boolean) => React.ReactNode;
  collapsibleFilters?: boolean;
  header?: React.ReactNode;
}

const LtiView = ({ renderVideoCard, collapsibleFilters, header }: Props) => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>();
  const [searchPageNumber, setPageNumber] = useState<number>(0);
  const [totalVideoElements, setTotalVideoElements] = useState<number>(0);
  const [facets, setFacets] = useState<VideoFacets>();
  const [singleFilter, setSingleFilter] = useState<any>(null);
  const [filters, setFilters] = useState<any>(null);
  const [apiSubjects, setApiSubjects] = useState<Subject[]>([]);
  const [apiChannels, setApiChannels] = useState<Channel[]>([]);
  const [filtersVisible, setFiltersVisible] = useState<boolean>(!collapsibleFilters);

  const videoServicePromise = useMemo(
    () =>
      new ApiClient(AppConstants.API_BASE_URL)
        .getClient()
        .then((client) => new VideoService(client)),
    [],
  );

  const onSearch = (query?: string, page: number = 0) => {
    if ((query && searchQuery !== query) || page !== searchPageNumber) {
      setSearchQuery(query);
      setPageNumber(page!!);
      setLoading(true);
    }
  };

  const handleSearchResults = (searchResults: ExtendedClientVideo<Video>) => {
    setFacets(searchResults.facets);
    setTotalVideoElements(searchResults.pageSpec.totalElements);
    setVideos(searchResults.page);
    setLoading(false);
  };

  const search = () => {
    videoServicePromise.then((videoService) => {
      videoService
        .searchVideos({
          query: searchQuery,
          page: searchPageNumber,
          size: 10,
          age_range: filters?.ageRanges,
          duration: filters?.duration,
          subject: filters?.subjects,
          channel: filters?.source,
        })
        .then((videosResponse) => {
          handleSearchResults(videosResponse);
        });
    },
    );
  };

  const getFilters = () => {
    videoServicePromise.then((client) => client.getSubjects())
      .then((it) => setApiSubjects(it));

    videoServicePromise.then((client) => client.getChannels())
      .then((it) => setApiChannels(it));
  };

  useEffect(() => {
    getFilters();
  }, []);

  useEffect(() => {
    if (singleFilter) {
      setFilters(((prevState) => ({ ...prevState, ...singleFilter })));
    }
  }, [singleFilter]);

  useEffect(() => {
    if (searchQuery || searchPageNumber || filters) {
      setLoading(true);
      search();
    }
  }, [searchQuery, searchPageNumber, filters]);

  const scrollToTop = () => {
    window.scrollTo(0, 0);
  };

  const showFiltersButton = collapsibleFilters && videos.length > 0;

  const renderVideoList = () => useMemo(() =>
    !loading && videos.length === 0 && !!searchQuery ? (
      <NoResults searchQuery={searchQuery}/>
    ) : (
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
          className={s.listWrapper}
          locale={{ emptyText: <EmptyList theme="lti" /> }}
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
    ), [videos, loading]);

  return (
    <>
      <Layout.Header className={!!searchQuery? s.layoutHeader : s.layoutHeaderBeforeSearch}>
        <Row>
          <Col xs={24}>
            {header || (<TitleHeader title="Video Library" />)}
          </Col>
        </Row>
        <Row>
          <Col sm={{ span: 24 }} md={{ span: 16, offset: 4 }}
            className={c({
              [s.searchBar]: true,
              [s.filtersButton]: showFiltersButton
            })}
          >
            <div className={s.searchBarContainer}>
              <SearchBar
                onSearch={onSearch}
                placeholder="Search for videos..."
                theme="lti"
              />
              {showFiltersButton && (
                <FiltersButton
                  filtersVisible={filtersVisible}
                  toggleFilters={setFiltersVisible}
                />
              )}
            </div>
          </Col>
        </Row>
        <Row >
          <Col sm={{ span: 24 }} md={{ span: 16, offset: 4 }} className={s.filtersAlign}>
            {(videos.length > 0 || singleFilter) && (
              <FilterPanel
                facets={facets}
                onApply={setSingleFilter}
                subjects={apiSubjects}
                sources={apiChannels}
                hidePanel={!filtersVisible}
              />
            )}
          </Col>
        </Row>
      </Layout.Header>
      <Layout.Content className={!!searchQuery? s.main : s.mainBeforeSearch }>
        <Row >
          {renderVideoList()}
        </Row>
      </Layout.Content>
    </>
  );
};

export default LtiView;
