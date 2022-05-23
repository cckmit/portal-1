/* eslint-disable */
// @ts-nocheck

const path = require("path")
const dir = __dirname
const dirRoot = path.resolve(dir, "..")

module.exports = {
  "env": {
    "browser": true,
    "serviceworker": true,
  },
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "tsconfigRootDir": dirRoot,
    "project": "./tsconfig.json",
    "sourceType": "module",
    "ecmaVersion": 12,
    "ecmaFeatures": {
      "jsx": true,
      "modules": true,
      "experimentalObjectRestSpread": true,
    },
  },
  "plugins": [
    "import",
    "react",
    "react-hooks",
    "@typescript-eslint",
  ],
  "settings": {
    "import/parsers": {
      "@typescript-eslint/parser": [ ".tsx", ".ts", ".js" ],
    },
    "import/resolver": {
      "typescript": {},
    },
    "react": {
      "version": "detect",
    },
  },
  "extends": [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:@typescript-eslint/recommended-requiring-type-checking"
  ],
  "rules": {
    "no-debugger": "error",
    "no-tabs": "error",
    "no-constant-condition": [ "warn", {
      "checkLoops": false,
    } ],
    "eol-last": [ "error", "always" ],
    "import/no-unresolved": [ "error", {
      "caseSensitive": false,
    } ],
    "import/no-extraneous-dependencies": [ "error", {
      "devDependencies": false,
    } ],
    "import/no-cycle": [ "warn", {
      "maxDepth": 1,
    } ],
    "import/no-self-import": "error",
    "import/no-relative-packages": "error",
    "import/no-useless-path-segments": "error",
    "import/no-nodejs-modules": "error",
    "import/no-commonjs": "error",
    "import/no-amd": "error",
    "import/no-default-export": "error",
    "import/newline-after-import": "warn",
    "react/react-in-jsx-scope": "off",
    "react/prop-types": "off",
    "comma-dangle": "off",
    "@typescript-eslint/comma-dangle": [ "error", "always-multiline" ],
    "quotes": "off",
    "@typescript-eslint/quotes": [ "warn", "double" ],
    "@typescript-eslint/no-empty-function": "off",
    "@typescript-eslint/no-empty-interface": "off",
    "@typescript-eslint/ban-ts-comment": "off",
    "@typescript-eslint/ban-types": [ "error", {
      "types": {
        "object": false,
      },
    } ],
    "@typescript-eslint/no-inferrable-types": "off",
    "@typescript-eslint/no-unused-vars": "off",
    "@typescript-eslint/explicit-module-boundary-types": "error",
    "@typescript-eslint/array-type": [ "warn", {
      "default": "generic",
    } ],
    "@typescript-eslint/member-delimiter-style": [ "warn", {
      "multiline": {
        "delimiter": "none",
        "requireLast": true
      },
      "singleline": {
        "delimiter": "comma",
        "requireLast": false
      },
    } ],
    "@typescript-eslint/restrict-template-expressions": "off",
    "@typescript-eslint/require-await": "off",
  },
  "globals": {
    "window": true,
    "self": true,
    "console": true,
    "navigator": true,
  },
}
