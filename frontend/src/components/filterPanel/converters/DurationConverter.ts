import { SelectOption } from '@bit/boclips.boclips-ui.types.select-option/index';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import moment from 'moment';
import Range from '../../../types/range';

interface DurationFacet {
  [id: string]: Facet;
}

export default class DurationConverter {
  static convertToSelectOptions(
    durationFacet: DurationFacet,
    defaultOptions: Range[]
  ): SelectOption[] {
    return defaultOptions.map((duration) => ({
      id: this.toIso(duration),
      label: this.getLabel(duration),
      count: this.extractFacetHits(this.toIso(duration), durationFacet),
    }));
  }

  private static getLabel = (range: Range) => {
    const formatter = (seconds) => `${seconds / 60}m`;

    if (range.max !== 86400) {
      return `${formatter(range.min)} - ${formatter(range.max)}`;
    }

    return `${formatter(range.min)} +`;
  };

  private static toIso(range: Range) {
    return `${this.secondsToIso(
      range.min,
    )}-${this.secondsToIso(range.max)}`;
  }

  private static extractFacetHits = (
    id: string,
    facet: { [id: string]: Facet },
  ): number => {
    const MaxElementCount = 500;
    if (!facet) {
      return 0;
    }
    if (!facet[id]) {
      return 0;
    }
    return facet[id].hits > MaxElementCount ? MaxElementCount : facet[id].hits;
  };

  private static secondsToIso = (seconds: number): string => {
    if (seconds === 0) {
      return 'PT0S';
    }

    return moment.duration(seconds, 'seconds').toISOString();
  };
}
