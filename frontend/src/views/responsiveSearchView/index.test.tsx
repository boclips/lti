import React from 'react';
import { render, screen } from '@testing-library/react';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { fireEvent } from '@testing-library/dom';
import { FacetsFactory } from 'boclips-api-client/dist/test-support/FacetsFactory';
import { UserFactory } from 'boclips-api-client/dist/test-support/UserFactory';
import ResponsiveSearchView from './index';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';
import { FiltersProvider } from '../../hooks/useFilters';
import { renderResponsiveBaseVideoCard } from '../../entryPoints/responsiveSearch/App';

describe('LTI test', () => {
  beforeEach(() => {
    configureMockAxiosService();
  });

  const renderWrapper = (apiClient) => {
    return render(
      <BoclipsClientProvider client={apiClient}>
        <FiltersProvider>
          <ResponsiveSearchView
            renderVideoCard={renderResponsiveBaseVideoCard}
          />
        </FiltersProvider>
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
  };

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

    apiClient.users.insertCurrentUser(
      UserFactory.sample({ features: { LTI_AGE_FILTER: true } }),
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

    await fireEvent.click(await screen.findByTestId('filter-button'));

    filterResults(screen, 'Age', '3 - 5');

    searchFor('nothing');

    expect(
      await screen.findByText(
        'Try again using different keywords or change the filters',
      ),
    );
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
});
