import React from 'react';
import { render } from '@testing-library/react';
import FiltersButton from './index';

describe('filtersButton', () => {
  const toggle = jest.fn();
  it('renders a filters applied count when filters hidden', () => {
    const view = render(
      <FiltersButton
        toggleFilters={toggle}
        activeFilterCount={3}
        filtersVisible={false}/>
    );

    expect(view.getByText('3')).toBeInTheDocument();
  });
  it('does not render a filters applied count when filters shown', () => {
    const view = render(
      <FiltersButton
        toggleFilters={toggle}
        activeFilterCount={3}
        filtersVisible/>
    );

    expect(view.queryByText('3')).not.toBeInTheDocument();
  });
});
