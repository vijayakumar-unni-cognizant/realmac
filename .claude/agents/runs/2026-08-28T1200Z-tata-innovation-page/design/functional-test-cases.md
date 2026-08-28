# Functional Test Cases — Tata Innovation Landing Page

```ids: prefix=TC count=46 TC-001..TC-046 (no gaps)
```

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- Every case below traces to a `US-###` acceptance criterion in `plan/requirements.yaml`.
- **Executor bias:** `auditron` discharges everything settleable pre-deploy against the local AEM SDK
  install that Auditron's own Build Validation Gate produces (structural DOM/class checks via a local
  curl of the installed page, unit tests, static policy/build checks). `sentinel` is reserved for
  cases that need the **real** deployed/CDN-fronted environment — none of the functional cases below
  require that (NFR execution — LCP/INP/CLS/a11y-scan/visual-diff — is tracked separately in
  `technical-specifications.md §7` and owned by Sentinel, deferred this run). Every case here is
  `executor: auditron`.

---

## US-001 — Hero banner

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-001 | Hero renders full-bleed with the correct DAM image | Sample page authored, hero teaser instance present | `fileReference=/content/dam/realmac/tata-innovation/about_innovation_banner_desktop_1920x1080.jpg` | Deploy to local SDK; curl the rendered page; inspect `.cmp-teaser--hero .cmp-teaser__image img` | `src`/`srcset` resolves to the seeded asset path; `.cmp-teaser--hero` computed `height` = `480px` at desktop viewport (per `component-specifications.md § B.1` acceptance table) | US-001 | auditron |
| TC-002 | Hero title renders as a single H1 in white text over the scrim | Same as TC-001 | `jcr:title="Innovation"` | Curl rendered page; find `.cmp-teaser--hero .cmp-teaser__title` | Exactly one `<h1>` on the page and it is this element; computed `color` = `rgb(255,255,255)`; scrim gradient present per token audit (`rgba(0,0,0,0.25)`→`rgba(0,0,0,0.45)`) | US-001 | auditron |
| TC-003 | No CTA renders in the hero even if an action is authored | Author an action on the hero teaser instance in a test fixture | action link+label set | Curl rendered page | `.cmp-teaser--hero .cmp-teaser__action-link` is absent or `display:none` (policy `actionsDisabled=true`) | US-001 | auditron |
| TC-004 | Hero image uses Core Image v3 responsive `srcset` with explicit dimensions | Same as TC-001 | — | Inspect rendered `<img>`/`<picture>` markup | `srcset` present with multiple widths; explicit `width`/`height` attributes present (CLS mitigation) | US-001 | auditron |
| TC-005 | Empty state: hero teaser has no image authored | Fixture page with hero teaser, no `fileReference` | — | Render in author mode | Author-mode "Please configure…" placeholder shown; no server error / NPE in the request log | US-001 | auditron |

## US-002 — Intro/overview text

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-006 | Intro renders as a single centered column ≤900px | Sample page has intro Text instance with `cmp-text--intro-lead` variant | Multi-paragraph copy | Curl rendered page; inspect `.cmp-text--intro-lead` | Computed `max-width=840px`, `margin:0 auto` | US-002 | auditron |
| TC-007 | Lead paragraph typography is visually distinct from body | Same as TC-006 | ≥2 paragraphs | Inspect `.cmp-text--intro-lead p:first-of-type` vs `.cmp-text--intro-lead p` | First paragraph computed `font-size=21px`; subsequent `font-size=16px` | US-002 | auditron |
| TC-008 | Intro is authored via the reused Core Text proxy, not a new component | — | — | Inspect the component's `sling:resourceType` in the authored JCR content and its `.content.xml` on disk | `sling:resourceType="realmac/components/text"`; no new component folder exists for "intro" | US-002 | auditron |
| TC-009 | Empty state: Text instance authored with no rich-text content | Fixture with empty `text` property | — | Render page | `.cmp-text--intro-lead` renders no `<p>` children; no broken markup; author-mode placeholder shown | US-002 | auditron |

