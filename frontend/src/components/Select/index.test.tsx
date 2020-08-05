import React from 'react';
import {
  screen, render, fireEvent, waitFor 
} from '@testing-library/react';
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
    render(<SelectFilter options={filters} title="Ages" onApply={() => {}} />);

    expect(screen.getByTestId('select-dropdown')).toBeInTheDocument();
  });

  it('Renders dropdown options', async () => {
    render(<SelectFilter options={filters} title="Ages" onApply={() => {}} />);

    const selectFilter = screen
      .getByTestId('select-dropdown')
      .getElementsByClassName('ant-select-selector')[0];

    await fireEvent.mouseDown(selectFilter);

    expect(
      screen.getByTestId(`filter-${Object.keys(filters)[0]}`),
    ).toBeInTheDocument();
  });

  it('Returns selected options when clicking apply', async () => {
    const onApply = jest.fn();
    render(<SelectFilter options={filters} title="Ages" onApply={onApply} />);

    const selectFilter = screen
      .getByTestId('select-dropdown')
      .getElementsByClassName('ant-select-selector')[0];

    await fireEvent.mouseDown(selectFilter);

    // const selectFilter1 = screen.getAllByText('5-9')[0];
    // const selectFilter3 = screen.getAllByText('11-14')[0];
    const selectFilter1 = screen
      .getByTestId(`filter-${Object.keys(filters)[1]}`);
    const selectFilter3 = screen
      .getByTestId(`filter-${Object.keys(filters)[3]}`);

    await fireEvent.click(selectFilter1);
    await fireEvent.click(selectFilter3);

    const applyButton = screen.getByTestId('apply-button');
    await fireEvent.click(applyButton);
    // console.log(screen.debug());
    // console.log(onApply.mock.calls);

    await waitFor(() => {
      // console.log(onApply.mock.calls);
      expect(onApply).toHaveBeenCalledWith(['5-9', '11-14']);
    }, { timeout: 3000 });


  });
});
