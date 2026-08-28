const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-008 — Card grid collapses to 1 column at mobile
test.describe('UI-008', () => {
  test.use({ viewport: { width: 390, height: 844 } })

  test('UI-008: single track, 4 cards stack vertically (no shared top offset)', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const grid = page.locator('.cmp-container--card-grid > .cmp-container > .aem-Grid')
    const trackCount = await grid.evaluate((el) => getComputedStyle(el).gridTemplateColumns.trim().split(/\s+/).length)
    expect(trackCount).toBe(1)

    const tops = await grid.evaluate((el) =>
      [...el.children].filter((c) => c.offsetParent !== null).map((c) => c.offsetTop))
    expect(new Set(tops).size).toBe(tops.length) // every card has a distinct top offset — stacked
  })
})
