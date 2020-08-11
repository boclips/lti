import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { ChannelFactory } from 'boclips-api-client/dist/test-support';
import FilterPanel from './index';

const testFacets = {
  durations: {
    'PT0S-PT2M': { hits: 53 },
    'PT2M-PT5M': { hits: 94 },
    'PT5M-PT10M': { hits: 44 },
    'PT10M-PT20M': { hits: 48 },
    'PT20M-PT24H': { hits: 45 }
  },
  resourceTypes: {},
  subjects: {},
  ageRanges: {
    '3-5': {
      hits: 3,
    },
  },
};

describe('Filter Panel', () => {
  it('shows subject filters with options from API', async () => {
    render(
      <FilterPanel
        facets={testFacets}
        onApply={jest.fn()}
        subjects={[
          { id: '1', name: 'subject1' },
          { id: '2', name: 'subject2' },
        ]}
      />,
    );

    const subjectSelector = screen.getByText('Subject');
    expect(subjectSelector).toBeVisible();

    fireEvent.mouseDown(subjectSelector);

    expect(screen.getByText('subject1'));
    expect(screen.getByText('subject2'));
  });

  it('shows sources filters with options from API', async () => {
    render(
      <FilterPanel
        facets={testFacets}
        onApply={jest.fn()}
        sources={[
          ChannelFactory.sample({ id: '1', name: 'biology channel', }),
          ChannelFactory.sample({ id: '2', name: 'history channel' }),
        ]}
      />,
    );

    const sourceSelector = screen.getByText('Source');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    expect(screen.getByTitle('biology channel'));
    expect(screen.getByTitle('history channel'));
  });

  it('shows duration filters', async () => {
    render(
      <FilterPanel
        facets={testFacets}
        onApply={jest.fn()}
      />,
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
});
