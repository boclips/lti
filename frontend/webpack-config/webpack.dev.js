require('dotenv').config({ path: '.env.dev' });
const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const HtmlWebpackPlugin = require('html-webpack-plugin');

const { merge } = require('webpack-merge');

const common = require('./webpack.common.js');

const googleAnalyticsId = 'does-not-matter';

const srcPath = path.resolve(__dirname, '../src');
const distPath = path.resolve(__dirname, '../dist');


module.exports = merge(common, {
  mode: 'development',
  devtool: 'eval-source-map',
  devServer: {
    contentBase: distPath,
    historyApiFallback: true,
    port: 9000,
    hot: true,
  },
  plugins: [
    new MiniCssExtractPlugin({ filename: '[name].css', ignoreOrder: true }),
    new HtmlWebpackPlugin({
      chunks: ['search'],
      filename: 'search.html',
      template: path.resolve(srcPath, 'index-dev.html'),
      ga: googleAnalyticsId,
    }),
    new HtmlWebpackPlugin({
      chunks: ['searchAndEmbed'],
      filename: 'search-and-embed.html',
      template: path.resolve(srcPath, 'index-dev.html'),
      ga: googleAnalyticsId,
    })
  ],
});
