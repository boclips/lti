import { fireEvent, render, screen } from '@testing-library/react';
import React from 'react';
import copy from 'copy-to-clipboard';
import CopyVideoLinkButton from './CopyVideoLinkButton';

jest.mock('copy-to-clipboard');

describe('CopyVideoLinkButton', () => {
  const setupInitialLocation = (location: string) => {
    const previousLocation = global.window.location;
    delete global.window.location;
    global.window.location = { ...previousLocation, href: location };
  };

  it('shows button when show_copy_link and embeddable_video_url are present', () => {
    setupInitialLocation(
      'http://dummy.com?show_copy_link=true&embeddable_video_url=http://video.com/{id}',
    );

    render(<CopyVideoLinkButton videoId="videoId" />);
    const button = screen.getByRole('button', { name: 'COPY LINK' });
    expect(button).toBeInTheDocument();
  });

  it('does not show button when show_copy_link is set to false', () => {
    setupInitialLocation(
      'http://dummy.com?show_copy_link=false&embeddable_video_url=http://video.com/{id}',
    );

    render(<CopyVideoLinkButton videoId="videoId" />);
    const button = screen.queryByRole('button', { name: 'COPY LINK' });
    expect(button).not.toBeInTheDocument();
  });

  it('does not show button when embeddable_video_url is missing', () => {
    setupInitialLocation('http://dummy.com?show_copy_link=true');

    render(<CopyVideoLinkButton videoId="videoId" />);
    const button = screen.queryByRole('button', { name: 'COPY LINK' });
    expect(button).not.toBeInTheDocument();
  });

  it('copies the link when clicking the button', () => {
    setupInitialLocation(
      'http://dummy.com?show_copy_link=true&embeddable_video_url=http://video.com/{id}',
    );

    render(<CopyVideoLinkButton videoId="video-id" />);
    const button = screen.getByRole('button', { name: 'COPY LINK' });

    const mockFn = copy as jest.Mock;

    fireEvent.click(button);
    expect(mockFn).toHaveBeenCalledWith('http://video.com/video-id');
  });
});
