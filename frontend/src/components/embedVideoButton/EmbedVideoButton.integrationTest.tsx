import {
  render,
  screen,
  fireEvent,
  getByLabelText,
} from '@testing-library/react';
import React from 'react';
import { FakeBoclipsClient } from 'boclips-api-client/dist/test-support';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import { Link } from 'boclips-api-client/dist/sub-clients/common/model/LinkEntity';
import EmbedVideoButton from './EmbedVideoButton';
import MockApi from '../../testSupport/mockApi';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';
import ApiClient from '../../service/client/ApiClient';
import convertApiClientVideo from '../../service/video/convertVideoFromApi';
import eventually from '../../testSupport/eventually';

describe('EmbedVideoButton', () => {
  global.window = Object.create(window);
  const url =
    'http://dummy.com?deep_link_return_url=https://return_url.com/&data=data&deployment_id=id';

  Object.defineProperty(window, 'location', {
    value: {
      href: url,
    },
  });

  it('has expected label', () => {
    render(
      <EmbedVideoButton
        onSubmit={jest.fn()}
        video={convertApiClientVideo(VideoFactory.sample({ id: '123' }))}
      />,
    );
    const button = screen.getByRole('button', { name: '+' });
    expect(button).toBeInTheDocument();
  });

  it('fetches the JWT and posts it to the LMS on click', async () => {
    const { apiMock, vanillaInstance } = configureMockAxiosService();

    apiMock.onPost().reply(200); // we need this because of analytic calls when we click the button

    MockApi.deepLinkingResponse(
      vanillaInstance,
      'data',
      'id',
      ['123'],
      'i am jwt',
    );
    const onSubmitMock = jest.fn();
    render(
      <EmbedVideoButton
        onSubmit={onSubmitMock}
        video={convertApiClientVideo(VideoFactory.sample({ id: '123' }))}
      />,
    );
    const button = screen.getByRole('button', { name: '+' });
    fireEvent.click(button);

    const hiddenForm = await screen.findByRole('form');
    expect(hiddenForm).toBeInTheDocument();

    expect(onSubmitMock).toHaveBeenCalled();

    const form: HTMLFormElement = onSubmitMock.mock.calls[0][0];
    expect(form.action).toEqual('https://return_url.com/');
    const jwt = getByLabelText(form, 'jwt') as HTMLInputElement;
    expect(jwt.value).toEqual('i am jwt');
  });

  it('tracks embedding the video', async () => {
    const { baseMock, apiMock, vanillaInstance } = configureMockAxiosService();
    apiMock.onPost().reply(200);
    baseMock.onGet().reply(200);

    const apiClientPromise = new ApiClient(
      'https://api.example.com',
    ).getClient() as Promise<FakeBoclipsClient>;
    const apiClient = await apiClientPromise;

    MockApi.deepLinkingResponse(
      vanillaInstance,
      'data',
      'id',
      ['id-1'],
      'i am jwt',
    );

    const video = VideoFactory.sample({
      id: 'id-1',
      links: {
        self: new Link({ href: '/v1/videos/id-1' }),
        logInteraction: new Link({
          href: '/v1/videos/id-1/events?logVideoInteraction=true&type={type}',
        }),
      },
    });
    apiClient.videos.insertVideo(video);

    render(
      <EmbedVideoButton
        onSubmit={jest.fn}
        video={convertApiClientVideo(video)}
      />,
    );
    const button = screen.getByRole('button', { name: '+' });
    fireEvent.click(button);

    await eventually(() => {
      expect(apiMock.history.post[0].url).toEqual(
        '/v1/videos/id-1/events?logVideoInteraction=true&type=LTI_SEARCH_AND_EMBED',
      );
    });
  });
});
