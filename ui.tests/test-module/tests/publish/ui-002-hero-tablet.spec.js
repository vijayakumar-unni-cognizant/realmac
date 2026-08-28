const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-002 — Hero title scales down at tablet
test.describe('UI-002', () => {
  test.use({ viewport: { width: 900, height: 1200 } })

  test('UI-002: hero height 360px, title font-size 36px at tablet band (768-1199px)', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    await expect(page.locator('.cmp-teaser--hero')).toHaveCSS('height', '360px')
    await expect(page.locator('.cmp-teaser--hero .cmp-teaser__title')).toHaveCSS('font-size', '36px')
  })
})
