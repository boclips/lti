import React from 'react';
import { Select } from 'antd';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';

import s from './styles.module.less';

interface Props {
  ageRanges: { [id: string]: Facet };
  onApply?: () => void;
}

const SelectFilter = ({ ageRanges }: Props) => {
  const values = Object.keys(ageRanges);

  return (
    <Select data-qa="select-dropdown" className={s.selectWrapper}>
      {values.map((it) => (
        <Select.Option key={it} data-qa={`filter-${it}`} value={it}> {it} </Select.Option>
      ))}
    </Select>
  );
};

export default SelectFilter;
