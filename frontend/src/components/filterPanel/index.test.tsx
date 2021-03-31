import React from 'react';
import {
  fireEvent, render, screen, waitFor
} from '@testing-library/react';
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
      name: '3-5'
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
  ]
});

describe('Filter Panel', () => {
  it('shows subject filters with options from API', async () => {
    render(
      <FilterPanel
        facets={testFacets}
        onApply={jest.fn()}
      />,
    );

    const subjectSelector = screen.getByText('Subject');
    expect(subjectSelector).toBeVisible();

    fireEvent.mouseDown(subjectSelector);

    await waitFor(() => {
      expect(screen.getByText('subject1'));
      expect(screen.getByText('subject2'));
    });
  });

  it('shows sources filters with options from API', async () => {
    render(<FilterPanel facets={testFacets} onApply={jest.fn()}/>);

    const sourceSelector = screen.getByText('Source');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    await waitFor(() => {
      expect(screen.getByTitle('biology channel'));
      expect(screen.getByTitle('history channel'));
    });
  });

  it('shows duration filters', async () => {
    render(<FilterPanel facets={testFacets} onApply={jest.fn()}/>);

    const sourceSelector = screen.getByText('Duration');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    await waitFor(() => {
      expect(screen.getByTitle('0m - 2m'));
      expect(screen.getByTitle('2m - 5m'));
      expect(screen.getByTitle('5m - 10m'));
      expect(screen.getByTitle('10m - 20m'));
      expect(screen.getByTitle('> 20m'));
    });
  });

  it('clear all clears the filters', async () => {
    const onApply = jest.fn();
    render(<FilterPanel facets={testFacets} onApply={onApply}/>);

    const sourceSelector = screen.getByText('Duration');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    fireEvent.click(screen.getByTitle('0m - 2m'));
    fireEvent.click(screen.getByTitle('2m - 5m'));

    fireEvent.click(screen.getByText('APPLY'));

    expect(screen.getByText('CLEAR ALL')).toBeInTheDocument();

    fireEvent.click(screen.getByText('CLEAR ALL'));

    waitFor(() => {
      expect(onApply).toBeCalledWith({
        ageRanges: [],
        source: [],
        subjects: [],
        duration: [],
      });
    });
  });

  it('Filters applied panel only shows when filters are selected', () => {
    render(<FilterPanel facets={testFacets} onApply={jest.fn()}/>);

    const sourceSelector = screen.getByText('Duration');

    fireEvent.mouseDown(sourceSelector);

    fireEvent.click(screen.getByTitle('0m - 2m'));
    fireEvent.click(screen.getByTitle('2m - 5m'));

    fireEvent.click(screen.getByText('APPLY'));

    expect(screen.queryByText('Filters applied:')).toBeInTheDocument();
    expect(screen.getAllByText('0m - 2m').length).toEqual(2);
    expect(screen.getAllByText('2m - 5m').length).toEqual(2);
    expect(screen.getAllByText('10m - 20m').length).toEqual(1);

    const removeElements = screen.getAllByTestId(/-remove-button/);
    removeElements.map((element) => fireEvent.click(element));

    waitFor(() => {
      expect(screen.queryByText('Filters applied')).not.toBeInTheDocument();
      expect(screen.getAllByText('0m - 2m').length).toEqual(1);
      expect(screen.getAllByText('2m - 5m').length).toEqual(1);
    });
  });

  it('When filters are removed from applied filters the count shows correct number', async () => {
    render(<FilterPanel facets={testFacets} onApply={jest.fn()}/>);

    const sourceSelector = screen.getByText('Duration');
    expect(sourceSelector).toBeVisible();

    fireEvent.mouseDown(sourceSelector);

    fireEvent.click(screen.getByTitle('0m - 2m'));
    fireEvent.click(screen.getByTitle('2m - 5m'));
    fireEvent.click(screen.getByTitle('10m - 20m'));

    fireEvent.click(screen.getByText('APPLY'));

    const removeElements = screen.getAllByTestId(/-remove-button/);

    await waitFor(() => {
      expect(screen.queryByTestId('count-wrapper')?.innerHTML).toEqual('3');
    });

    fireEvent.click(removeElements[0]);

    expect(screen.queryByTestId('count-wrapper')?.innerHTML).toEqual('2');
  });
  it('shows age range filters', async () => {
    render(<FilterPanel facets={testFacets} onApply={jest.fn()}/>);

    const ageRangeSelector = screen.getByText('Age');
    expect(ageRangeSelector).toBeVisible();

    fireEvent.mouseDown(ageRangeSelector);

    expect(screen.getByTitle('3 - 5'));
    expect(screen.findByText('APPLY'));
  });
});
