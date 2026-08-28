const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-005 — Intro renders centered column with distinct lead/body typography
test.describe('UI-005', () => {
  for (const vp of [
    { label: 'desktop', width: 1440, height: 900 },
    { label: 'mobile', width: 390, height: 844 },
  ]) {
    test(`UI-005: intro-lead column + lead/body typography split @ ${vp.label}`, async ({ page }) => {
      await page.setViewportSize({ width: vp.width, height: vp.height })
      await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

      const intro = page.locator('.cmp-text--intro-lead')
      await expect(intro).toBeVisible()

      if (vp.label === 'desktop') {
        await expect(intro).toHaveCSS('max-width', '840px')
      }

      const paragraphs = intro.locator('p')
      await expect(paragraphs.first()).toHaveCSS('font-size', '21px')
      await expect(paragraphs.first()).toHaveCSS('color', 'rgb(51, 51, 51)')

      const count = await paragraphs.count()
      if (count > 1) {
        await expect(paragraphs.nth(1)).toHaveCSS('font-size', '16px')
      }
    })
  }
})
