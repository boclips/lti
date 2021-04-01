const setupInitialLocation = (location: string) => {
  const previousLocation = global.window.location;
  // @ts-ignore
  delete global.window.location;
  global.window.location = { ...previousLocation, href: location };
};

export default setupInitialLocation;
