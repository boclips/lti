import React, { ReactElement } from 'react';
import Badge from '@bit/boclips.dev-boclips-ui.components.badge';
import PaperclipSVG from './resources/activity-tag.svg';

interface AttachmentBadgeProps {
  theme?: 'backoffice' | 'lti' | 'custom';
}

const AttachmentBadge = ({ theme }: AttachmentBadgeProps): ReactElement => (
  <div data-qa="attachment-badge">
    <Badge label="Activity" icon={<PaperclipSVG />} theme={theme} />
  </div>
);

export default AttachmentBadge;
