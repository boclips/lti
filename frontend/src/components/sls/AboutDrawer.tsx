import React, { useState } from 'react';
import { Button, Drawer } from 'antd';
import BackIcon from '../../resources/images/back.svg';
import AboutAppAndServicesDetails from './AboutAppAndServicesDetails';
import AboutButton from './AboutButton';
import s from './style.module.less';

interface Props {
  closeIcon: React.ReactNode;
}
const AboutDrawer = ({ closeIcon }: Props) => {
  const [aboutVisible, setAboutVisible] = useState(false);
  
  const backButton = (
    <Button type="link" size="large" className={s.backButton} icon={<BackIcon/>} onClick={() => setAboutVisible(false)}>
      Back to Video Library
    </Button>);
  
  return (
    <>
      <Drawer
        bodyStyle={{ background: '#F8FAFF', padding: '35px 55px' }}
        headerStyle={{ boxShadow: '0 4px 13px 0 rgba(204,219,237,0.5)' }}
        title={backButton}
        visible={aboutVisible}
        width="100%"
        closeIcon={closeIcon}
      >
        <AboutAppAndServicesDetails/>
      </Drawer>
      <AboutButton onClick={() => setAboutVisible(true)}/>
    </>
  );
};

export default AboutDrawer;
