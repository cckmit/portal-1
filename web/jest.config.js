/* eslint-disable */
// @ts-nocheck

module.exports = (env, options) => {

  const path = require("path")
  const dir = __dirname
  const dirRoot = path.resolve(dir, "..")
  const tsconfig = require(path.resolve(dirRoot, "tsconfig.json"))
  const modulePaths = tsconfig.compilerOptions.paths
  const moduleAbsolutePaths = Object.fromEntries(Object.entries(modulePaths).map(([module, paths]) => {
    return [module, paths.map(p => path.resolve(dirRoot, p))]
  }))
  const modules = Object.values(modulePaths).flat()

  const resolveModules = (...relative) => {
    return modules.map(module => path.resolve(dirRoot, module, "..", ...relative))
  }

  return {
    roots: resolveModules("src"),
    testMatch: resolveModules("src/**/*.test.{js,jsx,ts,tsx}"),
    moduleNameMapper: {
      "^.+\\.(css|less|sass|scss|svg)$": path.resolve(dir, "jest.stub.object.js"),
      ...moduleAbsolutePaths
    },
    testEnvironment: "jsdom",
    transform: {
      "\\.(js|jsx|ts|tsx)$": ["babel-jest", {
        rootMode: "upward",
      }],
    },
    setupFilesAfterEnv: [
      "jest-expect-message",
      path.resolve(dir, "jest.setup.js"),
    ],
    collectCoverage: false,
    collectCoverageFrom: [
      ...resolveModules("src/**/*.{js,jsx,ts,tsx}"),
      "!**/node_modules/**",
      "!**/vendor/**",
    ],
    coverageDirectory: path.resolve(dir, "build", "coverage"),
    coverageProvider: "babel",
    coverageReporters: [ "json", "lcov", "text", "clover" ],
  }
}
