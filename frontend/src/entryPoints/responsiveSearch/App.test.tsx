import React from 'react';
import { render } from '@testing-library/react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';

import App from './App';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

jest.mock('boclips-player');

describe('Search view', () => {
  let apiClient: FakeBoclipsClient;

  beforeAll(() => {
    configureMockAxiosService();

    apiClient = new FakeBoclipsClient();
  });

  it('renders an application with fake api client', () => {
    const wrapper = render(<App apiClient={apiClient} />);

    expect(wrapper.getByText('Search')).toBeInTheDocument();
  });
});
