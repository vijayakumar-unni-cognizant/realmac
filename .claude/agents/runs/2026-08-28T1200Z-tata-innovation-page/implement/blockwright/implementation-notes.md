# Blockwright Implementation Notes — Tata Innovation Landing Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Tracks completed:** components, templates (structure only), services (SCSS/JS build-side), ui-tests.

## Track 1 — `site-header` / `site-footer` components

- Authored under `ui.apps/src/main/content/jcr_root/apps/realmac/components/{site-header,site-footer}/`:
  `.content.xml` (componentGroup="Realmac - Structure"), `<name>.html`, `_cq_dialog/.content.xml`.
- Dialogs follow `dialog-specifications.md` verbatim (field names, resource types, required flags,
  tab structure). All `_cq_dialog/.content.xml` files declare the 5 standard namespaces (B1). Top-level
  image fields use `fileupload` (`allowUpload=false`); multifield-nested image fields use `pathfield`
  (B18).
- Sling Models under `core/src/main/java/com/realmac/aem/core/models/`:
  `SiteHeaderModel` + `UtilityLink`; `SiteFooterModel` + `FooterColumn` + `FooterLink` + `SocialLink`.
  All six use the array-form `@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})`
  (B2) — re-scanned via the `best-practices` skill; no deprecated API, no `loginAdministrative`, no
  JCR observation / OSGi EventHandler / replication / AssetManager usage found.
- **Design decision (multifield reading):** the spec text mentions `@ChildResource` injection for
  the multifield collections; implemented instead as `@SlingObject Resource` + a manual
  `resource.getChild(name)` null-guard in `@PostConstruct` (per best-practices **B15**), returning
  `Collections.emptyList()` when the multifield node is absent (freshly-dropped, dialog untouched).
  Functionally equivalent outcome (`List<X>` populated, adapted per child, empty-tolerant) with
  guaranteed null-safety. Documented here as the deviation-with-rationale this agent's decision
  authority covers (Java package layout / class design).
- Unit tests: `SiteHeaderModelTest`, `SiteFooterModelTest` under
  `core/src/test/java/com/realmac/aem/core/models/`, using `AppAemContext` (project convention) +
  wcm.io AEM Mocks. Cover: complete data, empty, partial data, defaults
  (`navigationStructureDepth` default `1`, `logoLinkURL`/`navigationRoot` defaults), multifield with
  multiple/zero items, nested multifield (columns→links), freshly-authored (multifield node absent)
  null-safety, and `hasContent()` true/false branches. Discharges TC-038..TC-045 (US-009).
- HTL emits the exact BEM classes from `component-specifications.md` (`.cmp-site-header`,
  `__inner`, `__logo`, `__logo-image`, `__menu-toggle`, `__nav`, `__utility-links`, `__utility-item`,
  `__utility-link`, `__utility-icon`; `.cmp-site-footer` + its 8 elements). Both wrapper elements are
  the real semantic tag (`<header>`/`<footer>`) authored directly by this custom component's own HTL —
  no Core XF v2 involved here (B16); the D10 `<div>`-wrapper constraint applies only to the
  `realmac/components/experiencefragment` *reference* in the template structure, not to these two
  custom components.
- Embedded navigation inside `site-header` is the synthetic `data-sly-resource` call against
  `realmac/components/navigation` exactly as specified in `component-specifications.md § A.1` — no
  child node, no policy resolution needed for it.
