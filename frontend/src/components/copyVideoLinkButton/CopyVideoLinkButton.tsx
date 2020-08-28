import React from 'react';
import { Button } from 'antd';
import copy from 'copy-to-clipboard';
import { tryExtractSingleUrlParameter } from '../../service/extractUrlParameter';
import s from './styles.module.less';

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

  // TODO ICONS
  return isCopying ? (
    <Button className={`${s.copyVideoLink} ${s.success}`}>LINK COPIED</Button>
  ) : (
    <Button
      role="button"
      className={`${s.copyVideoLink} ${s.primary}`}
      type="primary"
      onClick={handleCopy}
    >
      COPY LINK
    </Button>
  );
};

export default CopyVideoLinkButton;
