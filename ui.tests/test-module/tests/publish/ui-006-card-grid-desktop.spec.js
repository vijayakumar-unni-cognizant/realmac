const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-006 — Card grid renders 2 columns at desktop
test.describe('UI-006', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-006: 4 grid-item children, 2 equal-width tracks', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const grid = page.locator('.cmp-container--card-grid > .cmp-container > .aem-Grid')
    await expect(grid).toBeVisible()
    await expect(grid.locator('> .aem-GridColumn')).toHaveCount(4)

    const trackCount = await grid.evaluate((el) => getComputedStyle(el).gridTemplateColumns.trim().split(/\s+/).length)
    expect(trackCount).toBe(2)
  })
})
