import React from 'react';
import { render, screen } from '@testing-library/react';

import TitleHeader from './index';

describe('TitleHeader', () => {
  it('displays correct header', async () => {
    render(<TitleHeader title="Video Library" />);

    expect(await screen.findByTestId('title-header')).toBeInTheDocument();
    expect(await screen.getByText('Video Library')).toBeInTheDocument();
  });
});
