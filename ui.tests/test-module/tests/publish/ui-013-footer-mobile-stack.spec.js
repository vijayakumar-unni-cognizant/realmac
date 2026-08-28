const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-013 — Footer columns stack at mobile
test.describe('UI-013', () => {
  test.use({ viewport: { width: 390, height: 844 } })

  test('UI-013: single-track column grid, social row centered', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const columns = page.locator('.cmp-site-footer__columns')
    const trackCount = await columns.evaluate((el) => getComputedStyle(el).gridTemplateColumns.trim().split(/\s+/).length)
    expect(trackCount).toBe(1)

    await expect(page.locator('.cmp-site-footer__social')).toHaveCSS('justify-content', 'center')
  })
})
