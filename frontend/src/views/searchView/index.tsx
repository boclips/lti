import React, { useCallback, useEffect, useMemo, useState } from 'react';
import c from 'classnames';
import { Col, Layout, List, Row } from 'antd';
import { Video } from '@boclips-ui/video';
import VideoCardsPlaceholder from '@boclips-ui/video-card-placeholder';
import SearchBar from '@boclips-ui/search-bar-legacy';
import NoResults from '@boclips-ui/no-results';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import { User } from 'boclips-api-client/dist/sub-clients/organisations/model/User';
import { Channel } from 'boclips-api-client/dist/sub-clients/channels/model/Channel';
import { Subject } from 'boclips-api-client/dist/sub-clients/subjects/model/Subject';
import ApiClient from '../../service/client/ApiClient';
import { AppConstants } from '../../types/AppConstants';
import VideoService, {
  ExtendedClientVideo,
} from '../../service/video/VideoService';
import s from './styles.module.less';
import EmptyList from '../../components/EmptyList';
import TitleHeader from '../../components/TitleHeader';
import FilterPanel from '../../components/filterPanel';
import FiltersButton from '../../components/filtersButton';
import ClosableHeader from '../../components/closableHeader';

interface Props {
  renderVideoCard: (
    video: Video,
    query: string,
    showVideoCardV3: boolean,
  ) => React.ReactNode;
  collapsibleFilters?: boolean;
  closableHeader?: boolean;
  useFullWidth?: boolean;
}

