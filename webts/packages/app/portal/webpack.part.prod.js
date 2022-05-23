/* eslint-disable */
// @ts-nocheck

module.exports = (env, options) => {
  const TerserPlugin = require("terser-webpack-plugin")

  return {
    mode: "production",
    devtool: "nosources-source-map",
    optimization: {
      minimize: true,
      minimizer: [
        new TerserPlugin({
          extractComments: false,
          terserOptions: {
            keep_classnames: true,
            keep_fnames: true,
            mangle: false,
            compress: {
              collapse_vars: false,
              comparisons: false,
              conditionals: false,
              evaluate: false,
              hoist_funs: false,
              hoist_props: false,
              hoist_vars: false,
              if_return: false,
              join_vars: false,
              keep_classnames: true,
              keep_fargs: true,
              keep_fnames: true,
              reduce_vars: false,
              sequences: false,
            },
            output: {
              beautify: true,
              braces: true,
              keep_numbers: true,
              indent_level: 2,
              indent_start: 0,
              semicolons: false,
            },
          },
        }),
      ],
    },
  }
}
