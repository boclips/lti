import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import AppliedFiltersPanel from './index';

const testFacets = {
  durations: {
    'PT0S-PT2M': { hits: 53 },
    'PT2M-PT5M': { hits: 94 },
    'PT5M-PT10M': { hits: 44 },
    'PT10M-PT20M': { hits: 48 },
    'PT20M-PT24H': { hits: 45 }
  },
  resourceTypes: {},
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

describe('Applied filters panel', () => {
  it('shows badges for all applied filters', () => {
    render(
      <AppliedFiltersPanel
        subjectList={[{
          id: 'subject-id',
          name: 'history subject'
        }]}
        appliedFilters={{
          ageRanges: ['4-6'],
          duration: ['PT0S-PT2M'],
          subjects: ['subject-id'],
          source: ['BBC']
        }}
        setSubjectFilter={jest.fn()}
        setSourceFilter={jest.fn()}
        setDurationFilter={jest.fn()}
        setAgeRangeFilter={jest.fn()}
      />,
    );

    expect(screen.getByText('4-6'));
    expect(screen.getByText('0m - 2m'));
    expect(screen.getByText('history subject'));
    expect(screen.getByText('BBC'));
  });
  
  it('shows badges for all applied filters', async () => {
    const sourceFilterCalled = jest.fn();
    render(
      <AppliedFiltersPanel
        subjectList={[{
          id: 'subject-id',
          name: 'history subject'
        }]}
        appliedFilters={{
          ageRanges: [],
          duration: [],
          subjects: [],
          source: ['BBC', 'nature channel']
        }}
        setSubjectFilter={jest.fn()}
        setSourceFilter={sourceFilterCalled}
        setDurationFilter={jest.fn()}
        setAgeRangeFilter={jest.fn()}
      />,
    );
    const sourceFilter = screen.getByText('BBC').closest('close-icon');

    await fireEvent.click(sourceFilter);
    
    expect(await screen.findByText('BBC')).not.toBeInTheDocument();
    expect(sourceFilterCalled).toHaveBeenCalledTimes(1);
    expect(sourceFilterCalled).toHaveBeenCalledWith(['nature channel']);
  });
});
