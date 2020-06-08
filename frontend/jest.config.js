module.exports = {
  clearMocks: true,
  setupFilesAfterEnv: ['<rootDir>setupTests.ts'],
  globals: {
    'ts-jest': {
      tsConfig: 'tsconfig.jest.json',
    },
  },
  moduleFileExtensions: ['ts', 'tsx', 'js'],
  testEnvironment: 'jsdom',
  testMatch: ['<rootDir>src/**/*.(integrationTest|test).(ts|tsx)'],
  testPathIgnorePatterns: ['node_modules'],
  preset: 'ts-jest',
  moduleNameMapper: {
    '\\.(css|less)$': '<rootDir>/__mocks__/styleMock.js',
    '\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$':
      '<rootDir>/__mocks__/fileMock.js',
  },
};
