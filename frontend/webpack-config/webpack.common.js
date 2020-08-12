const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const tsImportPluginFactory = require('ts-import-plugin');
const path = require('path');

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
    extensions: ['.ts', '.tsx', '.js'],
    alias: {
      '@ant-design/icons/lib/': path.resolve(
        __dirname,
        '../resources/icons.ts',
      ),
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
          {
            loader: 'css-loader',
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
        test: /\.module.less$/,
        use: [
          {
            loader: 'style-loader',
          },
          {
            loader: 'css-loader',
            options: {
              sourceMap: true,
              modules: {
                localIdentName: '[name]__[local]--[hash:base64:5]',
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
        oneOf: [
          {
            loader: ['file-loader', 'image-webpack-loader'],
            resourceQuery: /inline/,
          },
          {
            loader: 'svg-react-loader',
            options: {
              props: {
                role: 'img',
              },
            },
          },
        ],
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
  plugins: [new MiniCssExtractPlugin({ filename: '[name]-[hash:20].css' })],
};
