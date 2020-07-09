import { render, screen } from '@testing-library/react';
import EmbedVideoButton from './EmbedVideoButton';
import React from 'react';

describe('EmbedVideoButton', () => {
  it('has expected label', async () => {
    render(<EmbedVideoButton />);
    const button = await screen.findByText('+ Add to lesson');
    expect(button).toBeTruthy();
  });

  it('fetches the JWT and posts it to the LMS on click', () => {});
});
