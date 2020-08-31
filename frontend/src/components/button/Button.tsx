import React from 'react';
import { Button as AntdButton } from 'antd';
import { ButtonProps } from 'antd/lib/button';
import s from './styles.module.less';

const Button = ({
  children, type, size, className, onClick
}: ButtonProps) => (
  <AntdButton
    type={type || 'primary'}
    size={size || 'large'}
    onClick={onClick}
    className={`${s.button} ${className}`}
  >
    {children}
  </AntdButton>
);

export default Button;
