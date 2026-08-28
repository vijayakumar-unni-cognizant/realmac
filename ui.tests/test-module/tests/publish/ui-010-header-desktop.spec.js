const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-010 — Header renders logo + nav + utility icons in one row (desktop)
test.describe('UI-010', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-010: flex row, DOM order logo -> nav -> utility, one banner landmark, utility aria-labels', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const inner = page.locator('.cmp-site-header__inner')
    await expect(inner).toHaveCSS('display', 'flex')
    await expect(inner).toHaveCSS('justify-content', 'space-between')

    const order = await inner.evaluate((el) => [...el.children].map((c) => c.className))
    const logoIdx = order.findIndex((c) => c.includes('cmp-site-header__logo'))
    const navIdx = order.findIndex((c) => c.includes('cmp-site-header__nav'))
    const utilityIdx = order.findIndex((c) => c.includes('cmp-site-header__utility-links'))
    expect(logoIdx).toBeGreaterThanOrEqual(0)
    expect(logoIdx).toBeLessThan(navIdx)
    expect(navIdx).toBeLessThan(utilityIdx)

    await expect(page.locator('header')).toHaveCount(1)

    const utilityLinks = page.locator('.cmp-site-header__utility-link')
    const n = await utilityLinks.count()
    for (let i = 0; i < n; i++) {
      await expect(utilityLinks.nth(i)).toHaveAttribute('aria-label', /.+/)
    }
  })
})
