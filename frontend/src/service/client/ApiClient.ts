import axios from 'axios';
import { ApiBoclipsClient, BoclipsClient } from 'boclips-api-client';

export class ApiClient {
  constructor(private readonly prefix: string) {}

  public getClient(): Promise<BoclipsClient> {
    return ApiBoclipsClient.create(axios, this.prefix);
  }
}
