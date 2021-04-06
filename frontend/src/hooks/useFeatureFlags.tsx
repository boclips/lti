import { useEffect, useState } from 'react';
import { UserFeatureKey } from 'boclips-api-client/dist/sub-clients/organisations/model/User';
import { useBoclipsClient } from './useBoclipsClient';

function useFeatureFlags() {
  const [featureFlags, setFeatureFlags] = useState<
    { [key in UserFeatureKey]?: boolean }
  >();
  const apiClient = useBoclipsClient();

  useEffect(() => {
    const fetchUser = async () => {
      const user = await apiClient.users.getCurrentUser();
      const features = user.features;

      setFeatureFlags(features);
    };

    fetchUser();
  }, [apiClient.users]);

  return featureFlags;
}

export default useFeatureFlags;
