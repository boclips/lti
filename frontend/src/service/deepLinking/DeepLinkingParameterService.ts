import { extractSingleUrlParameter } from '../extractUrlParameter';

const DeepLinkingParameterService = {
  getReturnUrl() {
    return extractSingleUrlParameter('deep_link_return_url');
  },
  getData() {
    return extractSingleUrlParameter('data');
  },
  getDeploymentId() {
    return extractSingleUrlParameter('deployment_id');
  },
};

export default DeepLinkingParameterService;
