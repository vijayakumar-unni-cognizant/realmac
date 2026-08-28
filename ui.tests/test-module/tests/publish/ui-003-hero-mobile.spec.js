const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-003 — Hero title scales down at mobile
test.describe('UI-003', () => {
  test.use({ viewport: { width: 390, height: 844 } })

  test('UI-003: hero height 280px, title font-size 28px, content padding 0/24/24/24 at mobile', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    await expect(page.locator('.cmp-teaser--hero')).toHaveCSS('height', '280px')
    await expect(page.locator('.cmp-teaser--hero .cmp-teaser__title')).toHaveCSS('font-size', '28px')
    await expect(page.locator('.cmp-teaser--hero .cmp-teaser__content')).toHaveCSS('padding', '0px 24px 24px 24px')
  })
})