## US-003 — Showcase card grid

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-010 | 4 cards render in a 2-column grid at desktop (1440px) | Sample page has card-grid container with 4 card teasers | 4 card instances | Curl rendered page at simulated 1440px CSS context; inspect `.cmp-container--card-grid > .cmp-container > .aem-Grid` | Computed `grid-template-columns` = two equal tracks (`repeat(2, 1fr)`) | US-003 | auditron |
| TC-011 | Grid collapses to 1 column below 768px | Same as TC-010 | — | Inspect same selector at simulated 390px CSS context | Computed `grid-template-columns` = one track (`1fr`) | US-003 | auditron |
| TC-012 | Each card renders image (top) + title + arrow-style link | Same as TC-010 | Card image, title, one action per card | Inspect each `.cmp-teaser--innovation-card` | `.cmp-teaser__image` has `order:-1`; `.cmp-teaser__title` present; `.cmp-teaser__action-link` present with no `.cmp-button`/filled-button class | US-003 | auditron |
| TC-013 | Card-grid container only allows Teaser children (least privilege) | — | Attempt (fixture) to author a Text component inside the card-grid container | Check policy resolution for that path | `policy_landing_card_grid.components=[realmac/components/teaser]` — a Text component dropped there is rejected/not offered in the component browser | US-003 | auditron |
| TC-014 | Empty state: card-grid container authored with 0 cards | Fixture container, no children | — | Render page | `.aem-Grid` renders with no grid-item children; no broken/empty-cell layout artifacts | US-003 | auditron |
| TC-015 | Card gap, radius, and surface match `design-token-audit.md` | Same as TC-010 | — | Inspect `.cmp-teaser--innovation-card` and grid gap | `border-radius=6px`; `border=1px solid rgb(229,229,229)`; grid `gap=24px` | US-003 | auditron |

## US-004 — Header

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-016 | Header renders via the Experience Fragment referenced by the template structure | `landing-page` structure has `experiencefragment-header` node | Header master XF authored with `site-header` | Curl rendered page | `<header class="cmp-site-header">` present in the rendered `<body>`, sourced from the XF include | US-004 | auditron |
| TC-017 | Logo, primary nav, and utility icons render in one horizontal row | Same as TC-016 | Logo, 1 nav root, 2 utility links (search, contact) | Inspect `.cmp-site-header__inner` | Computed `display:flex; justify-content:space-between`; logo `<img>`, `<nav>`, and utility `<ul>` all present as siblings | US-004 | auditron |
| TC-018 | Header collapses to a mobile menu below 768px | Same as TC-016 | — | Inspect at 390px CSS context | `.cmp-site-header__nav` computed `display:none`; `.cmp-site-header__menu-toggle` computed `display:flex` | US-004 | auditron |
| TC-019 | Utility icon-only links carry an accessibility label | Same as TC-016 | `ariaLabel="Search"`, `ariaLabel="Contact us"` | Inspect `.cmp-site-header__utility-link[aria-label]` | Each utility link has a non-empty `aria-label` matching authored value | US-004 | auditron |
| TC-020 | Empty state: 0 utility links authored | Fixture `site-header` with no `utilityLinks` | — | Render page | `.cmp-site-header__utility-links` renders as an empty `<ul>`; no broken markup, no NPE | US-004 | auditron |

## US-005 — Footer

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-021 | Footer renders via the Experience Fragment referenced by the template structure | `landing-page` structure has `experiencefragment-footer` node | Footer master XF authored with `site-footer` | Curl rendered page | `<footer class="cmp-site-footer">` present, sourced from the XF include | US-005 | auditron |
| TC-022 | Multi-column links + social icons + copyright render on a dark background | Same as TC-021 | 4 columns, 4 social links, legal text | Inspect `.cmp-site-footer` | `background-color=rgb(26,26,26)`; `.cmp-site-footer__columns`, `__social`, `__legal-text` all present | US-005 | auditron |
| TC-023 | Footer is authored as ONE cohesive component, not fragmented Text blocks | — | — | Inspect authored JCR content under the footer master XF's `root` node | Exactly one child node with `sling:resourceType="realmac/components/site-footer"`; no sibling `Text`/`Separator` chrome nodes remain | US-005 | auditron |
| TC-024 | Footer copyright text is neutral realmac copy, not Tata's | Same as TC-021 | `legalText="© 2026 Realmac. All rights reserved."` | Inspect `.cmp-site-footer__legal-text` text content | Text does not contain "Tata Sons" or any Tata-owned copyright string | US-005 | auditron |
| TC-025 | Empty state: 0 columns and 0 social links authored | Fixture `site-footer` with empty `columns`/`socialLinks` | — | Render page | `.cmp-site-footer__columns` and `.cmp-site-footer__social` render empty; legal bar still renders (independent of columns/social) | US-005 | auditron |
| TC-026 | Footer light-on-dark contrast ≥4.5:1 | Same as TC-021 | — | Compute contrast ratio of `#ffffff`/`#cccccc` text against `#1a1a1a` background (token values from `design-token-audit.md`) | Both ratios ≥4.5:1 (WCAG 2.1 AA) | US-005 | auditron |

