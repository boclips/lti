import React from 'react';
import { screen, render, fireEvent } from '@testing-library/react';
import SelectFilter from './index';

const filters = {
  '3-5': {
    hits: 1,
  },
  '5-9': {
    hits: 1,
  },
  '9-11': {
    hits: 3,
  },
  '11-14': {
    hits: 3,
  },
  '14-16': {
    hits: 0,
  },
  '16-99': {
    hits: 0,
  },
};

describe('Select dropdown', () => {
  it('Renders select dropdown', () => {
    render(<SelectFilter ageRanges={filters} />);

    expect(screen.getByTestId('select-dropdown')).toBeInTheDocument();
  });

  it('Renders select dropdown', async () => {
    render(<SelectFilter ageRanges={filters} />);

    const selectFilter = screen
      .getByTestId('select-dropdown')
      .getElementsByClassName('ant-select-selector')[0];

    await fireEvent.mouseDown(selectFilter);

    expect(
      screen.getByTestId(`filter-${Object.keys(filters)[0]}`),
    ).toBeInTheDocument();
  });
});
