import React from "react";
import {Layout} from "antd";
import HeaderWithLogo from '@bit/boclips.boclips-ui.components.header-with-logo';
import Title from "antd/lib/typography/Title";
import '../../index.less'

export const App = () => (
  <Layout>
    <HeaderWithLogo>
      <div></div>
    </HeaderWithLogo>
    <Layout.Content>
      <Title>HELLO - Search And Embed</Title>
    </Layout.Content>
  </Layout>
)
