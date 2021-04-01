import c from 'classnames';
import { Button } from 'antd';
import React from 'react';
import s from './styles.module.less';
import FiltersIcon from '../../resources/images/filters-icon.svg';

interface Props {
  filtersVisible: boolean;
  toggleFilters: (visible: boolean) => void;
  activeFilterCount?: number;
}

const FiltersButton = ({
  filtersVisible,
  toggleFilters,
  activeFilterCount,
}: Props) => (
  <Button
    type="primary"
    className={c({
      [s.toggleFiltersButton]: true,
      [s.showFilters]: !filtersVisible,
      [s.hideFilters]: filtersVisible,
    })}
    onClick={() => toggleFilters(!filtersVisible)}
  >
    <div className={s.labelWrapper}>
      <FiltersIcon />
      {filtersVisible ? 'HIDE FILTERS' : 'SHOW FILTERS'}
      {!filtersVisible && activeFilterCount! > 0 && (
        <span data-qa="count-wrapper" className={s.countWrapper}>
          {activeFilterCount}
        </span>
      )}
    </div>
  </Button>
);

export default FiltersButton;
