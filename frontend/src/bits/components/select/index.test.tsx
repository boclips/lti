import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option/index';
import { configure } from '@testing-library/dom';
import SelectFilter from './index';

const filters: SelectOption[] = [
  { id: '3-5', label: '3-5', count: 2 },
  { id: '5-9', label: '5-9', count: 2 },
  { id: '9-11', label: '9-11', count: 2 },
  { id: '11-14', label: '11-14', count: 2 },
];

configure({ testIdAttribute: 'data-qa' });

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

    filters.forEach((option) => {
      expect(screen.getByTitle(option.label)).toBeInTheDocument();
    });
  });

  it('Returns selected options when clicking apply', async () => {
    const onApply = jest.fn();
    render(<SelectFilter options={filters} title="Ages" onApply={onApply} />);

    const selectFilter = screen
      .getByTestId('select-dropdown')
      .getElementsByClassName('ant-select-selector')[0];

    await fireEvent.mouseDown(selectFilter);

    const option1 = screen.getByTitle('5-9');
    const option2 = screen.getByTitle('11-14');

    await fireEvent.click(option1);
    await fireEvent.click(option2);

    const applyButton = screen.getByTestId('apply-button');
    await fireEvent.click(applyButton);

    expect(onApply).toHaveBeenCalledWith(['5-9', '11-14']);
  });

  it('show search input when allowSearch true', () => {
    render(
      <SelectFilter
        options={filters}
        title="Ages"
        onApply={jest.fn()}
        searchPlaceholder="Search for subject"
        allowSearch
      />,
    );

    fireEvent.mouseDown(screen.getByText('Ages'));

    expect(screen.getByPlaceholderText('Search for subject')).toBeInTheDocument();
  });
});
