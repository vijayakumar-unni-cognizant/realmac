const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-001 — Hero renders full-bleed with overlaid title (desktop)
test.describe('UI-001', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-001: hero full-bleed 480px, single <h1> "Innovation", white title, non-empty image alt', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const hero = page.locator('.cmp-teaser--hero')
    await expect(hero).toBeVisible()
    await expect(hero).toHaveCSS('height', '480px')

    const h1 = page.locator('h1')
    await expect(h1).toHaveCount(1)
    await expect(h1).toHaveText('Innovation')
    await expect(hero.locator('.cmp-teaser__title')).toHaveCSS('color', 'rgb(255, 255, 255)')

    const heroImg = hero.locator('.cmp-teaser__image img').first()
    await expect(heroImg).toHaveAttribute('alt', /.+/)
  })
})
