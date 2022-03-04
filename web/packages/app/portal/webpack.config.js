/* eslint-disable */
// @ts-nocheck

module.exports = (env, options) => {

  const PROD = process.env.NODE_ENV === "production"

  const path = require("path")
  const dir = __dirname
  const dirRoot = path.resolve(dir, "..", "..", "..")

  console.log("")
  console.log("---------------------------")
  console.log("Webpack build")
  console.log(`This is ${PROD ? "PRODUCTION" : "DEVELOPMENT"} build`)
  console.log("----------------------------")
  console.log("")
  process.on("unhandledRejection", err => {
    throw err
  })

  const { merge } = require("webpack-merge")
  const commonConfig = require("./webpack.part.common")
  const prodConfig = require("./webpack.part.prod")
  const devConfig = require("./webpack.part.dev")

  return merge(
    commonConfig(env, options),
    ...[
      PROD && prodConfig(env, options),
      !PROD && devConfig(env, options),
    ].filter(Boolean)
  )
}
