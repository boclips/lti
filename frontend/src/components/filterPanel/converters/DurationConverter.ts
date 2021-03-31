import { SelectOption } from '@boclips-ui/select-option';
import { Facet } from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import Range from '../../../types/range';
import dayjs from '../../../types/dayjs';

export default class DurationConverter {
  static toSelectOptions(durationFacets: Facet[]): SelectOption[] {
    return durationFacets.map((facet) => ({
      id: facet.id,
      label: this.getLabelFromIso(facet.id),
      count: this.extractFacetHits(facet.hits),
    }))
      .filter((option) => option.label?.length > 0);
  }

  static getLabelFromIso(iso: string) {
    const values = iso.split('-');
    return values.length === 2
      ? this.getLabel({
        min: dayjs.duration(values[0]).asSeconds(),
        max: dayjs.duration(values[1]).asSeconds(),
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

  private static extractFacetHits = (hits: number): number => {
    const MaxElementCount = 500;
    if (!hits) {
      return 0;
    }
    return hits > MaxElementCount ? MaxElementCount : hits;
  };
}
