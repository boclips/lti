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
    {
      plugin: CracoLessPlugin,
      options: {
        modifyLessRule: function (lessRule) {
          lessRule.test = /\.(module)\.(less)$/;
          lessRule.exclude = /node_modules/;

          return lessRule;
        },
        cssLoaderOptions: {
          modules: true,
        },
      },
    },
  ],
};
