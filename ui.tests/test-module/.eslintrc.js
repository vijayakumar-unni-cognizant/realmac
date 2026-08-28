module.exports = {
  root: true,
  // browser: true is required because specs pass callbacks into page.evaluate() that
  // execute in the browser context (document, getComputedStyle, etc.), even though the
  // spec files themselves run under Node.
  env: { node: true, browser: true, es2022: true },
  parserOptions: { ecmaVersion: 2022, sourceType: 'commonjs' },
  extends: ['eslint:recommended'],
  ignorePatterns: [
    'node/**', 'node_modules/**', 'results/**', 'html-report/**',
    'playwright-report/**', 'test-results/**',
  ],
  rules: {},
}
