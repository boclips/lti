import React from 'react';
import { ReactComponent as BoclipsLogo } from '../../resources/images/boclips-logo.svg';
import './TopNavbar.less';
import { SearchBar } from './searchbar/SearchBar';
import { Row, Col } from 'antd';

export const TopNavbar = (): React.ReactElement => {
  return (
    <Row className="top-navbar">
      <Col span={6}>
        <BoclipsLogo className="top-navbar__logo" />
      </Col>
      <Col span={12}>
        <SearchBar />
      </Col>
    </Row>
  );
};
