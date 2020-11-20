import {
  render,
  screen,
  fireEvent,
  getByLabelText,
} from '@testing-library/react';
import React from 'react';
import EmbedVideoButton from './EmbedVideoButton';
import MockApi from '../../testSupport/mockApi';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

describe('EmbedVideoButton', () => {
  it('has expected label', () => {
    render(<EmbedVideoButton onSubmit={jest.fn()} videoId="123" />);
    const button = screen.getByRole('button', { name: '+ Add to lesson' });
    expect(button).toBeInTheDocument();
  });

  it('fetches the JWT and posts it to the LMS on click', async () => {
    const { apiMock, vanillaInstance } = configureMockAxiosService();

    apiMock.onPost().reply(200); // we need this because of analytic calls when we click the button
    global.window = Object.create(window);
    const url =
      'http://dummy.com?deep_link_return_url=https://return_url.com/&data=data&deployment_id=id';

    Object.defineProperty(window, 'location', {
      value: {
        href: url,
      },
    });

    MockApi.deepLinkingResponse(vanillaInstance, 'data', 'id', ['123'], 'i am jwt');
    const onSubmitMock = jest.fn();
    render(<EmbedVideoButton onSubmit={onSubmitMock} videoId="123" />);
    const button = screen.getByRole('button', { name: '+ Add to lesson' });
    fireEvent.click(button);

    const hiddenForm = await screen.findByRole('form');
    expect(hiddenForm).toBeInTheDocument();

    expect(onSubmitMock).toHaveBeenCalled();

    const form: HTMLFormElement = onSubmitMock.mock.calls[0][0];
    expect(form.action).toEqual('https://return_url.com/');
    const jwt = getByLabelText(form, 'jwt') as HTMLInputElement;
    expect(jwt.value).toEqual('i am jwt');
  });
});
