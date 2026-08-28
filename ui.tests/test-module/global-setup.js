const { request } = require('@playwright/test')
const fs = require('fs')
const path = require('path')

const STATE_PATH = path.join(__dirname, '.auth', 'state.json')

// AEM author 302-redirects unauthenticated requests to the login form rather than sending a
// 401 challenge, so httpCredentials would silently fail. Log in once via Granite
// j_security_check and capture the login-token cookie as storageState for the author-tier
// project; the publish-tier projects run anonymously and never load this state.
module.exports = async () => {
  const authorURL = process.env.AEM_AUTHOR_URL || 'http://localhost:4502'
  const user = process.env.AEM_AUTHOR_USERNAME || 'admin'
  const pass = process.env.AEM_AUTHOR_PASSWORD || 'admin'
  fs.mkdirSync(path.dirname(STATE_PATH), { recursive: true })
  const ctx = await request.newContext({ baseURL: authorURL, ignoreHTTPSErrors: true })
  // j_validate=true -> 200 + login-token cookie on success, 403 on failure, no redirect.
  const resp = await ctx.post('/libs/granite/core/content/login.html/j_security_check', {
    form: { _charset_: 'utf-8', j_username: user, j_password: pass, j_validate: 'true' },
  })
  if (!resp.ok()) {
    throw new Error(`AEM author login failed (HTTP ${resp.status()}) at ${authorURL}.`)
  }
  await ctx.storageState({ path: STATE_PATH })
  await ctx.dispose()
}
