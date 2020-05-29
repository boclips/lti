/* eslint-disable no-undef */
/* eslint-disable @typescript-eslint/no-var-requires */

const CracoLessPlugin = require('craco-less');

module.exports = {
  plugins: [
    {
      plugin: CracoLessPlugin,
      options: {
        lessLoaderOptions: {
          javascriptEnabled: true,
        },
      },
    },
  ],
};
