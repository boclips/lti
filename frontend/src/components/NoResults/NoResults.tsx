import React from 'react';
import './NoResults.less';

interface NoResultsProps{
  searchQuery:String;
}

const NoResults = ({ searchQuery }:NoResultsProps) => (
  <div
    className="no-results">
    <p className="no-results-text">{`We couldn't anything for "${searchQuery}" with your filters`}</p>
    <p className="no-results-description"> try again using different keywords or change the filters</p>
  </div>
);

export default NoResults;
