import React, { useState } from 'react';
import {
  Button, Checkbox, Input, Select 
} from 'antd';
import c from 'classnames';
import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option';
import IconOpen from './resources/icon-down.svg';
import s from './styles.module.less';

interface Props {
  options: SelectOption[];
  title: string;
  onApply: (selected: string[]) => void;
  allowSearch?: boolean;
  searchPlaceholder?: string;
}

const SelectFilter = ({
  options, title, onApply, allowSearch = false, searchPlaceholder
}: Props) => {
  const [selected, setSelected] = useState<string[]>([]);
  const [showCount, setShowCount] = useState<number>(0);
  const [dropdownOpen, setDropdownOpen] = useState<boolean>(false);

  const onClick = () => {
    setShowCount(selected.length);
    onApply(selected);
  };

  const getOptions = () =>
    options.map((it) => ({
      label: <Checkbox
        tabIndex={0}
        data-qa={`filter-${it}`}
        className={s.checkboxWrapper}
      >
        <span
          className={selected.includes(it.id)
            ? s.checkboxLabelSelected : s.checkboxLabel}>{it.label}
        </span>
      </Checkbox>,
      value: it.id,
      title: it.label,
    }));

  const manageSelected = (selectedValue: any) => {
    const { value } = selectedValue;
    if (selected.includes(value)) {
      const newSelected = selected.filter((v) => v !== value);
      setSelected(newSelected);
    } else {
      setSelected([...selected, value]);
    }
  };

  return (
    <div className={s.main}>
      <Select
        showSearch={false}
        options={getOptions()}
        menuItemSelectedIcon={null}
        data-qa="select-dropdown"
        className={c(s.selectWrapper, { [s.filtersSelected]: showCount > 0 })}
        dropdownClassName={c(s.filterSelectWrapper, { [s.wideDropdown]: allowSearch })}
        labelInValue
        value={[{
          value: title,
          label: <div className={s.inputValueWrapper}>
            {title} {showCount > 0 ? <span className={s.countWrapper}>{showCount}</span> : ''}
          </div>
        }]}
        mode="multiple"
        onDropdownVisibleChange={(open) => {
          setDropdownOpen(open);
        }}
        onSelect={(valueWithLabel) => {
          manageSelected(valueWithLabel);
        }}
        dropdownMatchSelectWidth={!allowSearch}
        dropdownRender={(i) => (
          <>
            <div className={s.optionsWrapper}>
              {allowSearch && <div className={s.searchInputWrapper}>
                <Input type="text" placeholder={searchPlaceholder} />
              </div>}
              {i}
            </div>
            <div className={s.buttonWrapper}>
              <Button
                type="primary"
                className={s.applyButton}
                data-qa="apply-button"
                disabled={!selected.length}
                onClick={onClick}
              > APPLY
              </Button>
            </div>
          </>
        )}
      />
      <div className={c(s.arrowIconWrapper, { [s.open]: dropdownOpen })}><IconOpen /></div>
    </div>
  );
};

export default SelectFilter;
