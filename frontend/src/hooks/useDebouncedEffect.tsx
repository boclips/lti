import React from 'react';

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