- No per-component runtime clientlib was created under `ui.apps`; no HTL references a clientlib
  directly. The one JS behavior needed (mobile menu toggle) lives at
  `ui.frontend/src/main/webpack/components/_site-header.js`, picked up by the existing
  `main.ts` glob-import (`'../components/**/*.js'`) and compiled into the project's single
  `realmac.site` clientlib — consistent with the rest of the project's component JS convention
  (mirrors `_helloworld.js`'s `data-cmp-is`/`data-cmp-hook-*` pattern).

## Track 2 — `landing-page` template (structure only)

- `ui.content/src/main/content/jcr_root/conf/realmac/settings/wcm/templates/landing-page/`:
  `.content.xml` (template type `page` reused, `allowedPaths="[/content/realmac(/.*)?]"` on
  `jcr:content` per the `create-editable-template` skill's convention — not on the `cq:Template`
  root), `structure/.content.xml` (copied verbatim from `template-design.md § Structure`: EF header
  ref → main-landmark container → innermost editable container → EF footer ref; no structural
  `<title>` per D22; forbidden-attributes checklist verified absent — no `editable=true` on `root`,
  no `editable=false`/`decoration=false` on the EF refs, only the innermost container carries
  `editable=true`), `initial/.content.xml` (minimal, mirrors `structure/` with no authored
  components, following the project's `xf-web-variation/initial` convention of omitting
  `editable`/EF-ref nodes from `initial`).
- Did **not** author `policies/` under this template (Configsmith's track — confirmed already
  landed on disk at `templates/landing-page/policies/.content.xml` per
  `handoffs/configsmith.yaml`, alongside this agent's `structure/`/`initial/`/`.content.xml`,
  cleanly non-overlapping).
- Did **not** touch `cq:allowedTemplates` on any content branch — `template-design.md` documents
  that the existing permissive regex at `/content/realmac` already covers `landing-page`; that's a
  Composer verification item, not a Blockwright edit.

## Track 3 — SCSS (4 Style System variants + 2 chrome components)

- `ui.frontend/src/main/webpack/components/_teaser.scss` — appended `.cmp-teaser--hero` and
  `.cmp-teaser--innovation-card` below the existing stub selectors (B9). Image sizing contract
  (B21) applied to the card image (`width:100%; height:auto; max-width:100%` + `aspect-ratio`/
  `object-fit:cover` for the cropped shape); hero image is absolutely positioned `inset:0` so it's
  already constrained by its wrapper.
- `ui.frontend/src/main/webpack/components/_text.scss` — appended `.cmp-text--intro-lead`.
- `ui.frontend/src/main/webpack/components/_container.scss` — appended `.cmp-container--card-grid`
  using the mandatory `.cmp-container--card-grid > .cmp-container > .aem-Grid` selector chain (B20 —
  the Style System class lands on the outer decoration wrapper, not on `.cmp-container`), with the
  `.aem-Grid` clearfix-pseudo neutralization and `aem-GridColumn` width reset.
- `ui.frontend/src/main/webpack/components/_site-header.scss` (new) and `_site-footer.scss` (new) —
  BEM classes matching the two components' HTL. Footer background is dark (`#1a1a1a`); every
  text-carrying descendant (`__column-heading`, `__link`, `__legal-text`) sets its own explicit
  foreground color rather than relying on inheritance (B13 principle — no element left to fall back
  to browser-default black-on-dark). Header is `position: sticky; z-index: $z-header` so it sits
  above the hero per Region 1. Responsive collapse: header hamburger `<768px` (Region 1); footer
  columns `repeat(4,1fr)` desktop → `1fr` stacked `<768px` (Region 5, matching the pinned
  Pixel-Verified table — no separate 2-column tablet step was pinned by the design-token-audit for
  the footer, so none was added).
- `ui.frontend/src/main/webpack/components/_site-header.js` (new) — minimal mobile menu-toggle
  behavior (`aria-expanded`/`aria-label` swap, `data-cmp-is-open` attribute driving the CSS), no
  heavy hydration, mirrors the project's existing `_helloworld.js` init/MutationObserver pattern.
- All new pinned design tokens added to `ui.frontend/src/main/webpack/site/_variables.scss` under a
  dedicated "Tata Innovation Landing Page" section (colors, typography, spacing, radius, z-index) —
  none of the existing generic tokens (`$color-foreground` etc.) overlapped with the pinned hex
  values, so new tokens were added rather than reusing unrelated ones.
- No `main.scss` edit was needed — it already glob-imports `../components/**/*.scss`, so the two new
  partials are picked up automatically.
- **B17 legacy-selector sweep:** grepped for `^(header|footer)\.cmp-experiencefragment` across
  `ui.frontend` — zero hits. (The project's pre-existing `experiencefragment_header.scss`/
  `_footer.scss` use an unrelated `header.experiencefragment`/`footer.experiencefragment` selector
  from the archetype's legacy demo content, not the Core XF v2 BEM contract — out of this run's
  scope, untouched.)
- **B11 DOM-class verification:** did not need to curl a live instance for Core Teaser/Container v2
  class names — the SCSS invariants were copied verbatim from `component-specifications.md`'s
  Pixel-Verified tables, which are themselves pre-verified against the actual v2 DOM contract
  (`.cmp-teaser__title` directly, no `-text` inner span; Style System class on the outer wrapper for
  Container).
- **Static validation performed (no `mvn`):** `npx sass` compiled all four modified/new partials
  (`_teaser.scss`, `_text.scss`, `_container.scss`, `_site-header.scss`, `_site-footer.scss`)
  together with `_variables.scss`/`_base.scss` with zero errors; compiled output spot-checked for
  the `.cmp-container--card-grid > .cmp-container > .aem-Grid` chain and all four/six BEM class
  families.
- **B8 runtime smoke render:** `runtime_style_system_classes: skipped(not-yet-deployed)` — the local
  SDK is reachable (401 on `/system/console`, i.e. up but unauthenticated) but none of this run's
  `ui.apps`/`ui.content` changes have been installed yet (Auditron owns the local install per the
  ADLC Build Validation Gate). Nothing to smoke-render until that install happens. Flagged for a
  follow-up smoke pass after Auditron's `autoInstallSinglePackage`.

## Track 4 — Playwright UI-test harness + specs

- **Harness state on entry:** Cypress (`ui.tests/test-module/cypress.config.js` +
  `cypress/e2e/*.cy.js` + `cypress` in `package.json`). Migrated to Playwright per
  `references/playwright-ui-test-module.md`:
  - `ui.tests/Dockerfile` → `mcr.microsoft.com/playwright:v1.49.1-jammy` base image.
  - `ui.tests/test-module/run.sh` → removed Xvfb block (Playwright runs headless in the base
    image), kept the Cloud Manager EaaS proxy block verbatim.
  - `ui.tests/test-module/package.json` → replaced with the Playwright template
    (`@playwright/test@1.49.1`, `@axe-core/playwright`, `eslint`). Version pin matches the
    Dockerfile tag (B: version pin rule).
  - Added `playwright.config.js`, `global-setup.js` (Granite `j_security_check` login →
    `storageState`), `.gitignore`.
  - **Deleted** `cypress/` (all 4 `*.cy.js` specs + fixtures/support), `cypress.config.js`,
    `reporter.config.js`, `test-module/README.md` (Cypress-specific), and the stale
    Cypress-locked `package-lock.json`.
  - `ui.tests/pom.xml` and `ui.tests/assembly-ui-test-docker-context.xml` — **untouched**
    (framework-agnostic, `DO NOT MODIFY` header preserved).
  - `ui.tests/README.md` updated to describe the Playwright harness instead of Cypress
    (documentation only, not a build file).
- **Tier parameterization:** `playwright.config.js` defines three projects —
  `publish-chromium`/`publish-webkit` (testDir `tests/publish`, `baseURL` from
  `AEM_PUBLISH_URL`, anonymous) and `author-chromium` (testDir `tests/author`, `baseURL` from
  `AEM_AUTHOR_URL`, authenticated via `global-setup.js`'s `storageState`). No spec hardcodes a
  host or credential; all read tier context implicitly through the project's `baseURL`, matching
  the env vars `ui.tests/pom.xml`'s `ui-tests-docker-execution` profile already injects
  (`AEM_AUTHOR_URL`, `AEM_AUTHOR_USERNAME`, `AEM_AUTHOR_PASSWORD`, `AEM_PUBLISH_URL`,
  `AEM_PUBLISH_USERNAME`, `AEM_PUBLISH_PASSWORD`, `REPORTS_PATH`).
- **Specs authored** — one per `design/ui-test-scenarios.md` scenario ID, tagged via a
  `test.describe('UI-0NN', ...)` wrapper so the ID is greppable in both source and
  `--list`/JUnit output:
  - `tests/publish/ui-001-hero-desktop.spec.js` .. `ui-016-keyboard-focus.spec.js` (16 files,
    UI-001..UI-016, Publish tier).
  - `tests/author/ui-017-site-header-dialog-roundtrip.spec.js`,
    `ui-018-site-footer-dialog-roundtrip.spec.js` (Author tier). These author-dialog-roundtrip
    specs implement the full open-dialog/edit-field/save/verify flow using generic Granite
    UI selectors (`[data-action="CONFIGURE"]`, `.cq-dialog`); a code comment flags that Sentinel
    may need to adjust the exact selectors against the live author instance's AEM version — this
    agent cannot validate them against a live authoring UI pre-deploy.
  - `tests/support/routes.js` — shared route constants (landing page + both master-XF
    edit/render URLs) to avoid duplicating literals across specs.
- **Static validation (no live env, no `mvn`):**
  - `npm install --no-audit --no-fund` → exit 0, `package-lock.json` regenerated (105 packages).
  - `npx playwright test --list` → exit 0, **38 tests discovered across 18 files** (3 projects ×
    16 publish specs + 1 author project × 2 author specs; UI-005/UI-015 each parameterize 2
    viewports as sub-tests within their single scenario ID).
  - `npx eslint .` → exit 0 after adding `browser: true` to `.eslintrc.js`'s `env` (specs pass
    callbacks into `page.evaluate()` that reference `document`/`getComputedStyle` in the browser
    context; `node: true` alone doesn't cover that).
- **Scenario coverage:** 18/18 scenario IDs (UI-001..UI-018) have a spec; `unmapped: []`.
- **No execution performed** — this track only proves the specs exist, parse, and are
  discoverable. Sentinel executes them post-deploy against the real dual-tier environment.

## Best-practices re-scan

Ran the `best-practices` skill's manual pattern-hint checklist against all 6 new Java files
(`SiteHeaderModel`, `SiteFooterModel`, `UtilityLink`, `FooterColumn`, `FooterLink`, `SocialLink`):
no `loginAdministrative`/`getAdministrativeResourceResolver`, no JCR observation
(`javax.jcr.observation.*`), no OSGi Event Admin, no `Replicator`/deprecated replication API, no
`AssetManager`, no `System.out`/`printStackTrace`, all six `@Model` annotations use the array-form
`adaptables`. HTL `data-sly-test` usage in both new components is against dynamic
`model`/`link`/`column`/`social` expressions only — no redundant constant comparisons.

## Open items / follow-ups

- Re-run the B8 runtime smoke render once Auditron has installed `ui.apps`/`ui.content` to the
  local SDK (curl the seeded landing page for `.cmp-teaser--hero`, `.cmp-container--card-grid`,
  `.cmp-site-header`, `.cmp-site-footer` class presence).
- Sentinel should double-check the Granite dialog selectors in `ui-017`/`ui-018` against the
  actual AEM version's authoring UI once a live author instance is available; adjust selectors
  only, not the assertions.
- Composer's XF-content-authoring dependency (replacing the master XFs' legacy
  Navigation+LanguageNavigation+Search / Separator+Text content with single `site-header`/
  `site-footer` instances) is unblocked now that both components exist on disk and Configsmith's
  XF Root policy amendment has landed.
