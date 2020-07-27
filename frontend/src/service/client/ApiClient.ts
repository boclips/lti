import { ApiBoclipsClient, BoclipsClient } from 'boclips-api-client';
import AxiosService from '../axios/AxiosService';

class ApiClient {
  constructor(private readonly prefix: string) {}

  public getClient(): Promise<BoclipsClient> {
    return ApiBoclipsClient.create(
      AxiosService.getApiAuthenticatedInstance(),
      this.prefix,
    );
  }
}

export default ApiClient;
