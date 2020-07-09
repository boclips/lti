import {ContentSelectionService} from "./ContentSelectionService";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";

describe('ContentSelectionService', () => {
  describe('getContentSelectionJwt', () => {
    it('returns an object with a JWT string in it', async () => {
      const axiosMock = new MockAdapter(axios);
      axiosMock
        .onPost('http://lti-service.com/v1p3/deep-linking-response')
        .reply(200, JSON.stringify({ jwt: 'a jwt string'}));

      const service = new ContentSelectionService()
      const videoIds = ['123', '456']

      const jwt = await service.getContentSelectionJwt(videoIds)

      expect(jwt).toEqual('a jwt string')
    })
  })
})
