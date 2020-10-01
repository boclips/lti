import { render, screen } from '@testing-library/react';
import { fireEvent } from '@testing-library/dom';
import React from 'react';
import AboutModal from '../sls/AboutModal';

describe('About button', () => {
  it('opens info modal after click', async () => {
    render(<AboutModal />);

    const aboutButton = screen.getByText('About the app and services');

    expect(aboutButton).toBeVisible();
    fireEvent.click(aboutButton);
    expect(await screen.findByText('How does it work?')).toBeVisible();
  });
});
