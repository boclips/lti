import React, { useEffect, useRef, useState } from 'react';
import { Video } from '@boclips-ui/video';
import Button from '../button/Button';
import ContentSelectionService from '../../service/contentSelection/ContentSelectionService';
import DeepLinkingParameterService from '../../service/deepLinking/DeepLinkingParameterService';
import s from './style.module.less';
import AnalyticsFactory from '../../service/analytics/AnalyticsFactory';

const contentSelectionService = new ContentSelectionService();

interface Props {
  video: Video;
  onSubmit: (form: HTMLFormElement | null) => void;
}

const EmbedVideoButton = ({ video, onSubmit }: Props) => {
  const [jwt, setJwt] = useState<string | undefined>(undefined);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (formRef.current) {
      onSubmit(formRef.current);
    }
  }, [jwt]);
  
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

  return (
    <>
      <Button
        role="button"
        type="primary"
        className={s.embedVideoButton}
        onClick={handleEmbed}
      >
        + Add to lesson
      </Button>
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
    </>
  );
};

export default EmbedVideoButton;
