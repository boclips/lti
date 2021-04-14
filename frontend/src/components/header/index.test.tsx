import React from 'react';
import { render, screen } from '@testing-library/react';
import Header from './index';
import setViewPortWidth from '../../testSupport/setViewPortWidth';

describe('Header', () => {
  it('should render header with search and filter button', () => {
    render(<Header onSearch={() => null} />);

    expect(screen.getByPlaceholderText('Search...')).toBeInTheDocument();
    expect(screen.getByTestId('filter-button')).toBeInTheDocument();
  });

  [
    {
      width: 1440,
      viewportName: 'desktop',
      showSearchIconOnly: false,
      showFilterIconOnly: false,
    },
    {
      width: 768,
      viewportName: 'tablet',
      showSearchIconOnly: true,
      showFilterIconOnly: false,
    },
    {
      width: 320,
      viewportName: 'mobile',
      showSearchIconOnly: true,
      showFilterIconOnly: true,
    },
  ].forEach((viewport) => {
    it(`should display correct search button for ${viewport.viewportName} view`, () => {
      setViewPortWidth(viewport.width);
      render(<Header onSearch={() => null} />);

      const searchButton = screen.getByRole('button', { name: 'search' });
      expect(searchButton).toBeInTheDocument();

      if (viewport.showSearchIconOnly) {
        expect(searchButton).not.toHaveTextContent(/search/i);
      } else {
        expect(searchButton).toHaveTextContent(/search/i);
      }
    });

    it(`should display correct filter button for ${viewport.viewportName} view`, () => {
      setViewPortWidth(viewport.width);
      render(<Header onSearch={() => null} />);

      const searchButton = screen.getByRole('button', { name: 'filter' });
      expect(searchButton).toBeInTheDocument();

      if (viewport.showSearchIconOnly) {
        expect(searchButton).not.toHaveTextContent(/filter/i);
      } else {
        expect(searchButton).toHaveTextContent(/filter/i);
      }
    });
  });
});
