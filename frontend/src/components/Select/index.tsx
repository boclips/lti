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
  const [selectedCount, setSelectedCount] = useState<number>(0);
  const values = Object.keys(options);

  const onClick = () => {
    console.log('selected: ' + selected);
    onApply(selected);
  };

  return (
    <Select
      onChange={(value) => {
        console.log(value);
      }}
      showArrow
      options={values.map((it) => ({
        label: <Checkbox> {it} </Checkbox>,
        value: it,
      }))}
      placeholder="Ages"
      data-qa="select-dropdown"
      className={s.selectWrapper}
      labelInValue={true}
      // value={{ value: '', label: <div>Ages: {selectedCount}</div> }}
      mode="multiple"
      showSearch={false}
      onSelect={(e) => {
        console.log(e);
      }}
      onClick={(e) => {
        console.log(e);
      }}
      dropdownRender={(i) => {
        return (
          <div>
            {i}
            <hr />
            <Button onClick={onClick}> Apply </Button>
          </div>
        );
      }}
    />
  );
};

export default SelectFilter;
