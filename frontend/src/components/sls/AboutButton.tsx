import { Button } from 'antd';
import React from 'react';
import InfoIcon from '../../resources/images/info.svg';
import s from './style.module.less';

interface Props {
  onClick: () => void;
}

const AboutButton = ({ onClick }: Props) =>
  <Button onClick={onClick} icon={<InfoIcon/>} className={s.aboutButton}>
    About the app and services
  </Button>;

export default AboutButton;
