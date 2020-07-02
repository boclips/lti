import moment from 'moment';
import AgeRange from '@bit/boclips.boclips-ui.types.age-range';
import { PlaybackConverter } from 'boclips-api-client/dist/sub-clients/common/model/PlaybackConverter';
import { Link } from '@bit/boclips.boclips-ui.types.link';
import { Video } from '@bit/boclips.boclips-ui.types.video';

const DEFAULT_THUMBNAIL_WIDTH = 500;

export function getEffectiveThumbnailUrl(thumbnailLink?: Link) {
  if (thumbnailLink === undefined) {
    throw new Error('Received a null thumbnail link');
  }

  return thumbnailLink.isTemplated
    ? thumbnailLink.getTemplatedLink({
      thumbnailWidth: DEFAULT_THUMBNAIL_WIDTH,
    })
    : thumbnailLink.getOriginalLink();
}

export default function convertVideoResource(resource: any): Video {
  const video: Video = {
    id: resource.id,
    title: resource.title,
    description: resource.description,
    duration: moment.duration(resource.playback.duration),
    releasedOn: new Date(resource.releasedOn),
    createdBy: resource.createdBy,
    thumbnailUrl: getEffectiveThumbnailUrl(
      new Link(resource.playback._links.thumbnail),
    ),
    playback: PlaybackConverter.convert(resource.playback),
    subjects: resource.subjects,
    badges: resource.badges,
    ageRange:
      resource.ageRange &&
      new AgeRange(resource.ageRange.min, resource.ageRange.max),
    rating: resource.rating,
    yourRating: resource.yourRating,
    bestFor:
      (resource.bestFor && resource.bestFor[0] && resource.bestFor[0].label) ||
      null,
    promoted: resource.promoted,
    attachments: resource.attachments,
    legalRestrictions: resource.legalRestrictions,
    links: {
      self: new Link(resource._links.self),
      rate: resource._links.rate ? new Link(resource._links.rate) : undefined,
      tag: resource._links.tag ? new Link(resource._links.tag) : undefined,
      logInteraction: resource._links.logInteraction
        ? new Link(resource._links.logInteraction)
        : undefined,
    },
  };

  if (resource._links.transcript) {
    video.links!!.transcript = new Link(resource._links.transcript);
  }

  return video;
}
