import { Input } from 'antd';
import React from 'react';
import { SearchOutlined } from '@ant-design/icons';
import './SearchBar.less';

const { Search } = Input;

export const SearchBar = (): React.ReactElement => {
  return (
    <Search
      className="search-bar"
      placeholder="Search for videos"
      enterButton="Search"
      size="large"
      addonBefore={<SearchOutlined />}
      onSearch={(value) => console.log(value)}
    />
  );
};
