import { hot } from 'react-hot-loader/root';
import React from 'react';
import ResponsiveSearchView from '../../views/responsiveSearchView';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';

const App = ({ apiClient }) => {
  return (
    <BoclipsClientProvider client={apiClient}>
      <ResponsiveSearchView />
    </BoclipsClientProvider>
  );
};

export default hot(App);
