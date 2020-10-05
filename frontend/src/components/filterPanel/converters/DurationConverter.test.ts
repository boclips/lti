import { SelectOption } from '@boclips-ui/select-option';
import DurationConverter from './DurationConverter';

describe('Duration converter', () => {
  const durationFacet = {
    'PT0S-PT2M': { hits: 53 },
    'PT2M-PT5M': { hits: 94 },
    'PT5M-PT10M': { hits: 44 },
    'PT10M-PT20M': { hits: 48 },
    'PT20M-PT24H': { hits: 45 }
  };

  it('can convert "duration" facet into SelectOption', () => {
    const selectOptions: SelectOption[] =
      DurationConverter.toSelectOptions(durationFacet);

    expect(selectOptions).toHaveLength(5);
    expect(selectOptions[0]).toEqual({ id: 'PT0S-PT2M', label: '0m - 2m', count: 53 });
    expect(selectOptions[1]).toEqual({ id: 'PT2M-PT5M', label: '2m - 5m', count: 94 });
    expect(selectOptions[2]).toEqual({ id: 'PT5M-PT10M', label: '5m - 10m', count: 44 });
    expect(selectOptions[3]).toEqual({ id: 'PT10M-PT20M', label: '10m - 20m', count: 48 });
    expect(selectOptions[4]).toEqual({ id: 'PT20M-PT24H', label: '> 20m', count: 45 });
  });
});
