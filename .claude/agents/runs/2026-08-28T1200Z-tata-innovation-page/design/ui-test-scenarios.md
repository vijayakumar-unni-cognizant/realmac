# UI Test Scenarios — Tata Innovation Landing Page

```ids: prefix=UI count=18 UI-001..UI-018 (no gaps)
```

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Framework-neutral scenario specs.** Blockwright authors these as `tests/*.spec.js` (Playwright)
  pre-deploy — one spec per ID — so Cloud Manager's Custom UI Testing runs Playwright on the first
  pipeline execution. Sentinel executes them post-deploy against the real environment (deferred this
  run per `technical-specifications.md §7`/`§9`) and reports per-ID coverage.
- **Tier:** every scenario below is a public render/user-journey scenario and runs against the
  **Publish** tier, except UI-017/UI-018 which are authoring-surface journeys and run against the
  **Author** tier.
- **Route:** `/content/realmac/us/en/innovation.html` unless noted otherwise.

---

## UI-001 — Hero renders full-bleed with overlaid title (desktop)

- **Tier:** Publish. **Viewport:** 1440×900.
- **User actions:** load the route.
- **DOM assertions:** `.cmp-teaser--hero` exists and has computed `height: 480px`; exactly one
  `<h1>` on the page with text `"Innovation"`, inside `.cmp-teaser--hero .cmp-teaser__title`, computed
  `color: rgb(255, 255, 255)`.
- **Accessibility assertions:** hero image has non-empty `alt`; contrast of title text against the
  scrim background computed to be ≥4.5:1.
- **Visual-fidelity notes:** matches `component-specifications.md § B.1` Pixel-Verified table at 1440px.

## UI-002 — Hero title scales down at tablet

- **Tier:** Publish. **Viewport:** 900×1200 (tablet band, 768–1199px).
- **DOM assertions:** `.cmp-teaser--hero` computed `height: 360px`; `.cmp-teaser__title` computed
  `font-size: 36px`.

## UI-003 — Hero title scales down at mobile

- **Tier:** Publish. **Viewport:** 390×844.
- **DOM assertions:** `.cmp-teaser--hero` computed `height: 280px`; `.cmp-teaser__title` computed
  `font-size: 28px`; content padding computed `0px 24px 24px 24px`.

## UI-004 — Hero has no CTA button

- **Tier:** Publish. **Viewport:** 1440×900.
- **DOM assertions:** `.cmp-teaser--hero .cmp-teaser__action-link` either does not exist in the DOM or
  is not visible (`display: none` / zero bounding box).

## UI-005 — Intro renders centered column with distinct lead/body typography

- **Tier:** Publish. **Viewport:** 1440×900 and 390×844.
- **DOM assertions:** `.cmp-text--intro-lead` computed `max-width: 840px` (desktop); first `<p>`
  computed `font-size: 21px`; second-and-later `<p>` computed `font-size: 16px` — asserted at both
  viewports.
- **Accessibility assertions:** text color `rgb(51,51,51)` against `#ffffff` background — contrast
  ≥4.5:1 (WCAG AA for normal text at these sizes).

## UI-006 — Card grid renders 2 columns at desktop

- **Tier:** Publish. **Viewport:** 1440×900.
- **DOM assertions:** `.cmp-container--card-grid > .cmp-container > .aem-Grid` has exactly 4 grid-item
  children (`.aem-GridColumn`); computed `grid-template-columns` resolves to 2 equal-width tracks.

## UI-007 — Card grid renders 2 columns at tablet

- **Tier:** Publish. **Viewport:** 900×1200.
- **DOM assertions:** same grid selector as UI-006 resolves to 2 equal-width tracks (reference:
  "Tablet: 2 columns").

## UI-008 — Card grid collapses to 1 column at mobile

- **Tier:** Publish. **Viewport:** 390×844.
- **DOM assertions:** same grid selector resolves to a single track; the 4 cards stack vertically
  (each card's bounding box top ≠ any sibling's top).

## UI-009 — Each card's arrow link navigates to its target

- **Tier:** Publish. **Viewport:** 1440×900.
- **User actions:** click `.cmp-teaser--innovation-card:nth-of-type(1) .cmp-teaser__action-link`.
- **DOM assertions:** resulting navigation URL matches the authored `linkURL` for that card.
- **Accessibility assertions:** the link has a discernible accessible name (link text, not icon-only).

## UI-010 — Header renders logo + nav + utility icons in one row (desktop)

- **Tier:** Publish. **Viewport:** 1440×900.
- **DOM assertions:** `.cmp-site-header__inner` computed `display: flex; justify-content: space-between`;
  contains, in DOM order, `.cmp-site-header__logo`, `.cmp-site-header__nav`,
  `.cmp-site-header__utility-links`.
