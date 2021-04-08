import React from 'react';
import { render, screen } from '@testing-library/react';
import Header from './index';
import { fireEvent } from '@testing-library/dom';

describe('Header', function () {
  it('should render header with search and filter button', function () {
    render(<Header onSearch={() => null} />);

    expect(screen.getByPlaceholderText('Search...')).toBeInTheDocument();
    expect(screen.getByTestId('filter-button')).toBeInTheDocument();
  });

  it('should open filter modal when clicked on filter button - mobile', function () {
    //@ts-ignore
    window.innerWidth = 320;

    render(<Header onSearch={() => null} />);

    fireEvent.click(screen.getByTestId('filter-button'));

    expect(screen.getByText('Filters')).toBeInTheDocument();
  });

  it('should close filter modal when clicked on back button - mobile', function () {
    //@ts-ignore
    window.innerWidth = 320;

    render(<Header onSearch={() => null} />);

    fireEvent.click(screen.getByTestId('filter-button'));

    expect(screen.getByText('Filters')).toBeInTheDocument();
  });

  it('should display filters when modal open - mobile', function () {
    //@ts-ignore
    window.innerWidth = 320;

    render(<Header onSearch={() => null} />);

    fireEvent.click(screen.getByTestId('filter-button'));

    expect(screen.getByText('filter')).toBeInTheDocument();
  });
});
