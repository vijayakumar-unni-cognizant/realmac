const { test, expect } = require('@playwright/test')
const { HEADER_XF_EDITOR, HEADER_XF_RENDER } = require('../support/routes')

// UI-017 — site-header dialog roundtrip persists authored values.
// Runs against the author-chromium project (authenticated via global-setup.js storageState).
test.describe('UI-017', () => {
  test('UI-017: editing Logo Alt Text via the component dialog persists to the rendered <img alt>', async ({ page }) => {
    const updatedAlt = `Realmac Logo ${Date.now()}`

    await page.goto(HEADER_XF_EDITOR, { waitUntil: 'domcontentloaded' })

    // Select the site-header component on the authoring canvas, then open its Configure dialog.
    // Note for Sentinel (post-deploy): Granite's edit-toolbar/dialog markup can shift between AEM
    // versions — if these selectors need adjustment against the live author instance, update them
    // here rather than loosening the assertions below.
    const contentFrame = page.frameLocator('iframe.cq-Editable-dom--container, iframe#ContentFrame')
    const component = contentFrame.locator('[data-cmp-is="site-header"], .cmp-site-header').first()
    await component.click()

    const configureButton = page
      .locator('coral-actionbar-item[data-action="CONFIGURE"], [data-action="CONFIGURE"], .cq-Overlay [title="Configure"]')
      .first()
    await configureButton.click()

    const dialog = page.locator('.cq-dialog, coral-dialog').first()
    await dialog.waitFor({ state: 'visible' })

    const logoAltField = dialog.locator('input[name="./logoAlt"]')
    await logoAltField.fill(updatedAlt)

    await dialog
      .locator('[data-foundation-wizard-control-action="default"], button[type="submit"], coral-button[icon="check"]')
      .first()
      .click()
    await dialog.waitFor({ state: 'hidden' })

    await page.goto(HEADER_XF_RENDER, { waitUntil: 'domcontentloaded' })
    await expect(page.locator('.cmp-site-header__logo-image').first()).toHaveAttribute('alt', updatedAlt)
  })
})
