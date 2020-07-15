import ContentSelectionService from './ContentSelectionService';
import MockApi from '../../testSupport/mockApi';
import AxiosService from '../axios/AxiosService';

AxiosService.configureAxios();

describe('ContentSelectionService', () => {
  describe('getContentSelectionJwt', () => {
    it('returns an object with a JWT string in it', async () => {
      const videoIds = ['123', '456'];
      MockApi.deepLinkingResponse(AxiosService.getVanillaInstance(), 'data', 'deployment-id', videoIds, 'a jwt string');

      const service = new ContentSelectionService();

      const jwt = await service.getContentSelectionJwt(videoIds, 'deployment-id', 'data');

      expect(jwt).toEqual('a jwt string');
    });
  });
});
