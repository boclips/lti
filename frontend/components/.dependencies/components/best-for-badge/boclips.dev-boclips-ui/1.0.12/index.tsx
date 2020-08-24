import Badge from '@bit/boclips.dev-boclips-ui.components.badge';
import React from 'react';

interface BestForBadgeProps {
  bestFor: string;
  theme?: 'backoffice' | 'lti' | 'custom';
}
const BestForBadge = ({ bestFor, theme }: BestForBadgeProps) => (
  <div data-qa="best-for-badge">
    <Badge value={bestFor} label="Best for:" theme={theme} />
  </div>
);

export default BestForBadge;
