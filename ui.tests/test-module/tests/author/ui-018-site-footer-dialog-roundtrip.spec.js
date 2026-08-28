const { test, expect } = require('@playwright/test')
const { FOOTER_XF_EDITOR, FOOTER_XF_RENDER } = require('../support/routes')

// UI-018 — site-footer dialog roundtrip persists authored values.
// Runs against the author-chromium project (authenticated via global-setup.js storageState).
test.describe('UI-018', () => {
  test('UI-018: editing Legal Text via the component dialog persists to the rendered legal bar', async ({ page }) => {
    const updatedLegalText = `Copyright test ${Date.now()}`

    await page.goto(FOOTER_XF_EDITOR, { waitUntil: 'domcontentloaded' })

    // See UI-017's note: Granite edit-toolbar/dialog selectors may need adjustment against the
    // live author instance; update selectors here, not the assertions.
    const contentFrame = page.frameLocator('iframe.cq-Editable-dom--container, iframe#ContentFrame')
    const component = contentFrame.locator('[data-cmp-is="site-footer"], .cmp-site-footer').first()
    await component.click()

    const configureButton = page
      .locator('coral-actionbar-item[data-action="CONFIGURE"], [data-action="CONFIGURE"], .cq-Overlay [title="Configure"]')
      .first()
    await configureButton.click()

    const dialog = page.locator('.cq-dialog, coral-dialog').first()
    await dialog.waitFor({ state: 'visible' })

    // Legal Text lives on the "Legal" tab alongside the optional Footer Logo field.
    const legalTab = dialog.getByRole('tab', { name: 'Legal' })
    if (await legalTab.count() > 0) {
      await legalTab.click()
    }

    const legalTextField = dialog.locator('input[name="./legalText"]')
    await legalTextField.fill(updatedLegalText)

    await dialog
      .locator('[data-foundation-wizard-control-action="default"], button[type="submit"], coral-button[icon="check"]')
      .first()
      .click()
    await dialog.waitFor({ state: 'hidden' })

    await page.goto(FOOTER_XF_RENDER, { waitUntil: 'domcontentloaded' })
    await expect(page.locator('.cmp-site-footer__legal-text')).toHaveText(updatedLegalText)
  })
})
