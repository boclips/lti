const tsImportPluginFactory = require('ts-import-plugin');
const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const srcPath = path.resolve(__dirname, '../src');
const distPath = path.resolve(__dirname, '../dist');

module.exports = {
  entry: {
    search: path.resolve(srcPath, 'entryPoints/search/index.tsx'),
    searchAndEmbed: path.resolve(
      srcPath,
      'entryPoints/searchAndEmbed/index.tsx',
    ),
  },
  output: {
    path: distPath,
    filename: '[name]-[hash:20].js',
    publicPath: '/',
  },
  performance: {
    hints: process.env.NODE_ENV === 'production' ? 'warning' : false,
  },
  // Allows ts(x) and js files to be imported without extension
  resolve: {
    // symlinks: false,
    extensions: ['.ts', '.tsx', '.js'],
    alias: {
      react: path.resolve('./node_modules/react'),
      antd: path.resolve('./node_modules/antd'),
      src: path.resolve(__dirname, '../src'),
      resources: path.resolve(__dirname, '../resources'),
      'test-support': path.resolve(__dirname, '../test-support'),
      'react-dom': '@hot-loader/react-dom',
    },
  },
  module: {
    rules: [
      {
        test: /\.(ts|tsx)$/,
        exclude: /node_modules/,
        use: [
          {
            loader: 'babel-loader',
          },
          {
            loader: 'ts-loader',
            options: {
              transpileOnly: true,
              getCustomTransformers: () => ({
                before: [
                  tsImportPluginFactory({
                    libraryName: 'antd',
                    libraryDirectory: 'lib',
                  }),
                ],
              }),
            },
          },
        ],
      },
      {
        test: /^((?!\.module).)*less$/,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader',
          {
            loader: 'less-loader',
            options: {
              lessOptions: {
                javascriptEnabled: true,
              },
            },
          },
        ],
      },
      {
        test: /\.module.less$/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              modules: {
                localIdentContext: __dirname,
                localIdentName: '[local]--[hash:base64:5]',
                mode: 'local',
              },
            },
          },
          {
            loader: 'less-loader',
            options: {
              lessOptions: {
                javascriptEnabled: true,
              },
            },
          },
        ],
      },
      {
        test: /.svg$/i,
        exclude: /node_modules/,
        loader: 'svg-react-loader',
        options: {
          props: {
            role: 'img',
          },
        },
      },
      {
        test: /\.(gif|png|jpe?g)$/i,
        use: ['file-loader', 'image-webpack-loader'],
      },
      {
        test: /\.svg$/i,
        include: /node_modules/,
        use: ['file-loader', 'image-webpack-loader'],
      },
    ],
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: '[name].[fullhash:5].css',
      chunkFilename: '[name].[chunkhash:5].chunk.css',
    }),
  ]
};
