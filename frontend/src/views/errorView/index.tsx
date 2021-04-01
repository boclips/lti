import React from 'react';
import CatError from '../../resources/images/cat-error.svg';
import s from './styles.module.less';

const ErrorView = () => (
  <>
    <div className={s.errorViewContainer}>
      <CatError />
      <div className={s.title}>Oops... Something went wrong!</div>
      <div>Please try closing and re-opening the video library</div>
    </div>
  </>
);

export default ErrorView;
