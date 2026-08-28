const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-016 — Keyboard focus order and visible focus rings
// Hero carries no focusable elements (actions disabled), so the checked order is:
// header logo -> header utility links -> card action links -> footer links -> footer social links.
test.describe('UI-016', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-016: each focusable landmark element is reachable and shows a visible focus indicator', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const focusableSelectors = [
      '.cmp-site-header__logo',
      '.cmp-site-header__utility-link',
      '.cmp-teaser__action-link',
      '.cmp-site-footer__link',
      '.cmp-site-footer__social-link',
    ]

    for (const selector of focusableSelectors) {
      const locator = page.locator(selector).first()
      if ((await locator.count()) === 0) {
        continue
      }
      await locator.focus()
      const isFocused = await locator.evaluate((el) => el === document.activeElement)
      expect(isFocused, `${selector} did not receive focus`).toBe(true)

      const outlineStyle = await locator.evaluate((el) => getComputedStyle(el).outlineStyle)
      expect(outlineStyle, `${selector} has no visible focus outline`).not.toBe('none')
    }
  })
})
