import React, { useState } from 'react';
import { Button, Checkbox, Select } from 'antd';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import s from './styles.module.less';

interface Props {
  options: { [id: string]: Facet };
  title: string;
  onApply: (ageRanges: string[]) => void;
}

const SelectFilter = ({ options, title, onApply }: Props) => {
  const [selected, setSelected] = useState<string[]>([]);
  const [showCount, setShowCount] = useState<number>(0);

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
        {it}
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
    <>
      <Select
        showArrow
        options={getOptions()}
        optionLabelProp="data-qa"
        menuItemSelectedIcon={null}
        data-qa="select-dropdown"
        className={s.selectWrapper}
        dropdownClassName={s.filterSelectWrapper}
        labelInValue
        value={{
          value: 'Ages',
          label: <div className={s.inputValueWrapper}>
            {title} {showCount > 0 ? showCount : ''}
          </div>
        }}
        mode="multiple"
        showSearch={false}
        onSelect={(valueWithLabel) => {
          manageSelected(valueWithLabel);
        }}
        dropdownRender={(i) => (
          <div>
            {i}
            <hr/>
            <Button data-qa="apply-button" onClick={onClick}> Apply </Button>
          </div>
        )}
      />
    </>
  );
};

export default SelectFilter;
