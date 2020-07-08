import { Video as ClientVideo } from 'boclips-api-client/dist/sub-clients/videos/model/Video';
import { convertFromApiClientLink } from '@bit/dev-boclips.boclips-ui.types.link';
import AgeRange from '@bit/dev-boclips.boclips-ui.types.age-range';
import { Video } from '@bit/dev-boclips.boclips-ui.types.video';
import { AgeRange as ClientAgeRange } from 'boclips-api-client/dist/sub-clients/common/model/AgeRange';
import { getEffectiveThumbnailUrl } from './convertVideoResource';

function convertApiClientVideo(clientVideo: ClientVideo): Video {
  const { bestFor, links } = clientVideo;
  const clientAgeRange: ClientAgeRange = clientVideo.ageRange;

  const convertedProperties: Partial<Video> = {
    thumbnailUrl: clientVideo.playback?.links?.thumbnail
      ? getEffectiveThumbnailUrl(
        convertFromApiClientLink(clientVideo.playback.links.thumbnail),
      )
      : undefined,
    ageRange:
      clientAgeRange && new AgeRange(clientAgeRange.min, clientAgeRange.max),
    bestFor: bestFor?.[0]?.label || undefined,
    links: {
      self: convertFromApiClientLink(links.self),
      rate: convertFromApiClientLink(links.rate),
      tag: convertFromApiClientLink(links.tag),
      logInteraction: convertFromApiClientLink(links.logInteraction),
    },
  };

  if (links.transcript) {
    if (convertedProperties?.links === undefined) {
      throw Error('convertedProperties is undefined');
    }
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
    ageRange: convertedProperties.ageRange!!,
    bestFor: convertedProperties.bestFor,
    attachments: clientVideo.attachments,
    contentWarnings: clientVideo.contentWarnings,
    language: clientVideo.language,
    legalRestrictions: clientVideo.legalRestrictions,
    links: convertedProperties.links,
  };
}

export default convertApiClientVideo;
