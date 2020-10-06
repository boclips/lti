import { Link } from '@boclips-ui/link';
import AxiosService from '../axios/AxiosService';

class BoclipsAnalytics {
  public trackVideoInteraction = (
    video: { id: String; links: { logInteraction?: Link } },
    interactionType: String,
  ): Promise<void> => {
    const link = video.links.logInteraction;

    if (!link) {
      return Promise.reject(
        new Error(`Video ${video.id} has no logInteraction link`),
      );
    }

    return AxiosService.getApiAuthenticatedInstance().post(
      link.getTemplatedLink({
        type: interactionType,
      }),
    );
  };
}

export default new BoclipsAnalytics();
