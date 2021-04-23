import React, { createContext, useContext, useState } from 'react';
import { Filters } from '../types/filters';

interface Props {
  children: React.ReactNode;
  defaultValue?: any;
}

const filtersContext = createContext<{
  filters: Filters;
  setFilters: React.Dispatch<React.SetStateAction<Filters>>;
  areFiltersApplied: boolean;
  clearFilters: () => void;
}>({
  filters: {},
  setFilters: (_) => {},
  areFiltersApplied: false,
  clearFilters: () => {},
});

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

  const filtersApplied = Object.keys(filters).filter((key) =>
    filters[key].length > 0 ? filters[key] : false,
  );

  const areFiltersApplied = filtersApplied.some((x) => x);

  const clearFilters = () => {
    setFilters({
      ageRanges: [],
      source: [],
      duration: [],
      subjects: [],
    });
  };

  return { filters, setFilters, areFiltersApplied, clearFilters };
};
