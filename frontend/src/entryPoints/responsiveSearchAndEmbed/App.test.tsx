import React from 'react';
import { fireEvent, render } from '@testing-library/react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { SubjectFactory } from 'boclips-api-client/dist/test-support/SubjectsFactory';
import { AttachmentFactory } from 'boclips-api-client/dist/test-support/AttachmentsFactory';
import App from './App';
import ApiClient from '../../service/client/ApiClient';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

jest.mock('boclips-player');

describe('Search and embed view', () => {
  let fakeApiClient: Promise<FakeBoclipsClient>;

  beforeAll(() => {
    configureMockAxiosService();

    fakeApiClient = new ApiClient(
      'https://api.example1.com',
    ).getClient() as Promise<FakeBoclipsClient>;
  });

  it('loads the video card with the correct badges', async () => {
    const apiClient = await fakeApiClient;
    apiClient.videos.insertVideo(
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
    apiClient.subjects.insertSubject(SubjectFactory.sample({ name: 'Design' }));

    const appComponent = render(<App apiClient={apiClient} />);

    const searchTextInput = appComponent.getByPlaceholderText('Search...');
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
});
