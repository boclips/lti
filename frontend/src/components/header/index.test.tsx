import React from 'react';
import { render, screen } from '@testing-library/react';
import Header from './index';

describe('Header', function () {
  it('should render header with search and filter button', async function () {
    render(<Header onSearch={() => null} />);

    expect(screen.getByPlaceholderText('Search...')).toBeInTheDocument();
    expect(screen.getByTestId('filter-button')).toBeInTheDocument();
  });
});
