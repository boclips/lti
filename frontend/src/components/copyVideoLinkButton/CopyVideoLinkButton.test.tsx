import { fireEvent, render, screen } from '@testing-library/react';
import React from 'react';
import copy from 'copy-to-clipboard';
import { VideoFactory } from 'boclips-api-client/dist/test-support/VideosFactory';
import CopyVideoLinkButton from './CopyVideoLinkButton';
import setupInitialLocation from '../../testSupport/setupInitialLocation';
import convertApiClientVideo from '../../service/video/convertVideoFromApi';
import { configureMockAxiosService } from '../../testSupport/configureMockAxiosService';

jest.mock('copy-to-clipboard');

const sampleVideo = (id: string) =>
  convertApiClientVideo(VideoFactory.sample({ id }));

describe('CopyVideoLinkButton', () => {
  beforeEach(() => {
    const { apiMock } = configureMockAxiosService();

    apiMock.onPost().reply(200); // we need this because of analytic calls when we click the button
  });

  const clickCopyButton = () => {
    const button = screen.getByRole('button', { name: 'COPY LINK' });
    const mockFn = copy as jest.Mock;
    fireEvent.click(button);
    return mockFn;
  };

  it('copies the link when clicking the button', () => {
    setupInitialLocation(
      'http://dummy.com?show_copy_link=true&embeddable_video_url=http://video.com/{id}',
    );
    render(<CopyVideoLinkButton video={sampleVideo('video-id')} />);
    const copiedToClipboardFn = clickCopyButton();

    expect(copiedToClipboardFn).toHaveBeenCalledWith(
      'http://video.com/video-id',
    );
  });

  it('can handle encoded URL when filling in video id', () => {
    setupInitialLocation(
      'http://localhost:8081/search?embeddable_video_url=http://localhost:8081/%257Bid%257D&show_copy_link=true',
    );
    render(<CopyVideoLinkButton video={sampleVideo('video-id')} />);
    const copiedToClipboardFn = clickCopyButton();

    expect(copiedToClipboardFn).toHaveBeenCalledWith(
      'http://localhost:8081/video-id',
    );
  });
});
