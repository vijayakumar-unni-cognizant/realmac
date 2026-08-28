const { test, expect } = require('@playwright/test')
const { LANDING_PAGE } = require('../support/routes')

// UI-009 — Each card's arrow link navigates to its target
test.describe('UI-009', () => {
  test.use({ viewport: { width: 1440, height: 900 } })

  test('UI-009: first card action link has a discernible name and navigates to its authored href', async ({ page }) => {
    await page.goto(LANDING_PAGE, { waitUntil: 'domcontentloaded' })

    const link = page.locator('.cmp-teaser--innovation-card').first().locator('.cmp-teaser__action-link')
    await expect(link).toBeVisible()

    const name = ((await link.textContent()) || '').trim()
    expect(name.length).toBeGreaterThan(0)

    const href = await link.getAttribute('href')
    expect(href).toBeTruthy()

    await link.click()
    await expect(page).toHaveURL(new RegExp(href.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '$'))
  })
})
