// @ts-nocheck
import React from 'react';
import { render, screen } from '@testing-library/react';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { fireEvent } from '@testing-library/dom';
import { UserFactory } from 'boclips-api-client/dist/test-support/UserFactory';
import { FacetsFactory } from 'boclips-api-client/dist/test-support/FacetsFactory';
import ResponsiveSearchView from './index';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';

describe('LTI test', () => {
  beforeEach(() => {
    configureMockAxiosService();
  });

  const renderWrapper = (apiClient) => {
    return render(
      <BoclipsClientProvider client={apiClient}>
        <ResponsiveSearchView />
      </BoclipsClientProvider>,
    );
  };

  const searchFor = (query: string) => {
    const searchBar = screen.getByTestId('search-input');
    fireEvent.change(searchBar, { target: { value: query } });

    const searchButton = screen.getByText('Search');
    fireEvent.click(searchButton);
  };

  const filterResults = async (
    component: any,
    filterType: string,
    filterSelection: string,
  ) => {
    await fireEvent.mouseDown(component.getByText(filterType));
    await fireEvent.click(component.getByTitle(filterSelection));
    await fireEvent.click(component.getByText('APPLY'));
  };

  const removeFilters = (component: any) => {
    fireEvent.click(component.getByText('CLEAR ALL'));
  };

  it('displays empty render with welcome message', async () => {
    const apiClient = new FakeBoclipsClient();
    apiClient.users.insertCurrentUser(UserFactory.sample());

    renderWrapper(apiClient);

    expect(
      await screen.findByText(
        'Use the search on top to discover inspiring videos',
      ),
    ).toBeInTheDocument();
  });

  it('shows rendered videos on search', async () => {
    const apiClient = new FakeBoclipsClient();

    apiClient.videos.insertVideo(
      VideoFactory.sample({ id: '123', title: 'Hi there' }),
    );
    apiClient.videos.insertVideo(
      VideoFactory.sample({ id: '456', title: 'Hello here' }),
    );

    renderWrapper(apiClient);

    searchFor('Hi');

    await screen.findByText('Hi there');
  });

  it('show filter panel when no results found but filters were applied', async () => {
    const apiClient = new FakeBoclipsClient();

    apiClient.videos.insertVideo(
      VideoFactory.sample({ id: '123', title: 'Hello' }),
    );

    apiClient.videos.setFacets(
      FacetsFactory.sample({
        ageRanges: [
          {
            hits: 3,
            name: '3-5',
            id: '3-5',
          },
        ],
      }),
    );

    renderWrapper(apiClient);

    searchFor('Hello');

    expect(await screen.findByText('FILTER BY:')).toBeInTheDocument();

    filterResults(screen, 'Age', '3 - 5');

    searchFor('nothing');

    expect(
      await screen.findByText(
        'Try again using different keywords or change the filters',
      ),
    );
    expect(screen.getByText('FILTER BY:')).toBeVisible();
  });

  it('does not show filter panel when no results found & no filters were applied', async () => {
    const apiClient = new FakeBoclipsClient();

    renderWrapper(apiClient);

    searchFor('find nothing');

    expect(
      await screen.findByText('Try different words that mean the same thing'),
    ).toBeVisible();
    expect(
      await screen.findByText("Sorry, we couldn't find any results for"),
    ).toBeVisible();
    expect(await screen.findByText('"find nothing"')).toBeVisible();
    expect(await screen.findByText('Check your spelling')).toBeVisible();
    expect(await screen.findByText('Try more general words')).toBeVisible();
    expect(screen.queryByText('FILTER BY:')).toBeNull();
  });

  it(
    'when searching with filters produces no results then filters are removed,' +
      ' the filter panel disappears and no results view changes',
    async () => {
      const apiClient = new FakeBoclipsClient();

      apiClient.videos.insertVideo(
        VideoFactory.sample({ id: '123', title: 'Hi' }),
      );

      apiClient.videos.setFacets(
        FacetsFactory.sample({
          ageRanges: [
            {
              name: '3-5',
              id: '3-5',
              hits: 1,
            },
          ],
        }),
      );

      renderWrapper(apiClient);

      searchFor('Hi');
      expect(await screen.findByText('FILTER BY:')).toBeInTheDocument();

      filterResults(screen, 'Age', '3 - 5');

      expect(await screen.findByText('CLEAR ALL')).toBeVisible();

      searchFor('definitely not a search query :( ');

      expect(
        await screen.findByText(
          /Try again using different keywords or change the filters/,
        ),
      ).toBeVisible();

      removeFilters(screen);

      expect(await screen.findByText(/Check your spelling/)).toBeVisible();

      expect(await screen.queryByText('FILTER BY:')).not.toBeInTheDocument();
    },
  );
});
