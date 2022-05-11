/* eslint-disable */
// @ts-nocheck

module.exports = (env, options) => {

  const PROD = process.env.NODE_ENV === "production"
  const localesToKeep = [ "ru", "en", "en-US" ]

  const path = require("path")
  const webpack = require("webpack")
  const { CleanWebpackPlugin } = require("clean-webpack-plugin")
  const CopyPlugin = require("copy-webpack-plugin")
  const ESLintPlugin = require("eslint-webpack-plugin")
  const TsconfigPathsPlugin = require("tsconfig-paths-webpack-plugin")
  const dir = __dirname
  const dirRoot = path.resolve(dir, "..", "..", "..")
  const dirDist = path.resolve(dirRoot, "src", "main", "resources", "ru", "protei", "portal", "ui", "web", "public")
  const versionPath = path.resolve(dirRoot, "version.json")
  const version = require(versionPath).version
  const tsconfig = require(path.resolve(dirRoot, "..", "tsconfig.json"))
  const modules = Object.values(tsconfig.compilerOptions.paths).flat()

  const resolveModules = (...relative) => {
    return modules.map(module => path.resolve(dirRoot, "..", module, "..", ...relative))
  }

  return {
    bail: PROD,
    entry: {
      "index": path.resolve(dir, "src", "index.ts"),
    },
    output: {
      path: dirDist,
      pathinfo: !PROD,
      publicPath: "Portal/",
      filename: "[name].bundle.js",
      chunkFilename: "[name].bundle.js",
    },
    plugins: [
      new webpack.ProgressPlugin(),
      new CleanWebpackPlugin({
        cleanStaleWebpackAssets: true,
        protectWebpackAssets: true,
      }),
      new CopyPlugin({
        patterns: [
          ...resolveModules("public").map(pattern => ({
            from: pattern,
            noErrorOnMissing: true,
          })),
        ],
      }),
      new webpack.ContextReplacementPlugin(
        /date-fns[\/\\]/,
        new RegExp(`[/\\\\\](${localesToKeep.join('|')})[/\\\\\]index\.js$`)
      ),
      new webpack.DefinePlugin({
        __app_version__: JSON.stringify(version),
      }),
      new ESLintPlugin({
        extensions: [ "tsx", "ts", "js" ],
        failOnError: true,
        failOnWarning: false,
      }),
    ].filter(Boolean),
    module: {
      rules: [
        {
          test: /\.(ts|js)x?$/,
          include: resolveModules("src"),
          exclude: /node_modules/,
          use: [
            {
              loader: "babel-loader",
              options: {
                rootMode: "upward",
                cacheDirectory: true,
                cacheCompression: false,
              },
            },
          ],
        },
        {
          test: /.*/,
          include: resolveModules("src", "asset"),
          exclude: /.*debugids\.json$/,
          type: "asset/resource",
          rules: [ {
            oneOf: [
              {
                include: resolveModules("src", "asset", "img"),
                generator: {
                  filename: "image/[name].[contenthash][ext][query]",
                },
              },
              {
                include: resolveModules("src", "asset", "font"),
                generator: {
                  filename: "font/[name].[contenthash][ext][query]",
                },
              },
              {
                include: resolveModules("src", "asset", "audio"),
                generator: {
                  filename: "audio/[name].[contenthash][ext][query]",
                },
              },
              {
                generator: {
                  filename: "asset/[name].[contenthash][ext][query]",
                },
              },
            ],
          } ],
        },
      ].filter(Boolean),
    },
    resolve: {
      extensions: [ ".tsx", ".ts", ".js" ],
      plugins: [ new TsconfigPathsPlugin({
        extensions: [ ".tsx", ".ts", ".js" ],
      }) ],
    },
    optimization: {
      realContentHash: true,
      runtimeChunk: "single",
      chunkIds: "named",
      moduleIds: "named",
      splitChunks: {
        chunks: "all",
        minSize: 0,
        cacheGroups: {
          proteilib: {
            test: /[\\/]packages[\\/]lib[\\/]/,
            name: "protei-lib",
            chunks: "all",
            enforce: true,
          },
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: "vendors",
            chunks: "all",
            enforce: true,
          },
        },
      },
    },
  }
}
