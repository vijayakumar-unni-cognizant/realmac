const { defineConfig, devices } = require('@playwright/test')
const path = require('path')

const reportsPath = process.env.REPORTS_PATH || 'results'
const authorURL = process.env.AEM_AUTHOR_URL || 'http://localhost:4502'
const publishURL = process.env.AEM_PUBLISH_URL || 'http://localhost:4503'

// Every scenario in design/ui-test-scenarios.md is Publish-tier except UI-017/UI-018
// (author dialog roundtrips). Specs live under tests/publish/ and tests/author/ so the
// project's testDir scopes each tier to the right base URL/auth without hardcoding a host
// or credential in any spec file.
module.exports = defineConfig({
  testDir: './tests',
  globalSetup: require.resolve('./global-setup'),
  outputDir: path.join(reportsPath, 'artifacts'),
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  timeout: 60_000,
  expect: { timeout: 10_000 },
  reporter: [
    ['list'],
    ['junit', { outputFile: path.join(reportsPath, 'results.xml') }],
    ['html', { outputFolder: path.join(reportsPath, 'html-report'), open: 'never' }],
  ],
  use: {
    ignoreHTTPSErrors: true,
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'on-first-retry',
  },
  projects: [
    // Publish tier — anonymous, public render/user-journey scenarios (UI-001..UI-016).
    // Chromium + WebKit per UI-015's cross-browser a11y requirement.
    {
      name: 'publish-chromium',
      testDir: './tests/publish',
      use: { ...devices['Desktop Chrome'], baseURL: publishURL },
    },
    {
      name: 'publish-webkit',
      testDir: './tests/publish',
      use: { ...devices['Desktop Safari'], baseURL: publishURL },
    },
    // Author tier — authenticated authoring-surface journeys (UI-017/UI-018).
    {
      name: 'author-chromium',
      testDir: './tests/author',
      use: {
        ...devices['Desktop Chrome'],
        baseURL: authorURL,
        storageState: path.join(__dirname, '.auth', 'state.json'),
      },
    },
  ],
})
