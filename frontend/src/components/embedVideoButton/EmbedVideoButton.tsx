import React, { useEffect, useRef, useState } from 'react';
import Button from '../button/Button';
import ContentSelectionService from '../../service/contentSelection/ContentSelectionService';
import DeepLinkingParameterService from '../../service/deepLinking/DeepLinkingParameterService';
import s from './style.module.less';

const contentSelectionService = new ContentSelectionService();

interface Props {
  videoId: string;
  onSubmit: (form: HTMLFormElement | null) => void;
}

const EmbedVideoButton = ({ videoId, onSubmit }: Props) => {
  const [jwt, setJwt] = useState<string | undefined>(undefined);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (formRef.current) {
      onSubmit(formRef.current);
    }
  }, [jwt]);

  return (
    <>
      <Button
        role="button"
        type="primary"
        className={s.embedVideoButton}
        onClick={() =>
          contentSelectionService
            .getContentSelectionJwt(
              [videoId],
              DeepLinkingParameterService.getDeploymentId(),
              DeepLinkingParameterService.getData(),
            )
            .then((jwtResponse) => setJwt(jwtResponse))
        }
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
