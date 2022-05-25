/* eslint-disable */
// @ts-nocheck

module.exports = {
  "presets": [
    [ "@babel/preset-env", {
      "debug": false,
      "bugfixes": true,
      "useBuiltIns": "usage",
      "corejs": {
        "version": "3.21",
        "proposals": true
      }
    } ],
    [ "@babel/preset-typescript" ],
    [ "@babel/preset-react", {
      "runtime": "automatic"
    } ],
  ],
  "plugins": [
    [ "babel-plugin-transform-typescript-metadata" ],
    [ "@babel/plugin-proposal-decorators", { "legacy": true } ],
    [ "@babel/plugin-proposal-class-properties" ]
  ]
}
