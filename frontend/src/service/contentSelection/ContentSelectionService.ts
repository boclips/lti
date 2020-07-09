import axios, { AxiosResponse } from 'axios';
import { AppConstants } from '../../types/AppConstants';

export default class ContentSelectionService {
  getContentSelectionJwt = async (
    videoIds: string[],
    deploymentId: string,
    data: string,
  ): Promise<string | undefined> => axios
    .post(`${AppConstants.LTI_BASE_URL}/v1p3/deep-linking-response`, {
      selectedItems: videoIds.map((it) => ({ id: it })),
      data,
      deploymentId,
    })
    .then((response: AxiosResponse) => response.data?.jwt);
}
