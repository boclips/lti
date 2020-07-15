import MockAdapter from 'axios-mock-adapter';
import { AxiosInstance } from 'axios';

const MockApi = {
  deepLinkingResponse: (
    axiosInstance: AxiosInstance,
    data: string,
    deploymentId: string,
    selectedIds: string[],
    jwt: string,
  ) => {
    const axiosMock = new MockAdapter(axiosInstance);
    axiosMock
      .onPost('http://lti-service.com/v1p3/deep-linking-response', {
        data,
        deploymentId,
        selectedItems: selectedIds.map((it) => ({ id: it })),
      })
      .reply(200, JSON.stringify({ jwt }));
  },
};

export default MockApi;
