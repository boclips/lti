// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom/extend-expect';
import { ApiBoclipsClient, TestSupport } from 'boclips-api-client';

// create window object
declare const window: any;

window.Environment = {};

const testApiClient = new TestSupport.FakeBoclipsClient();

jest.mock('boclips-api-client', () => {
  const r = jest.requireActual('boclips-api-client');

  return {
    ...r,
    ApiBoclipsClient: {
      ...r.ApiBoclipsClient,
      create: () => Promise.resolve(testApiClient),
    } as ApiBoclipsClient,
  };
});
