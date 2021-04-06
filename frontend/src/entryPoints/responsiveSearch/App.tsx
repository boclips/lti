import { hot } from 'react-hot-loader/root';
import React, { ReactElement } from 'react';
import AxiosWrapper from '../../service/axios/AxiosWrapper';
import ResponsiveSearchView from '../../views/responsiveSearchView';

const App = (): ReactElement => <ResponsiveSearchView />;

export default hot(AxiosWrapper(App));
