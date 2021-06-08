import React from 'react';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { SubjectFactory } from 'boclips-api-client/dist/test-support/SubjectsFactory';
import { AttachmentFactory } from 'boclips-api-client/dist/test-support/AttachmentsFactory';
import { UserFactory } from 'boclips-api-client/dist/test-support/UserFactory';
import App from './App';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

jest.mock('boclips-player');

describe('Search and embed view', () => {
  let fakeApiClient: FakeBoclipsClient;

  beforeEach(() => {
    configureMockAxiosService();

    fakeApiClient = new FakeBoclipsClient();
  });

  it('loads the video card with the correct badges', async () => {
    fakeApiClient.videos.insertVideo(
      VideoFactory.sample({
        id: '1',
        title: 'goats',
        subjects: [SubjectFactory.sample({ name: 'Design' })],
        attachments: [AttachmentFactory.sample({ id: 'i am attachment' })],
        ageRange: {
          min: 3,
          max: 5,
        },
        bestFor: [
          {
            label: 'Hook',
          },
        ],
      }),
    );
    fakeApiClient.subjects.insertSubject(
      SubjectFactory.sample({ name: 'Design' }),
    );
    fakeApiClient.users.insertCurrentUser(
      UserFactory.sample({ features: { LTI_AGE_FILTER: true } }),
    );

    const appComponent = render(<App apiClient={fakeApiClient} />);

    const searchTextInput = appComponent.getByPlaceholderText(
      'Search for videos',
    );
    fireEvent.change(searchTextInput, { target: { value: 'goats' } });

    const searchButton = appComponent.getByText('Search').closest('button');
    fireEvent.click(searchButton!);

    expect(await appComponent.findByText('Design')).toBeVisible();

    expect(await appComponent.queryByText('Best for:')).not.toBeInTheDocument();
    expect(await appComponent.queryByText('Hook')).not.toBeInTheDocument();

    expect(await appComponent.findByText('Ages 3-5')).toBeVisible();

    expect(
      await appComponent.queryByTestId('attachment-badge'),
    ).not.toBeInTheDocument();
  });

  it(`doesn't display age range badges when user has feature disabled`, async () => {
    fakeApiClient.videos.insertVideo(
      VideoFactory.sample({
        id: '1',
        title: 'goats',
        subjects: [SubjectFactory.sample({ name: 'Design' })],
        ageRange: {
          min: 3,
          max: 5,
        },
      }),
    );
    fakeApiClient.subjects.insertSubject(
      SubjectFactory.sample({ name: 'Design' }),
    );

    fakeApiClient.users.insertCurrentUser(
      UserFactory.sample({ features: { LTI_AGE_FILTER: false } }),
    );
    fakeApiClient.users.setCurrentUserFeatures({ LTI_AGE_FILTER: false });

    const appComponent = render(<App apiClient={fakeApiClient} />);

    const searchTextInput = appComponent.getByPlaceholderText(
      'Search for videos',
    );
    fireEvent.change(searchTextInput, { target: { value: 'goats' } });

    const searchButton = appComponent.getByText('Search').closest('button');
    fireEvent.click(searchButton!);

    expect(await appComponent.findByText('Design')).toBeVisible();
    expect(appComponent.queryByText('Ages 3-5')).toBeNull();
  });

  it('sends a PageRender event when mounting', async () => {
    render(<App apiClient={fakeApiClient} />);

    await waitFor(
      () => {
        const events = fakeApiClient.events.getEvents();
        expect(events.length).toEqual(1);
      },
      { timeout: 1500 },
    );
  });
});
