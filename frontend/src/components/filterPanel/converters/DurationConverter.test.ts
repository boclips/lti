import { SelectOption } from '@boclips-ui/select-option';
import { FacetsFactory } from 'boclips-api-client/dist/test-support/FacetsFactory';
import DurationConverter from './DurationConverter';

describe('Duration converter', () => {
  const durationFacets = FacetsFactory.sample({
    durations: [
      { name: 'PT0S-PT2M', id: 'PT0S-PT2M', hits: 53 },
      { name: 'PT2M-PT5M', id: 'PT2M-PT5M', hits: 94 },
      { name: 'PT5M-PT10M', id: 'PT5M-PT10M', hits: 44 },
      { name: 'PT10M-PT20M', id: 'PT10M-PT20M', hits: 48 },
      { name: 'PT20M-PT2H', id: 'PT20M-PT24H', hits: 45 },
    ]
  }).durations;

  it('can convert "duration" facet into SelectOption', () => {
    const selectOptions: SelectOption[] = DurationConverter.toSelectOptions(
      durationFacets,
    );

    expect(selectOptions).toHaveLength(5);
    expect(selectOptions[0]).toEqual({
      id: 'PT0S-PT2M',
      label: '0m - 2m',
      count: 53,
    });
    expect(selectOptions[1]).toEqual({
      id: 'PT2M-PT5M',
      label: '2m - 5m',
      count: 94,
    });
    expect(selectOptions[2]).toEqual({
      id: 'PT5M-PT10M',
      label: '5m - 10m',
      count: 44,
    });
    expect(selectOptions[3]).toEqual({
      id: 'PT10M-PT20M',
      label: '10m - 20m',
      count: 48,
    });
    expect(selectOptions[4]).toEqual({
      id: 'PT20M-PT24H',
      label: '> 20m',
      count: 45,
    });
  });
});
