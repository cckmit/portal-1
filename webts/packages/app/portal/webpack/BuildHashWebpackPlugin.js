/* eslint-disable */
// @ts-nocheck

const path = require("path")

class BuildHashPlugin {

  constructor(options) {
    this.options = {
      filename: "build-hash.txt",
    }
    if (options && options.filename) {
      this.options.filename = options.filename
    }
  }

  apply(compiler) {
    const buildFile = path.resolve(compiler.options.output.path || ".", this.options.filename)
    compiler.hooks.afterEmit.tapAsync("BuildHashWebpackPlugin", (compilation, callback) => {
      const stats = compilation.getStats().toJson({
        hash: true,
        publicPath: false,
        assets: false,
        chunks: false,
        modules: false,
        source: false,
        errorDetails: false,
        timings: false,
      })
      compiler.outputFileSystem.writeFile(buildFile, stats.hash, callback)
    })
  }
}

module.exports = BuildHashPlugin
