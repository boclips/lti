import { hot } from 'react-hot-loader/root';
import React from 'react';
import ResponsiveSearchView from '../../views/responsiveSearchView';
import { BoclipsClientProvider } from '../../hooks/useBoclipsClient';
import { FiltersProvider } from '../../hooks/useFilters';

const App = ({ apiClient }) => {
  return (
    <BoclipsClientProvider client={apiClient}>
      <FiltersProvider>
        <ResponsiveSearchView />
      </FiltersProvider>
    </BoclipsClientProvider>
  );
};

export default hot(App);
