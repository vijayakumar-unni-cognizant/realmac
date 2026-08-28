const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-011 — Header collapses to a mobile menu below 768px
test.describe('UI-011', () => {
  test.use({ viewport: { width: 390, height: 844 } })

  test('UI-011: nav hidden by default; toggle opens it and flips aria-expanded/aria-controls; keyboard operable', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const nav = page.locator('#site-header-nav')
    const toggle = page.locator('.cmp-site-header__menu-toggle')

    await expect(nav).toHaveCSS('display', 'none')
    await expect(toggle).toHaveAttribute('aria-expanded', 'false')

    const ariaControls = await toggle.getAttribute('aria-controls')
    expect(ariaControls).toBe(await nav.getAttribute('id'))

    await toggle.focus()
    await page.keyboard.press('Enter')

    await expect(nav).toHaveCSS('display', 'flex')
    await expect(toggle).toHaveAttribute('aria-expanded', 'true')
  })
})
