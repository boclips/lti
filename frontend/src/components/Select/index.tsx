import React, { useState } from 'react';
import { Button, Checkbox, Select } from 'antd';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import c from 'classnames';
import IconOpen from './resources/icon-down.svg';
import s from './styles.module.less';

interface Props {
  options: { [id: string]: Facet };
  title: string;
  onApply: (ageRanges: string[]) => void;
}

const SelectFilter = ({ options, title, onApply }: Props) => {
  const [selected, setSelected] = useState<string[]>([]);
  const [showCount, setShowCount] = useState<number>(0);
  const [dropdownOpen, setDropdownOpen] = useState<boolean>(false);

  const values = Object.keys(options);

  const onClick = () => {
    setShowCount(selected.length);
    onApply(selected);
  };

  const getOptions = () =>
    values.map((it) => ({
      label: <Checkbox
        tabIndex={0}
        data-qa={`filter-${it}`}
        onClick={(e) => e.stopPropagation()}
        className={s.checkboxWrapper}
      >
        <span
          className={selected.includes(it) ? s.checkboxLabelSelected : s.checkboxLabel}>{it}
        </span>
      </Checkbox>,
      value: it,
      title: it
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
        dropdownClassName={s.filterSelectWrapper}
        labelInValue
        value={{
          value: 'Ages',
          label: <div className={s.inputValueWrapper}>
            {title} {showCount > 0 ? <span className={s.countWrapper}>{showCount}</span> : ''}
          </div>
        }}
        mode="multiple"
        onDropdownVisibleChange={(open) => {
          setDropdownOpen(open);
        }}
        onSelect={(valueWithLabel) => {
          manageSelected(valueWithLabel);
        }}

        dropdownRender={(i) => (
          <div>
            <div className={s.optionsWrapper}>
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
          </div>
        )}
      />
      <div className={c(s.arrowIconWrapper, { [s.open]: dropdownOpen })}><IconOpen /></div>
    </div>
  );
};

export default SelectFilter;
