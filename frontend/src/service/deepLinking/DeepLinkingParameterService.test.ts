import DeepLinkingParameterService from './DeepLinkingParameterService';

describe('DeepLinkingParameterService', () => {
  const setupInitialLocation = (location: string) => {
    const previousLocation = global.window.location;
    delete global.window.location;
    global.window.location = { ...previousLocation, href: location };
  };

  it('can retrieve deep link return url parameter from url', () => {
    setupInitialLocation(
      'https://blah.com?deep_link_return_url=https://return_url.com/',
    );
    const returnUrl = DeepLinkingParameterService.getReturnUrl();
    expect(returnUrl).toEqual('https://return_url.com/');
  });

  it('can retrieve data parameter from url', () => {
    setupInitialLocation('https://blah.com?data=wow');
    const data = DeepLinkingParameterService.getData();
    expect(data).toEqual('wow');
  });

  it('can retrieve deploymentId parameter from url', () => {
    setupInitialLocation('https://blah.com?deployment_id=wow-id');
    const data = DeepLinkingParameterService.getDeploymentId();
    expect(data).toEqual('wow-id');
  });
});
