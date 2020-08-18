import React from 'react';
import {FilterBadge} from "../filterBadge";

interface Props {
  value:string[];
  onClick:(ageRange:string[])=>void;
  type:string;
}

export const AppliedFilters = ({value, onClick, type}: Props) => {

  return (
    <span>{(value!)?.map((filter)=>{
    return (<FilterBadge
        value={filter}
        label={`${type}:`}
        onClick={(filter: string) => onClick(value?.filter(item => item !== filter))}
      />
    )}
    )}</span>
  )
};

