import React from 'react';
import FilterBadge from '@bit/boclips.boclips-ui.components.filter-badge';

export interface BadgeOption {
  displayValue: string;
  key: string;
}

interface BadgeProps {
  badgeType: string;
  badges: (BadgeOption | undefined)[];
  updateFilters: (values: string[]) => void;
}

class FilterBadgeFactory {
  static produce({ badgeType, badges, updateFilters }: BadgeProps) {
    const onClick = (clickedValue: string) => {
      const filters = badges.map((b) => b!.key);
      updateFilters(filters?.filter((item) => item !== clickedValue));
    };

    return badges?.map((badge) => (
      badge && (
        <FilterBadge
          key={badge.key}
          id={badge.key}
          value={badge.displayValue}
          label={`${badgeType}:`}
          onClick={onClick}
        />
      )
    ));
  }
}

export default FilterBadgeFactory;