- **Accessibility assertions:** page has exactly one `banner` landmark (`<header>`); each utility link
  has a non-empty `aria-label`.

## UI-011 — Header collapses to a mobile menu below 768px

- **Tier:** Publish. **Viewport:** 390×844.
- **User actions:** click `.cmp-site-header__menu-toggle`.
- **DOM assertions:** before click, `.cmp-site-header__nav` computed `display: none` and
  `.cmp-site-header__menu-toggle[aria-expanded="false"]`; after click, nav becomes visible and
  `aria-expanded="true"`.
- **Accessibility assertions:** `aria-controls` on the toggle references the nav element's `id`;
  toggle is reachable and operable via keyboard (`Enter`/`Space`).

## UI-012 — Footer renders columns + social row + legal bar on dark background

- **Tier:** Publish. **Viewport:** 1440×900.
- **DOM assertions:** `.cmp-site-footer` computed `background-color: rgb(26, 26, 26)`;
  `.cmp-site-footer__columns`, `.cmp-site-footer__social`, `.cmp-site-footer__legal-text` all present
  and non-empty.
- **Accessibility assertions:** exactly one `contentinfo` landmark (`<footer>`); each social link has
  a non-empty `aria-label`; text/background contrast ≥4.5:1 for column headings and links.

## UI-013 — Footer columns stack at mobile

- **Tier:** Publish. **Viewport:** 390×844.
- **DOM assertions:** `.cmp-site-footer__columns` computed `grid-template-columns` resolves to a
  single track; `.cmp-site-footer__social` computed `justify-content: center`.

## UI-014 — Semantic landmarks present exactly once each

- **Tier:** Publish. **Viewport:** 1440×900.
- **DOM assertions:** exactly one `<header>` (banner), one `<main>` (the Page-Main landmark
  container), one `<footer>` (contentinfo) on the rendered page.

## UI-015 — Full-page accessibility scan has zero critical violations

- **Tier:** Publish. **Viewport:** 1440×900 and 390×844.
- **Accessibility assertions:** run an axe-core (`@axe-core/playwright`) scan against the full
  rendered page at both viewports; zero `critical`/`serious` violations (WCAG 2.1 AA ruleset).
- **Cross-browser notes:** run at minimum on Chromium and WebKit engines (Playwright's built-in
  browser projects) to catch engine-specific ARIA/contrast computation differences.

## UI-016 — Keyboard focus order and visible focus rings

- **Tier:** Publish. **Viewport:** 1440×900.
- **User actions:** press `Tab` repeatedly from page load.
- **DOM assertions:** focus order is: header logo → header nav links → header utility links → (page
  content — hero has no focusable elements since actions are disabled) → card action links (in DOM
  order) → footer links → footer social links.
- **Accessibility assertions:** every focused element has a visible focus indicator (outline or
  equivalent, not `outline: none` without a replacement).

## UI-017 — `site-header` dialog roundtrip persists authored values

- **Tier:** Author. **Route:** the header master XF's edit view
  (`/content/experience-fragments/realmac/us/en/site/header/master.html`, author instance).
- **User actions:** open the `site-header` component's dialog; change the Logo Alt Text field; save.
- **DOM assertions:** after save, the rendered `<img class="cmp-site-header__logo-image">` `alt`
  attribute reflects the new value.

## UI-018 — `site-footer` dialog roundtrip persists authored values

- **Tier:** Author. **Route:** the footer master XF's edit view
  (`/content/experience-fragments/realmac/us/en/site/footer/master.html`, author instance).
- **User actions:** open the `site-footer` component's dialog; edit the Legal Text field; save.
- **DOM assertions:** after save, `.cmp-site-footer__legal-text` text content reflects the new value.

---

## Coverage summary (visual / user-journey requirements → UI-### IDs)

| Requirement | UI-### IDs |
|---|---|
| US-001 (hero) | UI-001, UI-002, UI-003, UI-004 |
| US-002 (intro) | UI-005 |
| US-003 (card grid) | UI-006, UI-007, UI-008, UI-009 |
| US-004 (header) | UI-010, UI-011 |
| US-005 (footer) | UI-012, UI-013 |
| US-006 / NFR accessibility (landmarks, a11y, keyboard) | UI-014, UI-015, UI-016 |
| US-009 (dialog roundtrip — authoring surfaces this run creates) | UI-017, UI-018 |

Every visual/user-journey requirement has a corresponding `UI-###` scenario; no requirement is
deferred to another artifact without an explicit ID enumeration.
