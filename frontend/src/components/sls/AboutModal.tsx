import { Modal } from 'antd';
import React, { useState } from 'react';
import AboutAppAndServicesDetails from './AboutAppAndServicesDetails';
import AboutButton from './AboutButton';
import CloseIcon from '../../resources/images/close-icon.svg';

import s from './style.module.less';

const AboutModal = () => {
  const [modalVisible, setModalVisible] = useState(false);

  return <>
    <AboutButton onClick={() => setModalVisible(true)}/>
    <Modal
      title={
        <span className={s.modalTitle}>About the app and services</span>
      }
      bodyStyle={{ background: '#F8FAFF', padding: '35px 55px' }}
      visible={modalVisible}
      onCancel={() => setModalVisible(false)}
      footer={null}
      closeIcon={<span className={s.closeIcon}><CloseIcon/></span>}
      className={s.modal}
      width="80%"
    >
      <AboutAppAndServicesDetails/>
    </Modal>
  </>;
};

export default AboutModal;
