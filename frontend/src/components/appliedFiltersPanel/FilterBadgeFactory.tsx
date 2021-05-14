import React from 'react';
import FilterBadge from '@boclips-ui/filter-badge';
import CloseBadgeIcon from '../../resources/images/filter-badge-close-icon.svg';
import s from './style.module.less';

export interface BadgeOption {
  displayValue: string;
  key: string;
}

interface BadgeProps {
  badgeType: string;
  badges: BadgeOption[] | [];
  updateFilters: (any) => void;
}

class FilterBadgeFactory {
  static produce({ badges, updateFilters }: BadgeProps) {
    const onClick = (clickedValue: string) => {
      const filters = badges?.map((b) => b!.key);
      updateFilters(filters?.filter((item) => item !== clickedValue));
    };

    return badges?.map(
      (badge) =>
        badge && (
          <FilterBadge
            key={badge.key}
            id={badge.key}
            value={badge.displayValue}
            closeIcon={
              <span className={s.filtersBadgeCloseWrapper}>
                <CloseBadgeIcon />
              </span>
            }
            onClick={onClick}
          />
        ),
    );
  }
}

export default FilterBadgeFactory;