const LtiView = ({
  renderVideoCard,
  collapsibleFilters,
  closableHeader,
  useFullWidth,
}: Props) => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [channelsList, setChannelsList] = useState<Channel[]>([]);
  const [subjectsList, setSubjectsList] = useState<Subject[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>();
  const [searchPageNumber, setPageNumber] = useState<number>(0);
  const [totalVideoElements, setTotalVideoElements] = useState<number>(0);
  const [facets, setFacets] = useState<VideoFacets>();
  const [singleFilter, setSingleFilter] = useState<any>(null);
  const [filters, setFilters] = useState<any>(null);
  const [filtersVisible, setFiltersVisible] = useState<boolean>(
    !collapsibleFilters,
  );
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [activeFilterCount, setActiveFilterCount] = useState<number>(0);
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [showSlsTerms, setShowSlsTerms] = useState<boolean>(false);
  const [showVideoCardV3, setShowVideoCardV3] = useState<boolean>(false);

  const videoServicePromise = useMemo(
    () =>
      new ApiClient(AppConstants.API_BASE_URL)
        .getClient()
        .then((client) => new VideoService(client)),
    [],
  );

  const onSearch = useCallback(
    (query?: string, page = 0) => {
      if ((query && searchQuery !== query) || page !== searchPageNumber) {
        setSearchQuery(query);
        setPageNumber(page!);
        setLoading(true);
        setCurrentPage(1);
      }
    },
    [searchPageNumber, searchQuery],
  );

  const handleSearchResults = (searchResults: ExtendedClientVideo<Video>) => {
    setFacets(searchResults.facets);
    setTotalVideoElements(searchResults.pageSpec.totalElements);
    setVideos(searchResults.page);
    setLoading(false);
  };

  const handleFilterAdded = (addedFilter) => {
    setFilters((prevState) => ({ ...prevState, ...addedFilter }));
    setPageNumber(0);
    setCurrentPage(1);
  };

  const search = useCallback(() => {
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
          include_channel_facets: true,
        })
        .then((videosResponse) => {
          handleSearchResults(videosResponse);
        });
    });
  }, [
    filters?.ageRanges,
    filters?.duration,
    filters?.source,
    filters?.subjects,
    searchPageNumber,
    searchQuery,
    videoServicePromise,
  ]);

  useEffect(() => {
    const getCurrentUser = () => {
      videoServicePromise
        .then((client) => client.getCurrentUser())
        .then((it) => setCurrentUser(it))
        .catch(() => setCurrentUser(null));
    };

    const getSubjects = () => {
      videoServicePromise
        .then((client) => client.getSubjects())
        .then((subjects) => setSubjectsList(subjects));
    };

    const getChannels = () => {
      videoServicePromise
        .then((client) => client.getChannels())
        .then((channels) => setChannelsList(channels));
    };

    getCurrentUser();
    getChannels();
    getSubjects();
  }, [videoServicePromise, setChannelsList, setSubjectsList]);

  useEffect(() => {
    if (currentUser) {
      setShowSlsTerms(currentUser.features?.LTI_SLS_TERMS_BUTTON || false);
      setShowVideoCardV3(
        currentUser.features?.LTI_RESPONSIVE_VIDEO_CARD || false,
      );
    }
  }, [currentUser]);

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
  }, [searchQuery, searchPageNumber, filters, search]);

  const convertToArray = useCallback(
    (array) =>
      array.reduce(
        (filter, element) =>
          filter.concat(
            Array.isArray(element) ? convertToArray(element) : element,
          ),
        [],
      ),
    [],
  );

  useEffect(() => {
    if (filters) {
      const filterValues = Object.values(filters);
      setActiveFilterCount(convertToArray(filterValues).length);
    }
  }, [convertToArray, filters]);

  const scrollToTop = () => {
    window.scrollTo(0, 0);
  };

  const showFiltersButton = collapsibleFilters && videos.length > 0;

  const renderVideoList = useMemo(
    () =>
      !loading && videos.length === 0 && !!searchQuery ? (
        <NoResults
          searchQuery={searchQuery}
          filtersApplied={activeFilterCount > 0}
        />
      ) : (
        <Col
          sm={{ span: 24 }}
          md={useFullWidth ? { span: 24 } : { span: 16, offset: 4 }}
        >
          {videos.length > 0 && (
            <section
              className={c(s.numberOfResults, { [s.center]: !useFullWidth })}
            >
              <span>
                {`${
                  totalVideoElements > 500 ? '500+' : totalVideoElements
                } result${videos.length > 1 ? 's' : ''} found:`}
              </span>
            </section>
          )}
          <div
            className={c(s.listWrapper, {
              [s.fullItemWidth]: useFullWidth,
              [s.normalItemWidth]: !useFullWidth,
            })}
          >
            {loading ? (
              <div style={{ marginTop: '24px' }}>
                <VideoCardsPlaceholder />
              </div>
            ) : (
              <List
                itemLayout="vertical"
                size="large"
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
                renderItem={(video: Video) =>
                  renderVideoCard(video, searchQuery!, showVideoCardV3)
                }
              />
            )}
          </div>
        </Col>
      ),
    [
      loading,
      videos,
      searchQuery,
      activeFilterCount,
      useFullWidth,
      totalVideoElements,
      currentPage,
      onSearch,
      renderVideoCard,
      showVideoCardV3,
    ],
  );

  return (
    <>
      <Layout.Header
        className={c({
          [s.layoutHeader]: searchQuery,
          [s.layoutHeaderBeforeSearch]: !searchQuery,
          [s.fullWidth]: useFullWidth,
        })}
      >
        <Row>
          <Col xs={24}>
            {closableHeader ? (
              <ClosableHeader
                title="Video library"
                handleSubmit={(form) => form?.submit()}
                showSlsTerms={showSlsTerms}
              />
            ) : (
              <TitleHeader title="Video Library" showSlsTerms={showSlsTerms} />
            )}
          </Col>
        </Row>
        <Row>
          <Col
            sm={{ span: 24 }}
            md={useFullWidth ? { span: 24 } : { span: 16, offset: 4 }}
            className={c({
              [s.searchBar]: true,
              [s.filtersButton]: showFiltersButton,
            })}
          >
            <div
              className={
                useFullWidth ? s.fullSearchBarContainer : s.searchBarContainer
              }
            >
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
        <Row>
          <Col
            sm={{ span: 24 }}
            md={useFullWidth ? { span: 24 } : { span: 16, offset: 4 }}
            className={s.filtersAlign}
          >
            {(videos.length > 0 || activeFilterCount > 0) && (
              <div
                className={
                  useFullWidth
                    ? s.fullFilterPanelContainer
                    : s.filterPanelContainer
                }
              >
                <FilterPanel
                  channelsList={channelsList}
                  subjectsList={subjectsList}
                  facets={facets}
                  onApply={setSingleFilter}
                  hidePanel={!filtersVisible}
                />
              </div>
            )}
          </Col>
        </Row>
      </Layout.Header>
      <Layout.Content
        className={c({
          [s.main]: searchQuery,
          [s.mainBeforeSearch]: !searchQuery,
          [s.fullWidth]: useFullWidth,
        })}
      >
        <Row>{renderVideoList}</Row>
      </Layout.Content>
    </>
  );
};

export default LtiView;
