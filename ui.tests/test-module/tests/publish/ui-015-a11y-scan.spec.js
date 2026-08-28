const { test, expect } = require('@playwright/test')
const AxeBuilder = require('@axe-core/playwright').default
const { LANDING_PAGE } = require('../support/routes')

// UI-015 — Full-page accessibility scan has zero critical violations
// Cross-browser coverage comes from the publish-chromium + publish-webkit projects
// in playwright.config.js both discovering and running this spec.
test.describe('UI-015', () => {
  for (const vp of [
    { label: 'desktop', width: 1440, height: 900 },
    { label: 'mobile', width: 390, height: 844 },
  ]) {
    test(`UI-015: axe scan (WCAG 2.1 AA) has zero critical/serious violations @ ${vp.label}`, async ({ page }) => {
      await page.setViewportSize({ width: vp.width, height: vp.height })
      await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

      const { violations } = await new AxeBuilder({ page })
        .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
        .analyze()

      const blocking = violations.filter((v) => v.impact === 'critical' || v.impact === 'serious')
      expect(blocking, JSON.stringify(blocking.map((v) => v.id), null, 2)).toEqual([])
    })
  }
})
