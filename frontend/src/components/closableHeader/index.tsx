import React, { useEffect, useRef, useState } from 'react';
import s from './style.module.less';
import DeepLinkingParameterService from '../../service/deepLinking/DeepLinkingParameterService';
import ContentSelectionService from '../../service/contentSelection/ContentSelectionService';
import CloseIcon from '../../resources/images/close-icon.svg';

const contentSelectionService = new ContentSelectionService();

interface TitleHeaderProps {
  title: string;
  handleSubmit: (form: HTMLFormElement | null) => void;
}

const ClosableHeader = ({ title, handleSubmit }: TitleHeaderProps) => {
  const [jwt, setJwt] = useState<string | undefined>(undefined);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (formRef.current) {
      handleSubmit(formRef.current);
    }
  }, [jwt]);

  return (
    <div data-qa="closable-header" className={s.header}>
      <span className={s.text}>{title}</span>
      <span
        data-qa="close-icon"
        className={s.closeIcon}
        role="presentation"
        onClick={() =>
          contentSelectionService
            .getContentSelectionJwt(
              [],
              DeepLinkingParameterService.getDeploymentId(),
              DeepLinkingParameterService.getData(),
            )
            .then((jwtResponse) => setJwt(jwtResponse))
        }
      >
        <CloseIcon />
      </span>
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
export default ClosableHeader;
