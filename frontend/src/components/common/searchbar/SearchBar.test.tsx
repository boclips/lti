import { render } from '@testing-library/react';
import { SearchBar } from './SearchBar';
import React from 'react';

describe('SearchBar', () => {
  it('renders a search bar', () => {
    const { getByPlaceholderText } = render(<SearchBar />);
    const search = getByPlaceholderText('Search for videos');
    expect(search).toBeInTheDocument();
  });

  it('renders a button to search', () => {
    const { getByRole } = render(<SearchBar />);
    const searchButton = getByRole('button', { name: 'Search' });
    expect(searchButton);
  });
});
