import { Row } from 'antd';
import React from 'react';
import { ReactComponent as BoclipsLogo } from '../../resources/images/boclips-logo.svg';
import './TopNavbar.less';

export const TopNavbar = (): React.ReactElement => {
  return (
    <Row>
      <BoclipsLogo className="top-navbar top-navbar__logo" />
    </Row>
  );
};
