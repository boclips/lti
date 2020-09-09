import React from 'react';
import MockAdapter from 'axios-mock-adapter';
import axios from 'axios';
import { render } from '@testing-library/react';
import { AxiosWrapper } from './AxiosWrapper';

describe('axioswrapper', () => {
  it('renders the provided component when session is not timed out', () => {
    const axiosInstance = axios.create();
    const axiosMock = new MockAdapter(axiosInstance);
    axiosMock.onGet('https://lti/token').reply(200, 'valid-token');

    const DummyComponent = AxiosWrapper(() => <div>The content</div>);
    
    const wrapper = render(<DummyComponent />);

    expect(wrapper.getByText('The content')).toBeInTheDocument();
  });
  
  it('renders the error page when session is timed out', async () => {
    const original = axios.create();
    const axiosMock = new MockAdapter(original);
    axiosMock.onGet('.*/token').reply(401, 'expired');

    const DummyComponent = AxiosWrapper(() => <div>The content</div>);
    const wrapper = render(<DummyComponent/>);
    await axios.create().get('any');

    expect(wrapper.queryByText('The content')).toBeNull();
    expect(wrapper.getByText('Please try closing and re-opening the video library')).toBeInTheDocument();
  });
});
