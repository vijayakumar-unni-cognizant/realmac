const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-004 — Hero has no CTA button
test.describe('UI-004', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-004: hero action-link either absent or not visible (actionsDisabled on policy)', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const action = page.locator('.cmp-teaser--hero .cmp-teaser__action-link')
    const count = await action.count()
    if (count > 0) {
      await expect(action.first()).not.toBeVisible()
    }
  })
})
