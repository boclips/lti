import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { FacetsFactory } from 'boclips-api-client/dist/test-support/FacetsFactory';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { UserFactory } from 'boclips-api-client/dist/test-support/UserFactory';
import FilterPanel from './index';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';

const testFacets = FacetsFactory.sample({
  durations: [
    { id: 'PT0S-PT2M', name: 'PT0S-PT2M', hits: 53 },
    { id: 'PT2M-PT5M', name: 'PT2M-PT5M', hits: 94 },
    { id: 'PT5M-PT10M', name: 'PT5M-PT10M', hits: 44 },
    { id: 'PT10M-PT20M', name: 'PT10M-PT20M', hits: 48 },
    { id: 'PT20M-PT24H', name: 'PT20M-PT24H', hits: 45 },
  ],
  subjects: [
    {
      hits: 32,
      id: '5cb499c9fd5beb428189454d',
      name: 'subject1',
    },
    {
      hits: 88,
      id: '5cb499c9fd5beb428189454c',
      name: 'subject2',
    },
  ],
  ageRanges: [
    {
      hits: 3,
      id: '3-5',
      name: '3-5',
    },
  ],
  resourceTypes: [],
  channels: [
    {
      hits: 111,
      id: 'biology-channel-id',
      name: 'biology channel',
    },
    {
      hits: 212,
      id: 'history-channel-id',
      name: 'history channel',
    },
  ],
  videoTypes: [
    {
      hits: 111,
      id: 'biology-channel-id',
      name: 'biology channel',
    },
    {
      hits: 212,
      id: 'history-channel-id',
      name: 'history channel',
    },
  ],
});

describe('Filter Panel', () => {
  it('shows subject filters with options from API', async () => {
    render(
      <BoclipsClientProvider client={new FakeBoclipsClient()}>
        <FilterPanel facets={testFacets} setShowFilters={jest.fn()} />
      </BoclipsClientProvider>,
    );

    const subjectSelector = screen.getByText('Subject');
    expect(subjectSelector).toBeVisible();

    fireEvent.mouseDown(subjectSelector);

    expect(screen.getByText('subject1'));
    expect(screen.getByText('subject2'));
  });

  it('shows sources filters with options from API', async () => {
    render(
      <BoclipsClientProvider client={new FakeBoclipsClient()}>
        <FilterPanel facets={testFacets} setShowFilters={jest.fn()} />
      </BoclipsClientProvider>,
    );

    const sourceSelector = screen.getByText('Source');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    expect(screen.getByTitle('biology channel'));
    expect(screen.getByTitle('history channel'));
  });

  it('shows duration filters', async () => {
    render(
      <BoclipsClientProvider client={new FakeBoclipsClient()}>
        <FilterPanel facets={testFacets} setShowFilters={jest.fn()} />
      </BoclipsClientProvider>,
    );

    const sourceSelector = screen.getByText('Duration');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    expect(screen.getByTitle('0m - 2m'));
    expect(screen.getByTitle('2m - 5m'));
    expect(screen.getByTitle('5m - 10m'));
    expect(screen.getByTitle('10m - 20m'));
    expect(screen.getByTitle('> 20m'));
  });

  it('shows age range filters', async () => {
    const fakeBoclipsClient = new FakeBoclipsClient();
    fakeBoclipsClient.users.insertCurrentUser(
      UserFactory.sample({ features: { LTI_AGE_FILTER: true } }),
    );
    const wrapper = render(
      <BoclipsClientProvider client={fakeBoclipsClient}>
        <FilterPanel facets={testFacets} setShowFilters={jest.fn()} />
      </BoclipsClientProvider>,
    );

    expect(await wrapper.findByText('Age')).toBeVisible();

    fireEvent.mouseDown(wrapper.getByText('Age'));

    expect(await wrapper.findByText('3 - 5')).toBeInTheDocument();
  });

  it('does not show age range filters if feature is disabled', async () => {
    const fakeClient = new FakeBoclipsClient();
    fakeClient.users.insertCurrentUser(
      UserFactory.sample({ features: { LTI_AGE_FILTER: undefined } }),
    );
    const wrapper = render(
      <BoclipsClientProvider client={fakeClient}>
        <FilterPanel facets={testFacets} setShowFilters={jest.fn()} />
      </BoclipsClientProvider>,
    );

    expect(await wrapper.queryByText('Age')).toBeNull();
  });
});
