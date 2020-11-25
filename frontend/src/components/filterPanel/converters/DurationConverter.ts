import { SelectOption } from '@boclips-ui/select-option';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import Range from '../../../types/range';
import dayjs from '../../../types/dayjs';

interface DurationFacet {
  [id: string]: Facet;
}

export default class DurationConverter {
  static toSelectOptions(durationFacet: DurationFacet): SelectOption[] {
    return Object.keys(durationFacet).map((key) => ({
      id: key,
      label: this.getLabelFromIso(key),
      count: this.extractFacetHits(key, durationFacet),
    }))
      .filter((option) => option.label?.length > 0);
  }
  
  static getLabelFromIso(iso: string) {
    const values = iso.split('-');
    return values.length === 2 
      ? this.getLabel({
        min: dayjs.duration(values[0]).asSeconds(),
        max: dayjs.duration(values[1]).asSeconds()
      })
      : '';
  }
  
  private static getLabel = (range: Range) => {
    const formatter = (seconds) => `${seconds / 60}m`;

    if (range.max !== 86400) {
      return `${formatter(range.min)} - ${formatter(range.max)}`;
    }

    return `> ${formatter(range.min)}`;
  };

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
}
