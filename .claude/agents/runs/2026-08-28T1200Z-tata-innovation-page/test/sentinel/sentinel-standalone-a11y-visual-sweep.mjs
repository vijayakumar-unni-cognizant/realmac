// SENTINEL-AUTHORED, STANDALONE utility script — NOT part of the ui.tests Playwright harness.
// NOT discovered by `npx playwright test` (lives outside testDir './tests', not a *.spec.js file).
// NOT a modification of playwright.config.js / global-setup.js / any harness file.
//
// Why this exists: playwright.config.js's top-level `globalSetup` (global-setup.js) performs a
// Granite j_security_check form login (admin/admin defaults) unconditionally for the ENTIRE test
// binary regardless of --project filtering. Against this run's real AEMaaCS Author tier (bearer-token
// auth mode, no local admin/admin account), that login 403s and aborts `npm test` before ANY spec
// executes — including the 16 anonymous Publish-tier specs that need no author auth at all. This is
// reported as a NEW, high-severity, Blockwright-owned harness defect (see sentinel-report).
//
// This script uses Playwright's OWN pinned Chromium (via @playwright/test's re-exported `chromium`
// launcher, already installed in this module's node_modules) directly through the library API —
// bypassing only the broken CLI test-runner entry point, not any harness file — to still deliver the
// PUBLISH-tier tracks that are Sentinel's own (not Blockwright-authored spec) responsibility:
// the a11y-deep full-ruleset axe sweep and the Tier-A/baseline screenshot capture. The 16 Blockwright
// UI-00x Publish specs themselves cannot be run this way (they require the Playwright Test Runner's
// test()/expect() registration, which is inseparable from playwright.config.js) — those remain
// genuinely blocked and are reported as such.

import { chromium } from '@playwright/test'
import AxeBuilder from '@axe-core/playwright'
import fs from 'fs'
import path from 'path'

const PUBLISH_URL = process.env.AEM_PUBLISH_URL
const PAGE_PATH = '/content/realmac/us/en/innovation.html'
const OUT_DIR = process.env.SENTINEL_OUT_DIR
if (!PUBLISH_URL || !OUT_DIR) {
  console.error('FATAL: AEM_PUBLISH_URL and SENTINEL_OUT_DIR must be set')
  process.exit(1)
}

const VIEWPORTS = [
  { label: 'desktop', width: 1440, height: 900 },
  { label: 'mobile', width: 390, height: 844 },
]

const results = { url: PUBLISH_URL + PAGE_PATH, viewports: {} }

const browser = await chromium.launch({ headless: true })
try {
  for (const vp of VIEWPORTS) {
    const context = await browser.newContext({ viewport: { width: vp.width, height: vp.height } })
    const page = await context.newPage()
    const consoleErrors = []
    page.on('console', (msg) => { if (msg.type() === 'error') consoleErrors.push(msg.text()) })
    // NOTE: `?wcmmode=disabled` is an AUTHOR-tier preview trick (suppresses edit-mode chrome).
    // On this Publish tier it 404s (Dispatcher correctly blocks the wcmmode query param on publish
    // to prevent WCM-debug-mode leakage) — confirmed via curl before fixing this script. Publish
    // never renders editor chrome in the first place, so no query param is needed here at all.
    await page.goto(PUBLISH_URL + PAGE_PATH, { waitUntil: 'networkidle' })

    // screenshot (full page, clean render, no editor chrome)
    const screenshotPath = path.join(OUT_DIR, 'screenshots', `innovation-${vp.label}.png`)
    await page.screenshot({ path: screenshotPath, fullPage: true })

    // full WCAG ruleset (2.0 A/AA + 2.1 A/AA + 2.2 A/AA) — not filtered to critical/serious;
    // that filtering happens in the report's classification step, not the scan itself.
    const axeResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa', 'wcag22aa', 'best-practice'])
      .analyze()

    // one-<h1> gate — raw DOM count, per contract
    const h1Count = await page.locator('h1').count()

    results.viewports[vp.label] = {
      screenshot: screenshotPath,
      consoleErrors,
      h1Count,
      violations: axeResults.violations.map((v) => ({
        id: v.id,
        impact: v.impact,
        description: v.description,
        help: v.help,
        helpUrl: v.helpUrl,
        nodes: v.nodes.map((n) => ({ target: n.target, html: n.html, failureSummary: n.failureSummary })),
      })),
      passesCount: axeResults.passes.length,
      violationsCount: axeResults.violations.length,
    }
    await context.close()
  }
} finally {
  await browser.close()
}

fs.writeFileSync(path.join(OUT_DIR, 'axe-innovation.json'), JSON.stringify(results, null, 2))
console.log('SENTINEL_SWEEP_DONE')
console.log(JSON.stringify({
  desktop_violations: results.viewports.desktop.violationsCount,
  mobile_violations: results.viewports.mobile.violationsCount,
  desktop_h1: results.viewports.desktop.h1Count,
  mobile_h1: results.viewports.mobile.h1Count,
  desktop_console_errors: results.viewports.desktop.consoleErrors.length,
}, null, 2))
