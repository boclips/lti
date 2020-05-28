jest.mock('axios');

import axios from 'axios';
import { AuthService } from './AuthService';

describe('AuthService', () => {
  it('adds a request interceptor to axios', () => {
    AuthService.configureAxios(jest.fn());

    expect(axios.interceptors.request.use).toHaveBeenCalled();
  });
});
