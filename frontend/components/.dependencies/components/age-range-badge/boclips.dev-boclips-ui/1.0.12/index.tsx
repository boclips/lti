import React from 'react';
import Badge from '@bit/boclips.dev-boclips-ui.components.badge';
import AgeRange from '@bit/boclips.dev-boclips-ui.types.age-range';

interface AgeRangeBadgeProps {
  ageRange: AgeRange;
  theme: 'lti' | 'backoffice' | 'custom';
}

const AgeRangeBadge = ({ ageRange, theme = 'lti' }: AgeRangeBadgeProps) => {
  const getAgeRange = ageRange?.getShortLabel();

  return (
    <>
      {getAgeRange && (
        <div data-qa="age-range-badge">
          <Badge value={getAgeRange} label="Ages:" theme={theme} />
        </div>
      )}
    </>
  );
};

export default AgeRangeBadge;
