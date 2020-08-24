import React, {
  MutableRefObject,
  ReactElement,
  useEffect,
  useRef,
} from 'react';
import { ExtendedVideo } from '@bit/boclips.dev-boclips-ui.types.video';
import { Card, Skeleton as AntSkeleton } from 'antd';
import AgeRangeBadge from '@bit/boclips.dev-boclips-ui.components.age-range-badge';
import SubjectBadge from '@bit/boclips.dev-boclips-ui.components.subject-badge';
import BestForBadge from '@bit/boclips.dev-boclips-ui.components.best-for-badge';
import AttachmentBadge from '@bit/boclips.dev-boclips-ui.components.attachment-badge';
import ReleasedOn from '@bit/boclips.dev-boclips-ui.components.released-on';
import { useMediaBreakPoint } from '@bit/boclips.dev-boclips-ui.hooks.use-media-break-points';
import { MediaBreakpoints } from '@bit/boclips.dev-boclips-ui.types.media-break-points';
import ProviderBadge from '@bit/boclips.dev-boclips-ui.components.provider-badge';
import VideoPromotedSvg from './resources/promoted-icon.svg';

import s from './style.module.less';

interface EditValues {
  title?: string;
  description?: string;
}

interface Props {
  video?: ExtendedVideo | null;
  analytics?: () => void;
  authenticated?: boolean;
  loading?: boolean;
  hideBadges?: boolean;
  handleOnClick?: () => void;
  theme?: 'backoffice' | 'lti' | 'custom';
  editMode?: boolean;
  setEditValues?: MutableRefObject<EditValues | null>;
  hideAgeRange?: boolean;
  hideSubjects?: boolean;
  hideBestFor?: boolean;
  hideAttachments?: boolean;
}

interface Components {
  rating?: ReactElement;
  videoPlayer?: ReactElement;
  videoActionButtons?: ReactElement[];
}

export const VideoCardSkeleton = () => (
  <Card className={s.videoCard} bordered={false}>
    <AntSkeleton
      loading
      active
      title={{ width: '150px' }}
      paragraph={{ rows: 5 }}
      avatar={{ shape: 'square', size: 'large' }}
    />
  </Card>
);

export const VideoCard = ({
  video,
  authenticated,
  analytics,
  rating,
  videoPlayer,
  videoActionButtons,
  loading,
  handleOnClick,
  hideBadges,
  theme = 'lti',
  editMode,
  setEditValues,
  hideAgeRange,
  hideSubjects,
  hideBestFor,
  hideAttachments,
}: Props & Components): any => {
  const breakpoint = useMediaBreakPoint();
  const smallCard = breakpoint.width < MediaBreakpoints.md.width;
  const renderVideoButtons =
    ((video && video?.links?.transcript) || authenticated) && !smallCard;

  const titleInput = useRef<HTMLInputElement>(null);
  const descriptionInput = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (setEditValues) {
      setEditValues.current = {
        title: video?.title,
        description: video?.description,
      };
    }
  }, [editMode, setEditValues, video]);

  const onInputChange = () => {
    if (setEditValues) {
      setEditValues.current = {
        title: titleInput.current?.value,
        description: descriptionInput.current?.value,
      };
    }
  };

  const ClickableCard = () => (
    <Card
      bodyStyle={{ width: '100%' }}
      className={s.videoCard}
      bordered={false}
      data-qa="video-card"
      onMouseDown={analytics}
      onClick={handleOnClick}
    >
      <section className={s.cardHeader}>
        {editMode ? (
          <input
            data-qa="input-title"
            className={s.input}
            ref={titleInput}
            type="text"
            defaultValue={video?.title}
            onChange={onInputChange}
          />
        ) : (
          <h1 data-qa="video-title" className={s.headerTitle}>
            {video?.title}
          </h1>
        )}

        {theme === 'lti' && <ProviderBadge isLicensed={video?.playback?.type === 'STREAM'}/>}
      </section>

      <section className={s.subHeader}>
        {rating && (
          <section
            role="presentation"
            onClick={(e) => e.stopPropagation()}
            className={s.rating}
          >
            {rating}
          </section>
        )}
        {video?.releasedOn && video?.createdBy && (
          <ReleasedOn
            releasedOn={video?.releasedOn}
            createdBy={video?.createdBy}
            theme={theme}
          />
        )}
      </section>

      <section className={s.cardBody}>
        {videoPlayer && (
          <section
            role="presentation"
            onClick={(e) => e.stopPropagation()}
            className={s.videoPlayer}
          >
            {videoPlayer}
          </section>
        )}

        <div className={s.bodyRight}>
          {!hideBadges && (
            <div className={s.badgeList} data-qa="video-badge-list">
              {!hideAgeRange && video?.ageRange && (
                <AgeRangeBadge ageRange={video.ageRange} theme={theme} />
              )}
              {!hideSubjects &&
                video?.subjects &&
                video?.subjects.map((it) => (
                  <SubjectBadge key={it.id} subject={it} theme={theme} />
                ))}

              {!hideBestFor &&
                video?.bestFor &&
                video?.bestFor.map((it) => (
                  <BestForBadge bestFor={it.label} theme={theme} />
                ))}

              {!hideAttachments &&
                video?.attachments &&
                video?.attachments?.length > 0 && (
                <AttachmentBadge theme={theme} />
              )}

              {theme === 'backoffice' && video?.promoted && (
                <div className={s.videoPromotedSvg}>
                  <VideoPromotedSvg />
                </div>
              )}
            </div>
          )}

          {editMode ? (
            <textarea
              data-qa="textarea-description"
              className={s.input}
              ref={descriptionInput}
              defaultValue={video?.description}
              onChange={onInputChange}
            />
          ) : (
            <section className={s.description}>{video?.description}</section>
          )}

          {videoActionButtons && renderVideoButtons && (
            <section
              role="presentation"
              onClick={(e) => e.stopPropagation()}
              className={s.videoActionButtons}
            >
              {videoActionButtons}
            </section>
          )}
          <span />
        </div>
      </section>
    </Card>
  );

  return loading ? <VideoCardSkeleton /> : <ClickableCard />;
};
