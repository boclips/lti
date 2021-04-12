import React, { createContext, useContext, useState } from 'react';
import { Filters } from '../types/filters';

interface Props {
  children: React.ReactNode;
  defaultValue?: any;
}

const filtersContext = createContext<{
  filters: Filters;
  setFilters: React.Dispatch<React.SetStateAction<Filters>>;
}>({ filters: {}, setFilters: (_) => {} });

export const FiltersProvider = ({ children, defaultValue }: Props) => {
  const value = useProvideFilters();

  return (
    <filtersContext.Provider value={defaultValue || value}>
      {children}
    </filtersContext.Provider>
  );
};

export const useFilters = () => useContext(filtersContext);

const useProvideFilters = () => {
  const [filters, setFilters] = useState<Filters>({});

  return { filters, setFilters };
};
