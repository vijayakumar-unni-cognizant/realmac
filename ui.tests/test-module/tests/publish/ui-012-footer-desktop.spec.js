const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-012 — Footer renders columns + social row + legal bar on dark background
test.describe('UI-012', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-012: dark background, columns/social/legal present, one contentinfo landmark, social aria-labels', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    await expect(page.locator('.cmp-site-footer')).toHaveCSS('background-color', 'rgb(26, 26, 26)')
    await expect(page.locator('.cmp-site-footer__columns')).toBeVisible()
    await expect(page.locator('.cmp-site-footer__social')).toBeVisible()

    const legalText = page.locator('.cmp-site-footer__legal-text')
    await expect(legalText).toBeVisible()
    expect(((await legalText.textContent()) || '').trim().length).toBeGreaterThan(0)

    await expect(page.locator('footer')).toHaveCount(1)

    const socialLinks = page.locator('.cmp-site-footer__social-link')
    const n = await socialLinks.count()
    for (let i = 0; i < n; i++) {
      await expect(socialLinks.nth(i)).toHaveAttribute('aria-label', /.+/)
    }
  })
})
