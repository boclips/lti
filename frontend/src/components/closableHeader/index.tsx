import React, { useEffect, useRef, useState } from 'react';
import s from './style.module.less';
import DeepLinkingParameterService from '../../service/deepLinking/DeepLinkingParameterService';
import ContentSelectionService from '../../service/contentSelection/ContentSelectionService';
import CloseIcon from '../../resources/images/close-icon.svg';
import AboutDrawer from '../sls/AboutDrawer';

const contentSelectionService = new ContentSelectionService();

interface TitleHeaderProps {
  title: string;
  handleSubmit: (form: HTMLFormElement | null) => void;
  showSlsTerms?: boolean;
}

const ClosableHeader = ({ title, handleSubmit, showSlsTerms }: TitleHeaderProps) => {
  const [jwt, setJwt] = useState<string | undefined>(undefined);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (formRef.current) {
      handleSubmit(formRef.current);
    }
  }, [jwt]);

  const onClose = () => contentSelectionService
    .getContentSelectionJwt(
      [],
      DeepLinkingParameterService.getDeploymentId(),
      DeepLinkingParameterService.getData(),
    )
    .then((jwtResponse) => setJwt(jwtResponse));
  
  const closeIcon = (<span
    data-qa="close-icon"
    className={s.closeIcon}
    role="presentation"
    onClick={onClose}
  >
    <CloseIcon />
  </span>);

  return (
    <div data-qa="closable-header">
      <div className={s.header}>
        <div>
          <span className={s.text}>{title}</span>
          {showSlsTerms && <AboutDrawer closeIcon={closeIcon}/>}
        </div>
        {closeIcon}
      </div>
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
