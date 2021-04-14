import { act } from '@testing-library/react';

function setViewPortWidth(width: number) {
  Object.defineProperty(window, 'innerWidth', {
    writable: true,
    configurable: true,
    value: width,
  });

  // Trigger the window resize event.
  act(() => {
    global.dispatchEvent(new Event('resize'));
  });
}

export default setViewPortWidth;
