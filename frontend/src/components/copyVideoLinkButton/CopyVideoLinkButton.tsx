import React from 'react';
import copy from 'copy-to-clipboard';
import { Video } from '@bit/boclips.boclips-ui.types.video';
import { tryExtractSingleUrlParameter } from '../../service/extractUrlParameter';
import s from './styles.module.less';
import TickIcon from '../../resources/images/tick.svg';
import CopyIcon from '../../resources/images/copy.svg';
import Button from '../button/Button';
import AnalyticsFactory from '../../service/analytics/AnalyticsFactory';

interface Props {
  video: Video;
}

const CopyVideoLinkButton = ({ video }: Props) => {
  const [isCopying, setIsCopying] = React.useState(false);

  const showCopyLink =
    tryExtractSingleUrlParameter('show_copy_link') === 'true';

  const embeddableVideoUrl = tryExtractSingleUrlParameter(
    'embeddable_video_url',
  );

  const handleCopy = () => {
    AnalyticsFactory.getInstance().trackVideoInteraction(
      video,
      'LTI_SEARCH_VIDEO_LINK_COPIED',
    );
    copy(embeddableVideoUrl.replace('{id}', video.id));
    setIsCopying(true);
    setTimeout(() => setIsCopying(false), 1500);
  };

  if (!showCopyLink || !embeddableVideoUrl) {
    return null;
  }

  return isCopying ? (
    <Button className={`${s.copyVideoLink} ${s.success}`}>
      <span className={s.icon}>
        <TickIcon />
      </span>
      LINK COPIED
    </Button>
  ) : (
    <Button className={`${s.copyVideoLink}`} onClick={handleCopy}>
      <span className={s.icon}>
        <CopyIcon />
      </span>
      COPY LINK
    </Button>
  );
};

export default CopyVideoLinkButton;
