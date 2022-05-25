/* eslint-disable */
// @ts-nocheck

const { createHash } = require("crypto")
const { mkdirSync, writeFileSync } = require("fs")
const { dirname, relative } = require("path")
const { ESLint } = require("eslint")

const {
  CI_PROJECT_DIR = process.cwd(),
  ESLINT_FORMAT,
  ESLINT_HTML_REPORT,
  ESLINT_CODE_QUALITY_REPORT,
} = process.env

function createFingerprint(filePath, message) {
  const md5 = createHash("md5")
  md5.update(filePath)
  if (message.ruleId) {
    md5.update(message.ruleId)
  }
  md5.update(message.message)
  return md5.digest("hex")
}

function convertEslintResultsToCodeClimateIssues(results) {
  const issues = []
  // https://eslint.org/docs/developer-guide/nodejs-api#-lintresult-type
  for (const result of results) {
    // https://eslint.org/docs/developer-guide/nodejs-api#-lintmessage-type
    for (const message of result.messages) {
      const relativePath = relative(CI_PROJECT_DIR, result.filePath)
      // https://docs.gitlab.com/ee/user/project/merge_requests/code_quality.html#implementing-a-custom-tool
      // https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#issues
      issues.push({
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
  return issues
}

module.exports = async (results, context) => {
  if (ESLINT_CODE_QUALITY_REPORT) {
    const data = convertEslintResultsToCodeClimateIssues(results)
    const outputPath = ESLINT_CODE_QUALITY_REPORT
    const dir = dirname(outputPath)
    mkdirSync(dir, { recursive: true })
    writeFileSync(outputPath, JSON.stringify(data, null, 2))
  }
  if (ESLINT_HTML_REPORT) {
    const eslint = new ESLint({ cwd: context.cwd })
    const formatter = await eslint.loadFormatter("html")
    const data = await formatter.format(results, { rulesMeta: context.rulesMeta })
    const outputPath = ESLINT_HTML_REPORT
    const dir = dirname(outputPath)
    mkdirSync(dir, { recursive: true })
    writeFileSync(outputPath, data)
  }
  const eslint = new ESLint({ cwd: context.cwd })
  const formatter = await eslint.loadFormatter(ESLINT_FORMAT || undefined)
  return formatter.format(results, { rulesMeta: context.rulesMeta })
}
