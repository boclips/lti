import React, { ReactNode } from 'react';
import c from 'classnames';
import Icon from '@ant-design/icons';
import Chevron from '../../resources/images/grey-arrow.svg';
import s from './style.module.less';
import Ellipsis from '../../resources/images/ellipsis.svg';

interface Props {
  largeButton?: boolean;
  children: ReactNode;
  hideButton?: boolean;
}

const CustomPaginationButton = ({
  children,
  largeButton,
  hideButton,
}: Props) => {
  return (
    <div
      className={c(s.paginationButton, {
        [s.large]: largeButton,
        [s.hideButton]: hideButton,
      })}
    >
      {children}
    </div>
  );
};

export const PaginationButtons = (
  currentPage,
  type,
  _originalElement,
  mobileView,
  activePage,
) => {
  const visiblePagesOnMobile = (selectedPage, calculatePage) => {
    const onFirstPage = selectedPage === 1;
    const inRange = (page, min, max) => (page - min) * (page - max) <= 0;

    return onFirstPage
      ? inRange(calculatePage, selectedPage, selectedPage + 4)
      : inRange(calculatePage, selectedPage - 1, selectedPage + 2);
  };

  if (type === 'prev') {
    return (
      <CustomPaginationButton largeButton={!mobileView}>
        <Icon component={() => <Chevron />} className={s.prev} />
        {!mobileView && <span className={s.copy}>Prev</span>}
      </CustomPaginationButton>
    );
  }
  if (type === 'jump-prev' || type === 'jump-next') {
    return (
      <CustomPaginationButton hideButton={mobileView}>
        <Icon component={() => <Ellipsis />} className={s.ellipsis} />
      </CustomPaginationButton>
    );
  }
  if (type === 'page') {
    return (
      <CustomPaginationButton
        hideButton={
          mobileView && !visiblePagesOnMobile(activePage, currentPage)
        }
      >
        <span className={s.copy}>{currentPage}</span>
      </CustomPaginationButton>
    );
  }
  if (type === 'next') {
    return (
      <CustomPaginationButton largeButton={!mobileView}>
        {!mobileView && <span className={s.copy}>Next</span>}
        <Chevron />
      </CustomPaginationButton>
    );
  }
  return null;
};
