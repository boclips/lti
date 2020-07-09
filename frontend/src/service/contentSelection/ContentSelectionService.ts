import axios, { AxiosResponse } from 'axios';
import { AppConstants } from '../../types/AppConstants';

export class ContentSelectionService {
  async getContentSelectionJwt(
    videoIds: string[],
  ): Promise<string | undefined> {
    return axios
      .post(`${AppConstants.LTI_BASE_URL}/v1p3/deep-linking-response`, {
        selectedItems: videoIds,
        data: 'i-am-data',
        deploymentId: 'deployment-id',
      })
      .then((response: AxiosResponse) => response.data?.jwt);
  }
}
