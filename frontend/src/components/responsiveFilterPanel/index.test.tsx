import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { FacetsFactory } from 'boclips-api-client/dist/test-support/FacetsFactory';
import FilterPanel from './index';

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
    render(<FilterPanel facets={testFacets} setShowFilters={jest.fn()} />);

    const subjectSelector = screen.getByText('Subject');
    expect(subjectSelector).toBeVisible();

    fireEvent.mouseDown(subjectSelector);

    expect(screen.getByText('subject1'));
    expect(screen.getByText('subject2'));
  });

  it('shows sources filters with options from API', async () => {
    render(<FilterPanel facets={testFacets} setShowFilters={jest.fn()} />);

    const sourceSelector = screen.getByText('Source');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    expect(screen.getByTitle('biology channel'));
    expect(screen.getByTitle('history channel'));
  });

  it('shows duration filters', async () => {
    render(<FilterPanel facets={testFacets} setShowFilters={jest.fn()} />);

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
    const wrapper = render(
      <FilterPanel facets={testFacets} setShowFilters={jest.fn()} />,
    );

    expect(wrapper.getByText('Age')).toBeVisible();

    fireEvent.mouseDown(wrapper.getByText('Age'));

    expect(await wrapper.findByText('3 - 5')).toBeInTheDocument();
  });
});
