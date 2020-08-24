import AgeRange from './index';

const convertAgeRangesFromString = (
  ageRangeStrings?: string | string[],
): AgeRange[] => {
  if (!ageRangeStrings) {
    return [];
  }

  const ageRangeArray = !Array.isArray(ageRangeStrings)
    ? [ageRangeStrings]
    : ageRangeStrings;

  return ageRangeArray.map((ageRange) => {
    const minAndMax = ageRange.split('-');
    return new AgeRange(
      parseInt(minAndMax[0], 10),
      minAndMax[1] ? parseInt(minAndMax[1], 10) : undefined,
    );
  });
};

describe('AgeRange', () => {
  describe('generating an id', () => {
    test('returns a hyphenated id when ageRange has min and max', () => {
      const ageRange = new AgeRange(5, 11);

      expect(ageRange.getLabel()).toEqual('5 - 11');
      expect(ageRange.getShortLabel()).toEqual('5-11');
    });

    test('returns an n+ id when ageRange has min only', () => {
      const ageRange = new AgeRange(7);

      expect(ageRange.getLabel()).toEqual('7+');
      expect(ageRange.getShortLabel()).toEqual('7+');
    });
  });

  describe('generating a label', () => {
    test('returns a hyphenated label when ageRange has min and max', () => {
      const ageRange = new AgeRange(5, 11);

      expect(ageRange.getLabel()).toEqual('5 - 11');
      expect(ageRange.getShortLabel()).toEqual('5-11');
    });

    test('returns an n+ id when ageRange has a max of 19', () => {
      const ageRange = new AgeRange(7, 19);

      expect(ageRange.getLabel()).toEqual('7+');
      expect(ageRange.getShortLabel()).toEqual('7+');
    });

    test('returns an n+ id when ageRange max of 99', () => {
      const ageRange = new AgeRange(16, 99);

      expect(ageRange.getLabel()).toEqual('16 - 99');
      expect(ageRange.getShortLabel()).toEqual('16+');
    });
  });

  describe('generating an array', () => {
    test('returns a populated array from an age range with min and max', () => {
      const ageRange = new AgeRange(7, 11);

      expect(ageRange.getNumbers()).toEqual([7, 8, 9, 10, 11]);
    });

    test('returns an empty array from an age range with no max', () => {
      const ageRange = new AgeRange(16);

      expect(ageRange.getNumbers()).toEqual([16, 17, 18, 19]);
    });
  });

  describe('resolving the minimum age', () => {
    test('returns the default min age if given out of bounds min age', () => {
      const ageRange = new AgeRange(-1, 11);

      expect(ageRange.resolveMin()).toEqual(3);
    });
    test('returns the default min age if given undefined min age', () => {
      const ageRange = new AgeRange(undefined, 11);

      expect(ageRange.resolveMin()).toEqual(3);
    });
    test('returns the min age if set', () => {
      const ageRange = new AgeRange(5, 11);

      expect(ageRange.resolveMin()).toEqual(5);
    });
  });

  describe('resolving the maximum age', () => {
    test('returns the default max age if no max age set', () => {
      const ageRange = new AgeRange(3);
      expect(ageRange.resolveMax()).toEqual(19);
    });
    test('returns the max age if set', () => {
      const ageRange = new AgeRange(3, 16);
      expect(ageRange.resolveMax()).toEqual(16);
    });
  });

  describe('encoding and decoding ageranges as JSON', () => {
    it('encodes an agerange into json', () => {
      const ageRange = new AgeRange(10, 15);

      expect(ageRange.encodeJSON()).toEqual('{"min":10,"max":15}');
    });

    it('encodes an agerange with unbounded upper into json', () => {
      const ageRange = new AgeRange(19);

      expect(ageRange.encodeJSON()).toEqual('{"min":19}');
    });

    it('decodes an agerange with unbounded upper from json', () => {
      expect(AgeRange.fromJson('{"min":19}')).toEqual(new AgeRange(19));
    });

    it('decodes json into an agerange', () => {
      const jsonAgeRange = '{"min":10, "max":15}';

      expect(AgeRange.fromJson(jsonAgeRange)).toEqual(new AgeRange(10, 15));
    });
  });

  describe('removing duplicates', () => {
    it('sorts and removes duplicates correctly', () => {
      const ageRangeList: AgeRange[] = [
        new AgeRange(4, 7),
        new AgeRange(3, 4),
        new AgeRange(3, 4),
        new AgeRange(7, 10),
      ];
      const deduplicatedList: AgeRange[] = AgeRange.removeDuplicates(
        ageRangeList,
      );

      expect(deduplicatedList.length).toEqual(3);
      expect(deduplicatedList[0]).toEqual(new AgeRange(4, 7));
      expect(deduplicatedList[1]).toEqual(new AgeRange(3, 4));
      expect(deduplicatedList[2]).toEqual(new AgeRange(7, 10));
    });
  });

  describe('creating an age range from strings', () => {
    it('creates an age range from a string', () => {
      expect(convertAgeRangesFromString('3-5')).toEqual([new AgeRange(3, 5)]);
    });

    it('creates an age range from an string', () => {
      expect(convertAgeRangesFromString(['3-5', '5-7'])).toEqual([
        new AgeRange(3, 5),
        new AgeRange(5, 7),
      ]);
    });

    it('does not fail when undefined string is passed', () => {
      expect(convertAgeRangesFromString(undefined)).toEqual([]);
    });
  });
});

export default AgeRange;
