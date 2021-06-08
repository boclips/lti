import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Video } from '@boclips-ui/video';
import { VideoFacets } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import s from './style.module.less';
import VideoService, {
  ExtendedClientVideo,
} from '../../service/video/VideoService';
import NoResults from '../../components/noResults';

import { useBoclipsClient } from '../../hooks/useBoclipsClient';
import Header from '../../components/header';
import SearchResults from '../../components/searchResults';
import SearchResultsSummary from '../../components/searchResultsSummary';
import { useFilters } from '../../hooks/useFilters';
import AppliedFiltersPanel from '../../components/appliedFiltersPanel';
import useFeatureFlags from '../../hooks/useFeatureFlags';

interface ResponsiveSearchViewProps {
  renderVideoCard: (video: Video, query: string) => React.ReactNode;
}

const ResponsiveSearchView = ({
  renderVideoCard,
}: ResponsiveSearchViewProps) => {
  const [videos, setVideos] = useState<Video[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [searchPageNumber, setSearchPageNumber] = useState<number>(0);
  const [totalVideoElements, setTotalVideoElements] = useState<number>(0);
  const [facets, setFacets] = useState<VideoFacets>();
  const { filters } = useFilters();
  const hideAgeRanges = useFeatureFlags()?.LTI_AGE_FILTER === false;

  const [currentPage, setCurrentPage] = useState<number>(1);
  const [activeFilterCount, setActiveFilterCount] = useState<number>(0);

  const apiClient = useBoclipsClient();

  const videoService = useMemo(() => new VideoService(apiClient), [apiClient]);

  const onSearch = useCallback(
    (query: string, page = 0) => {
      if ((query && searchQuery !== query) || page !== searchPageNumber) {
        setSearchQuery(query);
        setSearchPageNumber(page!);
        setLoading(true);
        setCurrentPage(1);
      }
    },
    [searchPageNumber, searchQuery],
  );

  const handleSearchResults = (searchResults: ExtendedClientVideo<Video>) => {
    setFacets(searchResults.facets);
    setTotalVideoElements(searchResults.pageSpec.totalElements);
    if (hideAgeRanges) {
      setVideos(
        searchResults.page.map((video) => ({
          ...video,
          ageRange: undefined,
        })),
      );
    } else {
      setVideos(searchResults.page);
    }
  };

  useEffect(() => {
    setSearchPageNumber(0);
    setCurrentPage(1);
  }, [filters, searchQuery]);

  const search = useCallback(() => {
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
      })
      .finally(() => setLoading(false));
  }, [filters, searchPageNumber, searchQuery, videoService, hideAgeRanges]);

  useEffect(() => {
    if (searchQuery || searchPageNumber) {
      setLoading(true);
      search();
    }
  }, [searchQuery, searchPageNumber, search]);

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

  return (
    <div className={`${s.grid} ${s.container}`}>
      <Header onSearch={onSearch} facets={facets} />

      <AppliedFiltersPanel />

      {!loading && videos.length === 0 && !!searchQuery ? (
        <NoResults
          searchQuery={searchQuery}
          filtersApplied={activeFilterCount > 0}
        />
      ) : (
        <>
          <SearchResultsSummary
            results={videos.length}
            totalVideoElements={totalVideoElements}
          />
          <SearchResults
            currentPage={currentPage}
            onSearch={onSearch}
            setCurrentPage={setCurrentPage}
            results={videos}
            searchQuery={searchQuery}
            totalVideoElements={totalVideoElements}
            loading={loading}
            renderVideoCard={renderVideoCard}
          />
        </>
      )}
    </div>
  );
};

export default ResponsiveSearchView;
