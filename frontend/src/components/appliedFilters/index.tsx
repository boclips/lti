import React from 'react';
import { FilterBadge } from '../filterBadge';

interface Props {
  values:string[];
  onClick:(ageRange:string[])=>void;
  type:string;
}

export const AppliedFilters = ({ values, onClick, type }: Props) => (
  <span>
    {values?.map((filter) => (
      <FilterBadge
        key={filter}
        value={filter}
        label={`${type}:`}
        onClick={(clickedValue: string) => onClick(values?.filter((item) => item !== clickedValue))}
      />
    )
    )}
  </span>
);
