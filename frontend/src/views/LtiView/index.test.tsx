import React from 'react';
import { render, screen } from '@testing-library/react';
import LtiView from './index';

describe('LTI test', () => {
  it('render search header', async () => {
    render(<LtiView />);
    expect(await screen.findByTestId('header-with-logo')).toBeInTheDocument();
    expect(await screen.findByTitle('Boclips logo')).toBeInTheDocument();
    expect(await screen.findByTestId('search-input')).toBeInTheDocument();
  });
  it('displays empty render with welcome message', async () => {
    render(<LtiView />);
    expect(
      await screen.findByText('Welcome to BoClips Video Library'),
    ).toBeInTheDocument();
    expect(
      await screen.findByText(
        'Use the search on top to find interesting videos',
      ),
    ).toBeInTheDocument();
  });
});
