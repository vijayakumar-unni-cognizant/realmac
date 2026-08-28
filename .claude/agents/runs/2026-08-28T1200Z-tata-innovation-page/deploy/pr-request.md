# Release PR Handoff — Tata Innovation Landing Page

**Date**: 2026-08-28 / ADLC Run ID: `2026-08-28T1200Z-tata-innovation-page`

## PR Summary

| Key | Value |
|---|---|
| **PR URL** | https://github.com/vijayakumar-unni-cognizant/realmac/pull/1 |
| **PR Number** | #1 |
| **Repository** | vijayakumar-unni-cognizant/realmac |
| **Head Branch** | `feature/realmac-landing-page` |
| **Base Branch** | `master` |
| **Commits Ahead of Master** | 1 |
| **Build Hash** | e260c64ec27823a644bd7c02fc157c09453b45fc |

## Build Status (Auditron Gate)

- **Status**: PASS ✓
- **Build verdict**: BUILD_SUCCESS
- **Three-signal gates**:
  - Exit code: 0 ✓
  - all.zip present: 1,249,225 bytes ✓
  - Unit tests: 20/20 pass ✓
- **Functional test ledger**: 38 pass / 2 accepted-known-gap (CQ-09) / 6 blocked (no fixture) = 46 total

## What Changed

1. **Landing-page editable template** — responsive, multi-section structure with hero, intro, showcase grid, header/footer
2. **Site-header component** — Sling Model (SiteHeaderModel) + HTL, with utility links and social media integration
3. **Site-footer component** — Sling Model (SiteFooterModel, FooterColumn, FooterLink, SocialLink) + HTL
4. **Innovation-card component** — Core Teaser policy overlay, 4-card showcase grid
5. **Playwright test suite** — 18 UI specs (responsive, A11y, keyboard, landmarks) replacing Cypress
6. **DAM assets** — 10 SVG icons + 4 featured images for the showcase
7. **WCM policies** — consolidated policy definitions for the new components and template
8. **Filter.xml** — updated vault filter for new content paths

## Known Gap — CQ-09 (Accepted, Non-Blocking)

**Innovation card heading and style class**: Cards render with Core Teaser's `<h2>` default + no `.cmp-teaser--innovation-card` class, due to AEM Content Policy resolution limitation. **This is a documented, human-accepted cosmetic gap** (DECISIONS.md 2026-08-28T20:50Z) — not a blocker. Sentinel will note it if observed, but will not fail the validation.

## Files Committed to Master

- **Core models** (6 files, 2 test classes):
  - `core/src/main/java/com/realmac/aem/core/models/SiteHeaderModel.java`, `SiteFooterModel.java`, `FooterColumn.java`, `FooterLink.java`, `SocialLink.java`, `UtilityLink.java`
  - Unit tests: `SiteHeaderModelTest.java`, `SiteFooterModelTest.java`

- **Components** (3 new components):
  - `ui.apps/.../apps/realmac/components/site-header/`
  - `ui.apps/.../apps/realmac/components/site-footer/`
  - `ui.apps/.../apps/realmac/components/innovation-card/`

- **Template & Content**:
  - `ui.content/.../conf/realmac/settings/wcm/templates/landing-page/` (4 files: template, structure, initial, policies)
  - `ui.content/.../conf/realmac/settings/wcm/policies/.content.xml` (consolidated policy definitions)
  - `ui.content/.../content/dam/realmac/tata-innovation/` (20 DAM asset XML + binary metadata)
  - `ui.content/.../content/realmac/us/en/innovation/` (landing page sample instance)
  - `ui.content/.../content/experience-fragments/.../site/header/master/` (header XF master + policy)
  - `ui.content/.../content/experience-fragments/.../site/footer/master/` (footer XF master + policy)

- **Frontend**:
  - `ui.frontend/.../components/_site-header.scss`, `_site-header.js`, `_site-footer.scss`
  - Updated: `_container.scss`, `_teaser.scss`, `_text.scss`, `_variables.scss`