## US-006 — Editable template

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-027 | `landing-page` is selectable at `/content/realmac/us/en` | Template deployed, `cq:allowedTemplates` verified | — | Open "Create Page" wizard at `/content/realmac/us/en` on local SDK | `landing-page` (title "Innovation Landing Page") appears in the template list | US-006 | auditron |
| TC-028 | Structure references header/footer EFs and the project container proxy | Template deployed | — | Inspect `structure/jcr:content` on disk / in CRXDE | `experiencefragment-header`/`-footer` nodes present with correct `fragmentVariationPath`; all container nodes use `realmac/components/container` (never `wcm/foundation/components/responsivegrid`) | US-006 | auditron |
| TC-029 | Every editable container/component has a resolving content policy | Template + policies deployed | — | Walk `policies/jcr:content` mapping tree against `policy-mapping.md § 1`; resolve each `cq:policy` path | Every `cq:policy` value resolves to an existing policy node (no dangling reference) | US-006 | auditron |
| TC-030 | Page policy has a `clientlibs` mapping | Policies deployed | — | Inspect `realmac/components/page/policy` | `clientlibs=[realmac.dependencies,realmac.site]` present | US-006 | auditron |
| TC-031 | Exactly one `<h1>` renders on the page (no structural Title node) | Sample page deployed | — | Curl rendered page; count `<h1>` elements | Count = 1 (the hero teaser's title); `structure/` has no `title` node | US-006 | auditron |

## US-007 — Sample authored page

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-032 | Sample page authored at the correct path with all intermediate segments as `cq:Page` | Composer has run | `/content/realmac/us/en/innovation` | Inspect JCR node types for `us`, `en`, `innovation` | All three are `cq:Page` (none is `nt:folder`) | US-007 | auditron |
| TC-033 | All 13 supplied assets seeded with real binaries + original renditions | Composer has run | 13 files under `/content/dam/realmac/tata-innovation/` | Inspect each `dam:Asset` node for an `original` rendition with a non-zero-length binary | Every seeded asset has a binary; no `dam:Asset` node lacks a rendition (C11) | US-007 | auditron |
| TC-034 | Page renders hero + intro + 4 cards + header/footer chrome without "Please configure" placeholders | Full page authored | — | Curl rendered page in publish-like (non-author) mode | Zero occurrences of "Please configure" in the response body | US-007 | auditron |

## US-008 — SCSS partials

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-035 | SCSS partials exist for hero, card-grid, card, intro, header, footer | Blockwright has run | — | Inspect `ui.frontend/src/main/webpack/**/*.scss` | Rules exist for `.cmp-teaser--hero`, `.cmp-container--card-grid`, `.cmp-teaser--innovation-card`, `.cmp-text--intro-lead`, `.cmp-site-header`, `.cmp-site-footer` | US-008 | auditron |
| TC-036 | Selectors are BEM/element-based against Core Component output classes; no DOM restructuring | SCSS authored | — | Grep SCSS for selectors targeting non-Core-Component-output class names or requiring HTL structural changes | All selectors match classes already present in `component-specifications.md`'s documented DOM (no invented wrapper elements) | US-008 | auditron |
| TC-037 | Breakpoints align with template `cq:responsive` (768/1200) and the token audit | SCSS authored | — | Grep SCSS media queries | Media queries use `767px`/`1199px`/`1200px` boundaries consistent with `design-token-audit.md § Breakpoints` — no stray/invented breakpoint values | US-008 | auditron |

## US-009 — Sling Models + unit tests

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-038 | `SiteHeaderModel` adapts from both `Resource` and `SlingHttpServletRequest` | wcm.io AEM Mocks context | Mock resource with `logoFileReference`, `logoAlt` | `context.request().adaptTo(SiteHeaderModel.class)` and `context.currentResource().adaptTo(SiteHeaderModel.class)` | Both adaptations succeed and return non-null (validates D1 array-form `adaptables`) | US-009 | auditron |
| TC-039 | `SiteHeaderModel.getUtilityLinks()` correctly populates from the composite multifield | Mock resource with 2 `utilityLinks` child resources | `label`, `iconFileReference`, `linkURL`, `ariaLabel` per item | Adapt model; call `getUtilityLinks()` | Returns a `List<UtilityLink>` of size 2 with correct field values in order | US-009 | auditron |
| TC-040 | Empty `utilityLinks` returns an empty list, not `null` | Mock resource with no `utilityLinks` child | — | Adapt model; call `getUtilityLinks()` | Returns `Collections.emptyList()`, never `null` | US-009 | auditron |
| TC-041 | `SiteFooterModel.getColumns()` populates nested `FooterColumn`/`FooterLink` | Mock resource with 2 columns, each with 2 links | heading + link label/url per item | Adapt model; call `getColumns()` | Returns 2 `FooterColumn`s, each with 2 `FooterLink`s with correct values | US-009 | auditron |
| TC-042 | `SiteFooterModel.getSocialLinks()` populates from the social multifield | Mock resource with 3 `socialLinks` | icon/url/label per item | Adapt model; call `getSocialLinks()` | Returns 3 `SocialLink`s with correct values | US-009 | auditron |
| TC-043 | `SiteFooterModel.hasContent()` is `false` when `legalText` is absent | Mock resource with no `legalText` | — | Adapt model; call `hasContent()` | Returns `false` | US-009 | auditron |
| TC-044 | `mvn clean install` is green including the new unit tests | Full repo | — | Run `mvn clean install` | Build succeeds; `SiteHeaderModelTest`/`SiteFooterModelTest` (and item child-model tests) pass | US-009 | auditron |
| TC-045 | No deprecated AEM API used in the new models | New model source | — | Run `best-practices` skill static checks against `SiteHeaderModel`/`SiteFooterModel`/child models | Zero deprecated-API findings | US-009 | auditron |

## US-010 — Playwright specs authored (execution deferred)

| ID | Description | Preconditions | Test data | Steps | Expected result | Requirement | Executor |
|---|---|---|---|---|---|---|---|
| TC-046 | Playwright spec files exist under `ui.tests` for every `UI-###` scenario in `ui-test-scenarios.md`, authored (not executed) this run | Blockwright has authored specs from `ui-test-scenarios.md` | — | Inspect `ui.tests/**/*.spec.js` (or project's configured Playwright test glob) | One spec file (or one `test(...)` block) exists per `UI-###` ID; specs are present in the repo but their **execution** is deferred to Sentinel against the real environment (per `technical-specifications.md §7`/`§9`) | US-010 | auditron |

---

## Traceability summary

| Requirement | Test case IDs |
|---|---|
| US-001 | TC-001, TC-002, TC-003, TC-004, TC-005 |
| US-002 | TC-006, TC-007, TC-008, TC-009 |
| US-003 | TC-010, TC-011, TC-012, TC-013, TC-014, TC-015 |
| US-004 | TC-016, TC-017, TC-018, TC-019, TC-020 |
| US-005 | TC-021, TC-022, TC-023, TC-024, TC-025, TC-026 |
| US-006 | TC-027, TC-028, TC-029, TC-030, TC-031 |
| US-007 | TC-032, TC-033, TC-034 |
| US-008 | TC-035, TC-036, TC-037 |
| US-009 | TC-038, TC-039, TC-040, TC-041, TC-042, TC-043, TC-044, TC-045 |
| US-010 | TC-046 |

Every requirement ID (US-001..US-010) maps to ≥1 test case. No requirement is deferred to another
artifact with a bare "covered by" note — every ID above is enumerated against its owning requirement.
