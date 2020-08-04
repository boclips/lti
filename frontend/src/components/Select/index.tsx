import React, { ReactHTMLElement, useState } from 'react';
import { Button, Select } from 'antd';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';

import s from './styles.module.less';

interface Props {
  options: { [id: string]: Facet };
  onApply: (ageRanges: string[]) => void;
}

const SelectFilter = ({ options, onApply }: Props) => {
  const [selected, setSelected] = useState<string[]>([]);
  const values = Object.keys(options);

  const onClick = () => {
    console.log('selected: '+selected);
    onApply(selected);
  }

  return (
    <Select
      onChange={(e) => {
        console.log(e);
        // setSelected(e.target.value);
      }}
      data-qa="select-dropdown"
      className={s.selectWrapper}
    >
      {values.map((it) => (
        <Select.Option key={it} data-qa={`filter-${it}`} value={it}> {it} </Select.Option>
      ))}
      <Button onClick={onClick}/>
    </Select>
  );
};

export default SelectFilter;
