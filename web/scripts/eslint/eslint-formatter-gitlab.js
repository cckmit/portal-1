/* eslint-disable */
// @ts-nocheck

// @see https://gitlab.com/remcohaszing/eslint-formatter-gitlab

const { createHash } = require("crypto")
const { existsSync, lstatSync, mkdirSync, readFileSync, writeFileSync } = require("fs")
const { dirname, join, relative, resolve } = require("path")

const yaml = require("js-yaml")
const { ESLint } = require("eslint")

const {
  CI_CONFIG_PATH = ".gitlab-ci.yml",
  CI_JOB_NAME,
  CI_PROJECT_DIR = process.cwd(),
  ESLINT_CODE_QUALITY_REPORT,
  ESLINT_FORMATTER,
} = process.env

function getOutputPath() {
  const configPath = join(CI_PROJECT_DIR, CI_CONFIG_PATH)
  if (!existsSync(configPath) || !lstatSync(configPath).isFile()) {
    throw new Error(
      "Could not resolve .gitlab-ci.yml to automatically detect report artifact path." +
      " Please manually provide a path via the ESLINT_CODE_QUALITY_REPORT variable.",
    )
  }
  const jobs = yaml.load(readFileSync(configPath, "utf-8"))
  const { artifacts } = jobs[CI_JOB_NAME]
  const location = artifacts && artifacts.reports && artifacts.reports.codequality
  const msg = `Expected ${CI_JOB_NAME}.artifacts.reports.codequality to be one exact path`
  if (!location) {
    throw new Error(`${msg}, but no value was found.`)
  }
  if (Array.isArray(location)) {
    throw new TypeError(`${msg}, but found an array instead.`)
  }
  return resolve(CI_PROJECT_DIR, location)
}

function createFingerprint(filePath, message) {
  const md5 = createHash("md5")
  md5.update(filePath)
  if (message.ruleId) {
    md5.update(message.ruleId)
  }
  md5.update(message.message)
  return md5.digest("hex")
}

function convert(results) {
  const messages = []
  for (const result of results) {
    for (const message of result.messages) {
      const relativePath = relative(CI_PROJECT_DIR, result.filePath)
      // https://github.com/codeclimate/spec/blob/master/SPEC.md#data-types
      messages.push({
        description: message.message,
        severity: message.severity === 2 ? "major" : "minor",
        fingerprint: createFingerprint(relativePath, message),
        location: {
          path: relativePath,
          lines: {
            begin: message.line,
          },
        },
      })
    }
  }
  return messages
}

module.exports = async (results, context) => {
  if (CI_JOB_NAME || ESLINT_CODE_QUALITY_REPORT) {
    const data = convert(results)
    const outputPath = ESLINT_CODE_QUALITY_REPORT || getOutputPath()
    const dir = dirname(outputPath)
    mkdirSync(dir, { recursive: true })
    writeFileSync(outputPath, JSON.stringify(data, null, 2))
  }
  const eslint = new ESLint({ cwd: context.cwd })
  const formatter = await eslint.loadFormatter(ESLINT_FORMATTER || undefined)
  return formatter.format(results, { rulesMeta: context.rulesMeta })
}
