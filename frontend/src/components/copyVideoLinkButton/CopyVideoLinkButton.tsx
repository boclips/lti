import React from 'react';
import { Button } from 'antd';
import copy from 'copy-to-clipboard';
import { tryExtractSingleUrlParameter } from '../../service/extractUrlParameter';
import s from './styles.module.less';
import TickIcon from '../../resources/images/tick.svg';
import CopyIcon from '../../resources/images/copy.svg';

interface Props {
  videoId: string;
}

const CopyVideoLinkButton = ({ videoId }: Props) => {
  const showCopyLink =
    tryExtractSingleUrlParameter('show_copy_link') === 'true';
  const embeddableVideoUrl = tryExtractSingleUrlParameter(
    'embeddable_video_url',
  );

  const [isCopying, setIsCopying] = React.useState(false);
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
      type="primary"
      size="large"
      className={`${s.copyVideoLink} ${s.success}`}
    >
      <TickIcon className={s.icon} />
      LINK COPIED
    </Button>
  ) : (
    <Button
      role="button"
      type="primary"
      size="large"
      className={`${s.copyVideoLink} ${s.primary}`}
      onClick={handleCopy}
    >
      <CopyIcon className={s.icon} />
      COPY LINK
    </Button>
  );
};

export default CopyVideoLinkButton;
