import MockAdapter from 'axios-mock-adapter';
import axios from 'axios';

const MockApi = {
  deepLinkingResponse: (
    data: string,
    deploymentId: string,
    selectedIds: string[],
    jwt: string,
  ) => {
    const axiosMock = new MockAdapter(axios);
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
