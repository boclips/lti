const path = require('path');

const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');
const CompressionPlugin = require('compression-webpack-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');

const srcPath = path.resolve(__dirname, '../src');

const googleAnalyticsId = 'UA-126218810-2';

module.exports = merge(common, {
  mode: 'production',
  optimization: {
    splitChunks: {
      cacheGroups: {
        commons: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          chunks: 'all',
        },
      },
    },
    runtimeChunk: {
      name: 'manifest',
    },
    minimize: true,
    minimizer: [
      new webpack.IgnorePlugin({
        resourceRegExp: /^\.\/locale$/,
        contextRegExp: /moment$/,
      }),
      new CssMinimizerPlugin(),
    ],
  },
  plugins: [
    new HtmlWebpackPlugin({
      chunks: ['search'],
      filename: 'search.html',
      template: path.resolve(srcPath, 'index.html'),
      ga: googleAnalyticsId,
    }),

    new HtmlWebpackPlugin({
      chunks: ['searchAndEmbed'],
      filename: 'search-and-embed.html',
      template: path.resolve(srcPath, 'index.html'),
      ga: googleAnalyticsId,
    }),

    new CleanWebpackPlugin(),

    new webpack.IgnorePlugin({
      resourceRegExp: /^\.\/locale$/,
      contextRegExp: /moment$/,
    }),
    new CompressionPlugin()
  ]
});
