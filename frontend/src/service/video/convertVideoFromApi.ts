import { Video as ClientVideo } from 'boclips-api-client/dist/sub-clients/videos/model/Video';
import { convertFromApiClientLink } from '@bit/boclips.boclips-ui.types.link';
import { Video } from '@bit/boclips.boclips-ui.types.video';
import AgeRange from '@bit/boclips.boclips-ui.types.age-range';
import { getEffectiveThumbnailUrl } from './convertVideoResource';

function convertApiClientVideo(clientVideo: ClientVideo): Video {
  const { ageRange, bestFor, links } = clientVideo;

  const convertedProperties: Partial<Video> = {
    thumbnailUrl: clientVideo.playback?.links?.thumbnail
      ? getEffectiveThumbnailUrl(
        convertFromApiClientLink(clientVideo.playback.links.thumbnail),
      )
      : undefined,
    ageRange: ageRange && new AgeRange(ageRange.min, ageRange.max),
    bestFor: bestFor?.[0]?.label || null,
    links: {
      self: convertFromApiClientLink(links.self),
      rate: convertFromApiClientLink(links.rate),
      tag: convertFromApiClientLink(links.tag),
      logInteraction: convertFromApiClientLink(links.logInteraction),
    },
  };

  if (links.transcript) {
    convertedProperties.links.transcript = convertFromApiClientLink(
      links.transcript,
    );
  }

  return {
    id: clientVideo.id,
    title: clientVideo.title,
    description: clientVideo.description,
    duration: clientVideo.playback.duration,
    releasedOn: clientVideo.releasedOn,
    createdBy: clientVideo.createdBy,
    playback: clientVideo.playback,
    subjects: clientVideo.subjects,
    badges: clientVideo.badges,
    rating: clientVideo.rating,
    yourRating: clientVideo.yourRating,
    promoted: clientVideo.promoted,
    thumbnailUrl: convertedProperties.thumbnailUrl,
    ageRange: convertedProperties.ageRange,
    bestFor: convertedProperties.bestFor,
    attachments: clientVideo.attachments,
    contentWarnings: clientVideo.contentWarnings,
    links: convertedProperties.links,
  };
}

export default convertApiClientVideo;
