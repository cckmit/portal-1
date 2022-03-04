/* eslint-disable */
// @ts-nocheck

module.exports = (env, options) => {
  const path = require("path")
  const dir = __dirname
  const dirRoot = path.resolve(dir, "..", "..", "..")

  return {
    mode: "development",
    target: "web", // Fix HMR - https://stackoverflow.com/a/64988081
    devtool: "eval-source-map",
    optimization: {
      minimize: false,
    }
  }
}
