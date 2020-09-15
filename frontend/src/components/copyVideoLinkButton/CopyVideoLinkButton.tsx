import React from 'react';
import copy from 'copy-to-clipboard';
import { tryExtractSingleUrlParameter } from '../../service/extractUrlParameter';
import s from './styles.module.less';
import TickIcon from '../../resources/images/tick.svg';
import CopyIcon from '../../resources/images/copy.svg';
import Button from '../button/Button';

interface Props {
  videoId: string;
}

const CopyVideoLinkButton = ({ videoId }: Props) => {
  const [isCopying, setIsCopying] = React.useState(false);

  const showCopyLink =
    tryExtractSingleUrlParameter('show_copy_link') === 'true';

  const embeddableVideoUrl = tryExtractSingleUrlParameter(
    'embeddable_video_url',
  );

  const handleCopy = () => {
    copy(embeddableVideoUrl.replace('{id}', videoId));
    setIsCopying(true);
    setTimeout(() => setIsCopying(false), 1500);
  };

  if (!showCopyLink || !embeddableVideoUrl) {
    return null;
  }

  return isCopying ? (
    <Button
      className={`${s.copyVideoLink} ${s.success}`}
    >
      <span className={s.icon}>
        <TickIcon/>
      </span>
      LINK COPIED
    </Button>
  ) : (
    <Button
      className={`${s.copyVideoLink}`}
      onClick={handleCopy}
    >
      <span className={s.icon}>
        <CopyIcon />
      </span>
      COPY LINK
    </Button>
  );
};

export default CopyVideoLinkButton;
