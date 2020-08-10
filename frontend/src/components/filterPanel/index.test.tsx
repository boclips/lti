import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import FilterPanel from './index';

const testFacets = {
  durations: {},
  resourceTypes: {},
  subjects: {},
  ageRanges: {
    '3-5': {
      hits: 3,
    },
  },
};

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
