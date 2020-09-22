import React, { useEffect, useMemo, useState } from 'react';
import {
  Col, Layout, List, Row
} from 'antd';
import { Video } from '@bit/boclips.dev-boclips-ui.types.video';
import c from 'classnames';
import SearchBar from '@boclips-ui/search-bar';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { Subject } from 'boclips-api-client/dist/types';
import NoResults from '@bit/boclips.dev-boclips-ui.components.no-results';
import ApiClient from '../../service/client/ApiClient';
import { AppConstants } from '../../types/AppConstants';
import VideoService, { ExtendedClientVideo, } from '../../service/video/VideoService';
import s from './styles.module.less';
import EmptyList from '../../components/EmptyList';
import TitleHeader from '../../components/TitleHeader';
import FilterPanel from '../../components/filterPanel';
import FiltersButton from '../../components/filtersButton';

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
  const [filtersVisible, setFiltersVisible] = useState<boolean>(!collapsibleFilters);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [activeFilterCount, setActiveFilterCount] = useState<number>(0);

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
      setCurrentPage(1);
    }
  };

  const handleSearchResults = (searchResults: ExtendedClientVideo<Video>) => {
    setFacets(searchResults.facets);
    setTotalVideoElements(searchResults.pageSpec.totalElements);
    setVideos(searchResults.page);
    setLoading(false);
  };

  const handleFilterAdded = (addedFilter) => {
    setFilters(((prevState) => ({ ...prevState, ...addedFilter })));
    setPageNumber(0);
    setCurrentPage(1);
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
          include_channel_facets: true
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
  };

  useEffect(() => {
    getFilters();
  }, []);

  useEffect(() => {
    if (singleFilter) {
      handleFilterAdded(singleFilter);
    }
  }, [singleFilter]);

  useEffect(() => {
    if (searchQuery || searchPageNumber || filters) {
      setLoading(true);
      search();
    }
  }, [searchQuery, searchPageNumber, filters]);

  const convertToArray = (array) =>
    array.reduce((
      filter,
      element
    ) => filter.concat(
      Array.isArray(element) ?
        convertToArray(element) : element), []);

  useEffect(() => {
    if (filters) {
      const filterValues = Object.values(filters);
      setActiveFilterCount(convertToArray(filterValues).length);
    }
  }, [filters]);

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
              setCurrentPage(page);
            },
            current: currentPage,
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
      <Layout.Header className={searchQuery ? s.layoutHeader : s.layoutHeaderBeforeSearch}>
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
                  activeFilterCount={activeFilterCount}
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
                hidePanel={!filtersVisible}
              />
            )}
          </Col>
        </Row>
      </Layout.Header>
      <Layout.Content className={searchQuery ? s.main : s.mainBeforeSearch }>
        <Row >
          {renderVideoList()}
        </Row>
      </Layout.Content>
    </>
  );
};

export default LtiView;
