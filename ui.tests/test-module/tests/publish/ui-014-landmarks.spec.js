const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-014 — Semantic landmarks present exactly once each
test.describe('UI-014', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-014: exactly one <header> (banner), one <main>, one <footer> (contentinfo)', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    await expect(page.locator('header')).toHaveCount(1)
    await expect(page.locator('main')).toHaveCount(1)
    await expect(page.locator('footer')).toHaveCount(1)
  })
})
