import '@testing-library/jest-dom/extend-expect';
import { configure } from '@testing-library/dom';
import Enzyme from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import 'jest-enzyme';
import { ApiBoclipsClient, TestSupport } from 'boclips-api-client';

Enzyme.configure({ adapter: new Adapter() });

// create window object
declare const window: any;

window.open = jest.fn();

window.Environment = {
  LTI_BASE_URL: 'http://example.com',
  API_BASE_URL: 'http://api.com/v1',
};

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

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(), // deprecated
    removeListener: jest.fn(), // deprecated
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});

configure({ testIdAttribute: 'data-qa' });
