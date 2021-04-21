import React, { useEffect, useRef, useState } from 'react';
import { Video } from '@boclips-ui/video';
import Button from '@boclips-ui/button';
import { useMediaBreakPoint } from '@boclips-ui/use-media-breakpoints';
import ContentSelectionService from '../../service/contentSelection/ContentSelectionService';
import DeepLinkingParameterService from '../../service/deepLinking/DeepLinkingParameterService';
import s from './style.module.less';
import AnalyticsFactory from '../../service/analytics/AnalyticsFactory';
import {
  DESKTOP_BREAKPOINT,
  MOBILE_BREAKPOINT,
  TABLET_BREAKPOINT,
} from '../header';
import Embed from '../../resources/images/embed.svg';

const contentSelectionService = new ContentSelectionService();

interface Props {
  video: Video;
  onSubmit: (form: HTMLFormElement | null) => void;
}

export const ResponsiveEmbedVideoButton = ({ video, onSubmit }: Props) => {
  const [jwt, setJwt] = useState<string | undefined>(undefined);
  const formRef = useRef<HTMLFormElement>(null);

  const currentBreakpoint = useMediaBreakPoint();
  const desktopView = currentBreakpoint.type === DESKTOP_BREAKPOINT;
  const mobileView = currentBreakpoint.type === MOBILE_BREAKPOINT;
  const tabletView = currentBreakpoint.type === TABLET_BREAKPOINT;

  useEffect(() => {
    if (formRef.current) {
      onSubmit(formRef.current);
    }
  }, [jwt, onSubmit]);

  const handleEmbed = () => {
    AnalyticsFactory.getInstance().trackVideoInteraction(
      video,
      'LTI_SEARCH_AND_EMBED',
    );

    contentSelectionService
      .getContentSelectionJwt(
        [video.id],
        DeepLinkingParameterService.getDeploymentId(),
        DeepLinkingParameterService.getData(),
      )
      .then((jwtResponse) => setJwt(jwtResponse));
  };

  const getButtonWidth = (): string => {
    switch (true) {
      case mobileView:
        return '72px';
        break;
      case tabletView:
        return '98px';
        break;
      case desktopView:
        return '154px';
        break;
      default:
        return '72px';
    }
  };

  return (
    <div className={s.buttonWrapper}>
      <Button
        width={getButtonWidth()}
        height="42px"
        onClick={handleEmbed}
        icon={<Embed />}
        text="Add"
        iconOnly={mobileView}
      />
      {jwt && (
        <form
          ref={formRef}
          name="hidden-form"
          method="post"
          action={DeepLinkingParameterService.getReturnUrl()}
        >
          <input type="hidden" aria-label="jwt" value={jwt} name="JWT" />
        </form>
      )}
    </div>
  );
};
