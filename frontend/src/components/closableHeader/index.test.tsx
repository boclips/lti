import React from 'react';
import {
  fireEvent,
  getByLabelText,
  render,
  screen,
} from '@testing-library/react';

import ClosableHeader from './index';
import MockApi from '../../testSupport/mockApi';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

describe('ClosableHeader', () => {
  it('displays correct header', async () => {
    render(
      <ClosableHeader title="my closable header" handleSubmit={jest.fn()} />,
    );

    expect(await screen.findByTestId('closable-header')).toBeInTheDocument();
    expect(await screen.getByText('my closable header')).toBeInTheDocument();
  });

  it('contains a close icon which closes the form on click', async () => {
    const { vanillaInstance } = configureMockAxiosService();

    global.window = Object.create(window);
    const url =
      'http://dummy.com?deep_link_return_url=https://return_url.com/&data=data&deployment_id=id';

    Object.defineProperty(window, 'location', {
      value: {
        href: url,
      },
    });
    MockApi.deepLinkingResponse(vanillaInstance, 'data', 'id', [], 'i am jwt');

    const mockSubmit = jest.fn();
    render(
      <ClosableHeader title="my closable header" handleSubmit={mockSubmit} />,
    );
    const closeIcon = await screen.findByTestId('close-icon');
    expect(closeIcon).toBeInTheDocument();

    fireEvent.click(closeIcon);
    const hiddenForm = await screen.findByRole('form');
    expect(hiddenForm).toBeInTheDocument();

    expect(mockSubmit).toHaveBeenCalled();
    const form: HTMLFormElement = mockSubmit.mock.calls[0][0];
    expect(form.action).toEqual('https://return_url.com/');
    const jwt = getByLabelText(form, 'jwt') as HTMLInputElement;
    expect(jwt.value).toEqual('i am jwt');
  });
});
