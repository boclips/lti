import React, { useState } from 'react';
import { Button, Checkbox, Select } from 'antd';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import s from './styles.module.less';

interface Props {
  options: { [id: string]: Facet };
  onApply: (ageRanges: string[]) => void;
}

const SelectFilter = ({ options, onApply }: Props) => {
  const [selected, setSelected] = useState<string[]>([]);
  const [showCount, setShowCount] = useState<number>();

  const values = Object.keys(options);

  const onClick = () => {
    setShowCount(selected.length);
    console.log(`selected: ${selected}`);
    onApply(selected);
  };

  const getOptions = () =>
    values.map((it) => ({
      label: <Checkbox tabIndex={0} 
        onClick={(e) => e.stopPropagation()}
        className={s.checkboxWrapper}>
        {it}
      </Checkbox>,
      value: it,
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
        onChange={() => null}
        options={getOptions()}
        placeholder="Ages"
        data-qa="select-dropdown"
        className={s.selectWrapper}
        dropdownClassName={s.filterSelectWrapper}
        labelInValue
        value={{ value: '', label: <div className={s.inputValueWrapper}> Ages {showCount && showCount} </div> }}
        mode="multiple"
        showSearch={false}
        onSelect={(valueWithLabel) => {
          manageSelected(valueWithLabel);
        }}
        dropdownRender={(i) => (
          <div>
            {i}
            <hr/>
            <Button onClick={onClick}> Apply </Button>
          </div>
        )}
      />
    </>
  );
};

export default SelectFilter;
