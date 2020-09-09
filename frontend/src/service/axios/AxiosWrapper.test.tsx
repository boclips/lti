import React from 'react';
import MockAdapter from 'axios-mock-adapter';
import axios from 'axios';
import { render } from '@testing-library/react';
import AxiosWrapper from './AxiosWrapper';

describe('axioswrapper', () => {
  it('renders the provided component when session is not timed out', () => {
    const axiosInstance = axios.create();
    const axiosMock = new MockAdapter(axiosInstance);
    axiosMock.onGet('https://lti/token').reply(200, 'valid-token');

    const DummyComponent = AxiosWrapper(() => <div>The content</div>);
    
    const wrapper = render(<DummyComponent />);

    expect(wrapper.getByText('The content')).toBeInTheDocument();
  });
});
