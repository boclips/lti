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
  resourceTypes: {
    'biology channel': {
      hits: 3
    },
    'history channel': {
      hits: 5
    }
  },
  subjects: {
    '5cb499c9fd5beb428189454c': { hits: 88 },
    '5cb499c9fd5beb428189454d': { hits: 32 }
  },
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
          { id: '5cb499c9fd5beb428189454c', name: 'subject1' },
          { id: '5cb499c9fd5beb428189454d', name: 'subject2' },
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
        sources={[ChannelFactory.sample({ id: '1', name: 'biology channel' }),
          ChannelFactory.sample({ id: '1', name: 'history channel' })]}
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

  it('clear all clears the filters', async () => {
    const onApply = jest.fn();
    
    render(
      <FilterPanel
        facets={testFacets}
        onApply={onApply}
      />,
    );

    const sourceSelector = screen.getByText('Duration');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    fireEvent.click(screen.getByTitle('0m - 2m'));
    fireEvent.click(screen.getByTitle('2m - 5m'));

    fireEvent.click(screen.getByText('APPLY'));
    
    expect(screen.getByText('CLEAR ALL')).toBeInTheDocument();
    
    fireEvent.click(screen.getByText('CLEAR ALL'));
    
    expect(onApply).toBeCalledWith({
      ageRanges: [], source: [], subjects: [], duration: [] 
    });
  });
});
