import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import {
  ChannelFactory,
  FakeBoclipsClient,
} from 'boclips-api-client/dist/test-support';
import { SubjectFactory } from 'boclips-api-client/dist/test-support/SubjectsFactory';
import AppliedFiltersPanel from './index';
import { FiltersProvider } from '../../hooks/useFilters';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';

describe('Applied filters panel', () => {
  it('shows badges for all applied filters', async () => {
    const fakeApiClient = new FakeBoclipsClient();

    fakeApiClient.subjects.insertSubject(
      SubjectFactory.sample({ id: 'subject-id', name: 'history subject' }),
    );

    fakeApiClient.channels.insertFixture(
      ChannelFactory.sample({ id: 'bbc-id', name: 'history channel' }),
    );

    const defaultFilters = {
      filters: {
        ageRanges: ['4-6'],
        duration: ['PT0S-PT2M'],
        subjects: ['subject-id'],
        source: ['bbc-id'],
      },
    };

    render(
      <BoclipsClientProvider client={fakeApiClient}>
        <FiltersProvider defaultValue={defaultFilters}>
          <AppliedFiltersPanel />
        </FiltersProvider>
      </BoclipsClientProvider>,
    );

    expect(screen.getByText('4-6'));
    expect(screen.getByText('0m - 2m'));

    await waitFor(() => {
      expect(screen.getByText('history subject'));
      expect(screen.getByText('history channel'));
    });
  });

  it('calls correct set method when removing a badge', async () => {
    const fakeApiClient = new FakeBoclipsClient();
    const filterMock = jest.fn();

    fakeApiClient.subjects.insertSubject(
      SubjectFactory.sample({ id: 'subject-1' }),
    );
    fakeApiClient.subjects.insertSubject(
      SubjectFactory.sample({ id: 'subject-2' }),
    );

    fakeApiClient.channels.insertFixture(
      ChannelFactory.sample({ id: 'bbc-id' }),
    );

    fakeApiClient.channels.insertFixture(
      ChannelFactory.sample({ id: 'nature-channel-id' }),
    );

    const appliedFilters = {
      filters: {
        ageRanges: ['4-6', '7-9'],
        duration: ['PT0S-PT2M', 'PT2M-PT5M'],
        subjects: ['subject-1', 'subject-2'],
        source: ['bbc-id', 'nature-channel-id'],
      },
      setFilters: filterMock,
    };

    render(
      <BoclipsClientProvider client={fakeApiClient}>
        <FiltersProvider defaultValue={appliedFilters}>
          <AppliedFiltersPanel />
        </FiltersProvider>
      </BoclipsClientProvider>,
    );

    const sourceBadge = await screen.findByTestId('bbc-id-remove-button');
    await fireEvent.click(sourceBadge!);

    expect(filterMock).toHaveBeenCalledTimes(1);
    expect(filterMock).toHaveBeenCalledWith(
      expect.objectContaining({
        source: ['nature-channel-id'],
      }),
    );

    const ageBadge = screen.getByTestId('4-6-remove-button');
    await fireEvent.click(ageBadge!);
    expect(filterMock).toHaveBeenCalledTimes(2);
    expect(filterMock).toHaveBeenCalledWith(
      expect.objectContaining({
        ageRanges: ['7-9'],
      }),
    );

    const durationBadge = screen.getByTestId('PT0S-PT2M-remove-button');
    await fireEvent.click(durationBadge!);
    expect(filterMock).toHaveBeenCalledTimes(3);
    expect(filterMock).toHaveBeenCalledWith(
      expect.objectContaining({
        duration: ['PT2M-PT5M'],
      }),
    );

    const subjectBadge = screen.getByTestId('subject-1-remove-button');
    await fireEvent.click(subjectBadge!);
    expect(filterMock).toHaveBeenCalledTimes(4);
    expect(filterMock).toHaveBeenCalledWith(
      expect.objectContaining({
        subjects: ['subject-2'],
      }),
    );
  });
});
