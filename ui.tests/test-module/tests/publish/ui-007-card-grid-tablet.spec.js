const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-007 — Card grid renders 2 columns at tablet
test.describe('UI-007', () => {
  test.use({ viewport: { width: 900, height: 1200 } })

  test('UI-007: 2 equal-width tracks at tablet band', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const grid = page.locator('.cmp-container--card-grid > .cmp-container > .aem-Grid')
    const trackCount = await grid.evaluate((el) => getComputedStyle(el).gridTemplateColumns.trim().split(/\s+/).length)
    expect(trackCount).toBe(2)
  })
})
