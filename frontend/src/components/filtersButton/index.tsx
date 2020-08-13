import c from 'classnames';
import { Button } from 'antd';
import React from 'react';
import s from './styles.module.less';
import FiltersIcon from '../../resources/images/filters-icon.svg';

interface Props {
  filtersVisible: boolean;
  toggleFilters: (visible: boolean) => void;
}

const FiltersButton = ({ filtersVisible, toggleFilters }: Props) => 
  <Button
    type="primary"
    className={c({
      [s.toggleFiltersButton]: true,
      [s.showFilters]: !filtersVisible,
      [s.hideFilters]: filtersVisible
    })}
    onClick={() => toggleFilters(!filtersVisible)}
  >
    <div className={s.labelWrapper}><FiltersIcon/>
      {filtersVisible ? 'HIDE FILTERS' : 'SHOW FILTERS'}
    </div>
  </Button>;

export default FiltersButton;
