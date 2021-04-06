import React, { useEffect } from 'react';
import { useBoclipsClient } from '../../hooks/useBoclipsClient';

const ResponsiveSearchView = () => {
  const client = useBoclipsClient();

  useEffect(() => {
    client.subjects.getAll();
  }, [client.subjects]);

  return <div> 123 </div>;
};

export default ResponsiveSearchView;
