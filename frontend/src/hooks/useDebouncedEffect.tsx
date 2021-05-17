import React from 'react';

// Modified from https://stackoverflow.com/a/61127960/7346215
export const useDebouncedEffect = (
  effect: (isFirstRender: boolean) => void,
  delay: number,
  deps?: React.DependencyList | undefined,
): void => {
  const [isFirstRender, setFirstRender] = React.useState(true);

  React.useEffect(() => {
    const handler = setTimeout(() => effect(isFirstRender), delay);
    setFirstRender(false);

    return () => clearTimeout(handler);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...(deps || []), delay]);
};