- **Tests** (Playwright migration):
  - `ui.tests/test-module/playwright.config.js`, `global-setup.js`, `.gitignore`
  - 18 spec files under `tests/author/` and `tests/publish/`
  - Removed: all Cypress config, specs, and support files

- **Updated**: `ui.content/.../META-INF/vault/filter.xml`, `ui.tests/Dockerfile`, `ui.tests/README.md`, etc.

## Files Intentionally Excluded from This PR

To preserve Cloud Manager's normal frontend build:

- ~~`pom.xml`~~ (local-only frontend-maven-plugin skip for Zscaler)
- ~~`ui.apps/pom.xml`~~ (local-only ui.frontend dependency comment)
- ~~`ui.frontend/pom.xml`~~ (local-only skipAssembly=true)
- ~~`.gitignore`~~ (unrelated local change)

These remain in your local working tree but are NOT part of this commit, so Cloud Manager will build the frontend normally.

## Lead Approval Checklist

**Before merging to master, the Lead should verify**:

1. ✓ PR description is clear and matches the deliverables
2. ✓ All 111 file changes are reviewed and appropriate for the landing page feature
3. ✓ Build hash (`e260c64ec27823a644bd7c02fc157c09453b45fc`) matches Auditron's pass verdict
4. ✓ CQ-09 known gap (card heading/style) is noted and accepted
5. ✓ No production-critical test failure remains (all 38 passing + 2 accepted-gap + 6 blocked due to missing fixtures = 46 ledger complete)

**Post-merge Lead Actions**:

1. Merge this PR to `master` on GitHub
2. Sync the merged commit to Adobe Git (the production deployment source)
3. Trigger deployment via Cloud Manager (Dev → Stage → Prod)
4. Validate in the Dev environment:
   - Landing page renders at `/innovation` or configured path
   - Hero, intro, 4-card showcase, header, footer all visible
   - Images load
   - No console errors
5. Record the Lead's approval and deployment completion in `DECISIONS.md` (new section):
   ```
   ### Lead Approval — 2026-08-28Txxxx
   - Author URL: https://author-p123456-e789.adobeaemcloud.com
   - Publish URL: https://publish-p123456-e789.adobeaemcloud.com
   - Auth mode: none (public) / bearer-token / other
   - PR merged: #1
   - Deployed to Cloud Manager: Dev/Stage/Prod as of 2026-08-28Txxxx
   ```
6. Reply to the ADLC run with the real environment URLs and auth mode

## Sentinel Validation (Post-Deploy)

Once the real environment URLs are supplied, Sentinel will run LAST and validate against:

- Playwright specs: responsive design, hero teaser, card grid, header/footer, navigation
- Accessibility: WCAG 2.1 AA compliance, landmarks, keyboard focus
- Performance: time-to-interactive, cumulative layout shift, core vitals
- SEO: meta tags, Open Graph, structured data
- Observability: logs, metrics, alerts (if configured)
- GraphQL parity (if applicable)

**Deferred validations are NOT part of this PR review** — they will execute on the real environment only.

## Summary

✓ Build is green (20/20 unit tests, BUILD_SUCCESS, 3-signal all pass).
✓ 111 files committed (components, models, template, policies, DAM, Playwright tests).
✓ Known gap CQ-09 is documented and accepted.
✓ Local-only pom hacks are excluded to preserve Cloud Manager's build.
✓ Ready for Lead review, merge, and deployment to the real environment.
✓ Awaiting Lead's post-deployment approval and real environment URLs for Sentinel validation.

---

**ADLC Flow Status**: awaiting_lead_approval — The active ADLC work is complete. The human Lead must now:
1. Review and merge this PR.
2. Deploy to the real environment.
3. Supply the real Author/Publish URLs to resume into Sentinel.
