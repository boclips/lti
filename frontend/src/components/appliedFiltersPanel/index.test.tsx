import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import AppliedFiltersPanel from './index';

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

  it('calls correct set method when removing a badge', async () => {
    const ageFilterMock = jest.fn();
    const durationFilterMock = jest.fn();
    const subjectFilterMock = jest.fn();
    const sourceFilterMock = jest.fn();
    render(
      <AppliedFiltersPanel
        subjectList={[{
          id: 'subject-1',
          name: 'history subject'
        }, {
          id: 'subject-2',
          name: 'art subject'
        }]}
        appliedFilters={{
          ageRanges: ['4-6', '7-9'],
          duration: ['PT0S-PT2M', 'PT2M-PT5M'],
          subjects: ['subject-1', 'subject-2'],
          source: ['BBC', 'nature channel']
        }}
        setSubjectFilter={subjectFilterMock}
        setSourceFilter={sourceFilterMock}
        setDurationFilter={durationFilterMock}
        setAgeRangeFilter={ageFilterMock}
      />,
    );

    const sourceBadge = screen.getByTestId('BBC-remove-button');
    await fireEvent.click(sourceBadge!);
    expect(sourceFilterMock).toHaveBeenCalledTimes(1);
    expect(sourceFilterMock).toHaveBeenCalledWith(['nature channel']);

    const ageBadge = screen.getByTestId('4-6-remove-button');
    await fireEvent.click(ageBadge!);
    expect(ageFilterMock).toHaveBeenCalledTimes(1);
    expect(ageFilterMock).toHaveBeenCalledWith(['7-9']);

    const durationBadge = screen.getByTestId('PT0S-PT2M-remove-button');
    await fireEvent.click(durationBadge!);
    expect(durationFilterMock).toHaveBeenCalledTimes(1);
    expect(durationFilterMock).toHaveBeenCalledWith(['PT2M-PT5M']);

    const subjectBadge = screen.getByTestId('subject-1-remove-button');
    await fireEvent.click(subjectBadge!);
    expect(subjectFilterMock).toHaveBeenCalledTimes(1);
    expect(subjectFilterMock).toHaveBeenCalledWith(['subject-2']);
  });

  it('does not display header when no filters are applied', async () => {
    render(
      <AppliedFiltersPanel
        subjectList={[]}
        appliedFilters={{
          ageRanges: undefined,
          duration: [],
          subjects: [],
          source: undefined
        }}
        setSubjectFilter={jest.fn()}
        setSourceFilter={jest.fn()}
        setDurationFilter={jest.fn()}
        setAgeRangeFilter={jest.fn()}
      />,
    );

    expect(await screen.queryByText('Filters applied')).not.toBeInTheDocument();
  });
});
