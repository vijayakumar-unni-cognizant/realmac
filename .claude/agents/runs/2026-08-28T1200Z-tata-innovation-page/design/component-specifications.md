# Component Specifications — Tata Innovation Landing Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Scope:** 2 net-new chrome components (`site-header`, `site-footer`) + 4 Style System variants on
  existing `realmac` Core Component proxies. No other components are in scope. No custom HTL/dialog
  is proposed on top of any Core Component proxy for the 4 reuse items (D12).
- Every value in the Pixel-Verified Acceptance Criteria tables is sourced from
  `design/design-token-audit.md` and cites the `reference-deconstruction.md` region it pins.

---

## Part A — Net-new components

### A.1 `realmac/components/site-header`

| Field | Value |
|---|---|
| Classification | (C) new component — chrome, S8 |
| Component group | `Realmac - Structure` |
| `sling:resourceSuperType` | none (standalone custom component; does not extend a Core Component) |
| Java package | `com.realmac.aem.core.models` |
| Sling Model class | `SiteHeaderModel` (+ child model `UtilityLink`) |
| Reused sub-component | `realmac/components/navigation` (Core Navigation v2 proxy), embedded via `data-sly-resource` with a **synthetic resource** (not an authorable child node — no policy resolution required for it; `navigationRoot`/`structureDepth` passed as explicit resource properties, which the Core Navigation Sling Model reads via `@ValueMapValue`, taking precedence over any `currentStyle` policy fallback) |

**Sling Model — `SiteHeaderModel`:**

```java
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SiteHeaderModel {
    // fields per accessor table below
}
```

> Per D1: adaptables declared in **array form**. Do not use the single-class form — it breaks
> `resource.adaptTo(SiteHeaderModel.class)` in wcm.io AEM Mocks unit tests.

**Accessors:**

| Accessor | Java type | Source | Notes |
|---|---|---|---|
| `getLogoFileReference()` | `String` | `@ValueMapValue(name="logoFileReference")` | DAM asset path, required |
| `getLogoAlt()` | `String` | `@ValueMapValue` | Required |
| `getLogoLinkURL()` | `String` | `@ValueMapValue` `@Default(values="/content/realmac/us/en.html")` | Optional, defaults to site root |
| `getNavigationRoot()` | `String` | `@ValueMapValue` `@Default(values="/content/realmac/us/en")` | Passed to embedded Navigation |
| `getNavigationStructureDepth()` | `int` | `@ValueMapValue` `@Default(intValues=1)` | Passed to embedded Navigation |
| `getUtilityLinks()` | `List<UtilityLink>` | `@ChildResource(name="utilityLinks")` → `List<Resource>` processed in `@PostConstruct`, each adapted to `UtilityLink` | Composite multifield; 0..n entries; empty list tolerated (US-004 does not require a minimum) |
| `hasContent()` | `boolean` | derived | `logoFileReference != null` |

**Child model — `UtilityLink`:**

| Accessor | Java type | Source |
|---|---|---|
| `getLabel()` | `String` | `@ValueMapValue` |
| `getIconFileReference()` | `String` | `@ValueMapValue` |
| `getLinkURL()` | `String` | `@ValueMapValue` |
| `getAriaLabel()` | `String` | `@ValueMapValue` |
| `hasContent()` | `boolean` | `label != null && linkURL != null` |

**HTL semantic structure (root element `<header>` — permitted per D10 corollary; custom components
are free to use semantic HTML, unlike Core XF v2's hardcoded `<div>` wrapper):**

```
<header class="cmp-site-header" data-sly-use.model="...SiteHeaderModel">
  <div class="cmp-site-header__inner">
    <a class="cmp-site-header__logo" href="${model.logoLinkURL}">
      <img class="cmp-site-header__logo-image" src="${model.logoFileReference}" alt="${model.logoAlt}"/>
    </a>
    <button type="button" class="cmp-site-header__menu-toggle" aria-expanded="false"
            aria-controls="site-header-nav" aria-label="Open menu">…</button>
    <nav class="cmp-site-header__nav" id="site-header-nav" aria-label="Primary">
      <div data-sly-resource="${'navigation' @ resourceType='realmac/components/navigation',
                                 navigationRoot=model.navigationRoot,
                                 structureDepth=model.navigationStructureDepth}"></div>
    </nav>
    <ul class="cmp-site-header__utility-links" data-sly-list.link="${model.utilityLinks}">
      <li class="cmp-site-header__utility-item">
        <a class="cmp-site-header__utility-link" href="${link.linkURL}" aria-label="${link.ariaLabel}">
          <img class="cmp-site-header__utility-icon" src="${link.iconFileReference}" alt=""/>
        </a>
      </li>
    </ul>
  </div>
</header>
```

**BEM class names:** `cmp-site-header`, `cmp-site-header__inner`, `cmp-site-header__logo`,
`cmp-site-header__logo-image`, `cmp-site-header__menu-toggle`, `cmp-site-header__nav`,
`cmp-site-header__utility-links`, `cmp-site-header__utility-item`, `cmp-site-header__utility-link`,
`cmp-site-header__utility-icon`.

**Accessibility expectations:**
- `<header>` implies the `banner` landmark — no explicit `role` needed.
- Logo image `alt` is authored (non-empty), not decorative.
- Menu toggle: `aria-expanded` reflects open/closed state (JS-managed); `aria-controls` points at the nav id; `aria-label="Open menu"`/`"Close menu"` toggled by the same minimal JS (per NFR INP mitigation — no heavy hydration).
- Utility icon links carry `aria-label` from the authored `ariaLabel` field (icon-only links, R1); icon `<img alt="">` is decorative since the label lives on the parent `<a>`.
- Keyboard focus: visible focus ring on logo link, nav links (inherited from Core Navigation), menu toggle, and utility links.

**Edge cases:**
- `utilityLinks` empty → utility list renders as an empty `<ul>` with no `<li>` (no broken markup; see `authoring-test-cases.md` AUTH-011).
- `logoFileReference` missing → `hasContent()` false; HTL author-mode placeholder ("Please configure Site Header"), no runtime NPE.
- `navigationRoot` missing → falls back to `@Default` `/content/realmac/us/en`.

---

### A.2 `realmac/components/site-footer`

| Field | Value |
|---|---|
| Classification | (C) new component — chrome, S8 |
| Component group | `Realmac - Structure` |
| `sling:resourceSuperType` | none |
| Java package | `com.realmac.aem.core.models` |
| Sling Model class | `SiteFooterModel` (+ child models `FooterColumn`, `FooterLink`, `SocialLink`) |

**Sling Model — `SiteFooterModel`:**

```java
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SiteFooterModel {
    // fields per accessor table below
}
```

> Per D1: array-form adaptables — same rationale as A.1.

**Accessors:**

| Accessor | Java type | Source | Notes |
|---|---|---|---|
| `getFooterLogoFileReference()` | `String` | `@ValueMapValue` | Optional (R5: "Optional footer logo") |
| `getColumns()` | `List<FooterColumn>` | `@ChildResource(name="columns")` processed in `@PostConstruct` | Composite multifield of composite multifields; 0..n columns |
| `getSocialLinks()` | `List<SocialLink>` | `@ChildResource(name="socialLinks")` processed in `@PostConstruct` | Composite multifield; 0..n |
| `getLegalText()` | `String` | `@ValueMapValue` | Required — neutral realmac copyright line, not Tata's (per S9.a) |
| `hasContent()` | `boolean` | derived | `legalText != null` |

**Child model — `FooterColumn`:**

| Accessor | Java type | Source |
|---|---|---|
| `getHeading()` | `String` | `@ValueMapValue` |
| `getLinks()` | `List<FooterLink>` | `@ChildResource(name="links")` processed in `@PostConstruct` |
| `hasContent()` | `boolean` | `heading != null` |

**Child model — `FooterLink`:**

| Accessor | Java type | Source |
|---|---|---|
| `getLabel()` | `String` | `@ValueMapValue` |
| `getUrl()` | `String` | `@ValueMapValue` |
| `hasContent()` | `boolean` | `label != null && url != null` |

**Child model — `SocialLink`:**

| Accessor | Java type | Source |
|---|---|---|
| `getIconFileReference()` | `String` | `@ValueMapValue` |
| `getUrl()` | `String` | `@ValueMapValue` |
| `getLabel()` | `String` | `@ValueMapValue` (used as `aria-label`, e.g. "Facebook") |
| `hasContent()` | `boolean` | `iconFileReference != null && url != null` |

**HTL semantic structure (root element `<footer>`):**

```
<footer class="cmp-site-footer" data-sly-use.model="...SiteFooterModel">
  <div class="cmp-site-footer__inner">
    <img data-sly-test="${model.footerLogoFileReference}" class="cmp-site-footer__logo"
         src="${model.footerLogoFileReference}" alt=""/>
    <div class="cmp-site-footer__columns">
      <nav class="cmp-site-footer__column" data-sly-list.column="${model.columns}"
           aria-label="${column.heading}">
        <h3 class="cmp-site-footer__column-heading">${column.heading}</h3>
        <ul class="cmp-site-footer__link-list">
          <li data-sly-list.link="${column.links}">
            <a class="cmp-site-footer__link" href="${link.url}">${link.label}</a>
          </li>
        </ul>
      </nav>
    </div>
    <ul class="cmp-site-footer__social" data-sly-list.social="${model.socialLinks}">
      <li class="cmp-site-footer__social-item">
        <a class="cmp-site-footer__social-link" href="${social.url}" aria-label="${social.label}">
          <img class="cmp-site-footer__social-icon" src="${social.iconFileReference}" alt=""/>
        </a>
      </li>
    </ul>
  </div>
  <div class="cmp-site-footer__legal">
    <p class="cmp-site-footer__legal-text">${model.legalText}</p>
  </div>
</footer>
```

**BEM class names:** `cmp-site-footer`, `cmp-site-footer__inner`, `cmp-site-footer__logo`,
`cmp-site-footer__columns`, `cmp-site-footer__column`, `cmp-site-footer__column-heading`,
`cmp-site-footer__link-list`, `cmp-site-footer__link`, `cmp-site-footer__social`,
`cmp-site-footer__social-item`, `cmp-site-footer__social-link`, `cmp-site-footer__social-icon`,
`cmp-site-footer__legal`, `cmp-site-footer__legal-text`.

**Accessibility expectations:**
- `<footer>` implies the `contentinfo` landmark.
- Each column is a `<nav>` with `aria-label` = its own heading (distinguishes multiple nav landmarks).
- Social icon-only links carry `aria-label` from the authored `label` field (R5, "aria-labels on icon-only links"); icon `<img alt="">` decorative.
- Footer light-on-dark contrast ≥4.5:1 — enforced via `design-token-audit.md § Colors` (`#ffffff`/`#cccccc` on `#1a1a1a`); see Pixel-Verified table below.
- Keyboard focus: visible focus ring on every footer link and social link.

**Edge cases:**
- `columns` empty → `.cmp-site-footer__columns` renders empty (no broken grid; social row and legal bar unaffected).
- `socialLinks` empty → `.cmp-site-footer__social` renders empty `<ul>`.
- `footerLogoFileReference` absent → logo `<img>` omitted entirely (`data-sly-test`), no broken image.
- `legalText` missing → `hasContent()` false; author-mode placeholder.

---

## Part B — Style System variants (no new component, no custom HTL/dialog on the proxy)

### B.1 Hero banner — `realmac/components/teaser` variant `cmp-teaser--hero`

- **Classification:** (A) Style System variant on Core Teaser v2 (via `realmac/components/teaser`
  proxy). No Sling Model, no HTL, no dialog changes — per D12/D13.
- **Field mapping (author → Core Teaser):** `jcr:title="Innovation"` → title (titleType pinned via
  policy, see `policy-mapping.md`), `fileReference` → hero image (via `imageDelegate=realmac/components/image`),
  `actionsDisabled=true` on the policy (no CTA per US-001).
- **Heading level:** this instance **owns the page `<h1>`** — see `template-design.md § Heading-level
  budget`. `titleType=h1` on `policy_landing_hero_teaser`.
- **Style System policy:** `realmac/components/teaser/policy_landing_hero_teaser` (new, landing-page-specific).
- **Style variant class:** `cmp-teaser--hero`.

**SCSS invariants** (Core Teaser v2 renders `.cmp-teaser__content` before `.cmp-teaser__image` in the
DOM — D7):

```scss
.cmp-teaser--hero {
    position: relative;
    overflow: hidden;
    display: flex;
    align-items: flex-end;
    height: 480px;                              // desktop — design-token-audit § Layout patterns

    @media (max-width: 1199px) { height: 360px; }  // tablet
    @media (max-width: 767px)  { height: 280px; }  // mobile

    .cmp-teaser__image {
        position: absolute;
        inset: 0;
        z-index: 1;

        img { width: 100%; height: 100%; object-fit: cover; }

        &::after {
            content: '';
            position: absolute;
            inset: 0;
            z-index: 2;
            background-image: linear-gradient(to bottom, rgba(0,0,0,0.25) 0%, rgba(0,0,0,0.45) 100%);
        }
    }

    .cmp-teaser__content {
        position: relative;
        z-index: 3;
        padding: 0 48px 48px;
        @media (max-width: 767px) { padding: 0 24px 24px; }
    }

    .cmp-teaser__title {
        color: #ffffff;
        font-weight: 700;
        font-size: 56px;
        @media (max-width: 1199px) { font-size: 36px; }
        @media (max-width: 767px)  { font-size: 28px; }
    }

    .cmp-teaser__action-container,
    .cmp-teaser__action-link { display: none; }   // actionsDisabled on policy; belt-and-suspenders
}
```

**Pixel-Verified Acceptance Criteria (viewport 1440px desktop / 390px mobile):**

| Selector | Property | Expected @ 1440 | Expected @ 390 | Region |
|---|---|---|---|---|
| `.cmp-teaser--hero` | `height` | `480px` | `280px` | R2 |
| `.cmp-teaser--hero` | `display` | `flex` | `flex` | R2 |
| `.cmp-teaser--hero` | `align-items` | `flex-end` | `flex-end` | R2 |
| `.cmp-teaser--hero .cmp-teaser__image` | `position` | `absolute` | `absolute` | R2 |
| `.cmp-teaser--hero .cmp-teaser__image img` | `object-fit` | `cover` | `cover` | R2 |
| `.cmp-teaser--hero .cmp-teaser__content` | `padding` | `0px 48px 48px 48px` | `0px 24px 24px 24px` | R2 |
| `.cmp-teaser--hero .cmp-teaser__title` | `font-size` | `56px` | `28px` | R2 |
| `.cmp-teaser--hero .cmp-teaser__title` | `color` | `rgb(255, 255, 255)` | `rgb(255, 255, 255)` | R2 |
| `.cmp-teaser--hero .cmp-teaser__title` | `font-weight` | `700` | `700` | R2 |
| `.cmp-teaser--hero .cmp-teaser__action-link` | `display` | `none` | `none` | R2 |

---

### B.2 Intro/overview text — `realmac/components/text` variant `cmp-text--intro-lead`

- **Classification:** (A) Style System variant on Core Text v2. No new component.
- **Authoring model:** **one** Core Text instance authored with the full intro copy (lead paragraph
  as the first `<p>`, followed by body paragraphs) — NOT two separate Text instances and NOT a new
  container variant (only 4 variants were approved at Human Checkpoint 1; an "intro container"
  variant was not among them). The `cmp-text--intro-lead` variant CSS both constrains the column width
  and differentiates the first paragraph's typography from subsequent paragraphs via `:first-of-type`.
  An **optional** secondary heading uses the existing, unmodified `realmac/components/title` (h2,
  reusing the project's existing "Content Title" policy — see `policy-mapping.md`) as a sibling
  instance in the same parsys — this is ordinary component reuse, not a new variant.
- **Style System policy:** `realmac/components/text/policy_landing_intro_text` (new, landing-page-specific,
  clones the project default `rtePlugins` config from `policy_641562756958017` and adds the variant).
- **Style variant class:** `cmp-text--intro-lead`.

**SCSS invariants:**

```scss
.cmp-text--intro-lead {
    max-width: 840px;
    margin: 0 auto;
    padding: 56px 0;
    @media (max-width: 767px) { padding: 32px 20px; }

    p {
        color: #333333;
        font-size: 16px;
        line-height: 1.6;
        margin-bottom: 20px;
    }

    p:first-of-type {
        font-size: 21px;
        font-weight: 400;
        line-height: 1.5;
    }
}
```

**Pixel-Verified Acceptance Criteria:**

| Selector | Property | Expected @ 1440 | Expected @ 390 | Region |
|---|---|---|---|---|
| `.cmp-text--intro-lead` | `max-width` | `840px` | `840px` (constrained by 100% viewport) | R3 |
| `.cmp-text--intro-lead` | `padding` | `56px 0px` | `32px 20px` | R3 |
| `.cmp-text--intro-lead p:first-of-type` | `font-size` | `21px` | `21px` | R3 |
| `.cmp-text--intro-lead p:first-of-type` | `color` | `rgb(51, 51, 51)` | `rgb(51, 51, 51)` | R3 |
| `.cmp-text--intro-lead p` | `font-size` | `16px` | `16px` | R3 |
| `.cmp-text--intro-lead p` | `line-height` | `25.6px` (1.6 × 16px) | `25.6px` | R3 |
| `.cmp-text--intro-lead p` | `margin-bottom` | `20px` | `20px` | R3 |

---

### B.3 Card grid — `realmac/components/container` variant `cmp-container--card-grid`

- **Classification:** (A) Style System variant on Core Container v1. No new component.
- **DOM contract (critical — see `create-editable-template/references/policies.md § CRITICAL`):**
  AEM applies the `cq:styleClasses` value to the container's **outer decoration wrapper**, not to the
  inner `.cmp-container`. The SCSS **must** reach down through `.cmp-container > .aem-Grid`.
- **Style System policy:** `realmac/components/container/policy_landing_card_grid` (new). This policy
  also sets `components="[realmac/components/teaser]"` — the card-grid container's own allowed-children
  list — see `policy-mapping.md`.
- **Style variant class:** `cmp-container--card-grid`.

**SCSS invariants:**

```scss
.cmp-container--card-grid > .cmp-container > .aem-Grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);   // desktop AND tablet — reference: "Tablet: 2 columns"
    gap: 24px;
    max-width: 1200px;
    margin: 0 auto;

    &::before, &::after { display: none; }  // neutralize .aem-Grid float-clearfix pseudos
    > .aem-GridColumn { width: auto; }        // reset aem-GridColumn--default--12 (width:100%)

    @media (max-width: 767px) {
        grid-template-columns: 1fr;
    }
}
```

**Pixel-Verified Acceptance Criteria:**

| Selector | Property | Expected @ 1440 | Expected @ 390 | Region |
|---|---|---|---|---|
| `.cmp-container--card-grid > .cmp-container > .aem-Grid` | `display` | `grid` | `grid` | R4 |
| `.cmp-container--card-grid > .cmp-container > .aem-Grid` | `grid-template-columns` | `repeat(2, 1fr)` (two equal tracks) | `1fr` (one track) | R4 |
| `.cmp-container--card-grid > .cmp-container > .aem-Grid` | `gap` | `24px` | `24px` | R4 |
| `.cmp-container--card-grid > .cmp-container > .aem-Grid` | `max-width` | `1200px` | `1200px` | R4 (cross-region) |
| `.cmp-container--card-grid > .cmp-container > .aem-Grid > .aem-GridColumn` | `width` | `auto` | `auto` | R4 |

---

### B.4 Showcase card ×4 — `realmac/components/teaser` variant `cmp-teaser--innovation-card`

- **Classification:** (A) Style System variant on Core Teaser v2. No new component.
- **Field mapping (author → Core Teaser):** `jcr:title` → card title (e.g. "Tata Chemicals Innovation
  Centre"), `description` → optional short descriptor, `fileReference` → card image (via
  `imageDelegate=realmac/components/image`), `actions[0].link` + `actions[0].text` → the single
  arrow-style CTA link (no filled button, per R4).
- **Heading level:** `titleType=h3` — this instance does **not** own the page H1 (see
  `template-design.md § Heading-level budget`).
- **Style System policy:** `realmac/components/teaser/policy_landing_card_teaser` (new,
  landing-page-specific, reachable ONLY at the nested card-grid container path — see
  `policy-mapping.md` for why this does not collide with B.1's hero policy at a different path).
- **Style variant class:** `cmp-teaser--innovation-card`.
- **Image-first visual order (D7 corollary):** Core Teaser v2 emits `.cmp-teaser__content` before
  `.cmp-teaser__image` in the DOM; the card needs the image to appear visually FIRST (top). Per D7,
  use `order` — **not** `flex-direction: column-reverse` (which would reverse tab/keyboard order).

**SCSS invariants:**

```scss
.cmp-teaser--innovation-card {
    display: flex;
    flex-direction: column;
    background: #ffffff;
    border: 1px solid #e5e5e5;
    border-radius: 6px;      // pinned per design-token-audit § Radii (reference-measured, overrides
                              // the generic 16px guardrail default)
    overflow: hidden;

    .cmp-teaser__image {
        order: -1;             // visually first; DOM/tab order unchanged (a11y-safe per D7)

        img {
            width: 100%;
            aspect-ratio: 1.38;   // ~360:260 supplied card image ratio
            object-fit: cover;
        }
    }

    .cmp-teaser__content {
        order: 0;
        padding: 20px 20px 24px;
    }

    .cmp-teaser__title {
        font-size: 20px;
        font-weight: 700;
        color: #1a1a1a;
        @media (max-width: 767px) { font-size: 18px; }
    }

    .cmp-teaser__description {
        font-size: 14px;
        color: #555555;
        margin-top: 8px;
    }

    .cmp-teaser__action-link {
        display: inline-block;
        margin-top: 16px;
        align-self: flex-end;
        font-size: 14px;
        font-weight: 500;
        color: #0a66c2;

        &::after { content: ' \2192'; }   // arrow glyph, no filled button per R4
    }
}
```

**Pixel-Verified Acceptance Criteria:**

| Selector | Property | Expected @ 1440 | Expected @ 390 | Region |
|---|---|---|---|---|
| `.cmp-teaser--innovation-card` | `display` | `flex` | `flex` | R4 |
| `.cmp-teaser--innovation-card` | `border-radius` | `6px` | `6px` | R4 |
| `.cmp-teaser--innovation-card` | `border` | `1px solid rgb(229, 229, 229)` | same | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__image` | `order` | `-1` | `-1` | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__image img` | `aspect-ratio` | `1.38` | `1.38` | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__image img` | `object-fit` | `cover` | `cover` | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__title` | `font-size` | `20px` | `18px` | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__title` | `color` | `rgb(26, 26, 26)` | `rgb(26, 26, 26)` | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__description` | `font-size` | `14px` | `14px` | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__description` | `color` | `rgb(85, 85, 85)` | `rgb(85, 85, 85)` | R4 |
| `.cmp-teaser--innovation-card .cmp-teaser__action-link` | `color` | `rgb(10, 102, 194)` | `rgb(10, 102, 194)` | R4 (cross-region) |

---

## Part C — Chrome component Pixel-Verified Acceptance Criteria

### C.1 `site-header`

| Selector | Property | Expected @ 1440 | Expected @ 390 | Region |
|---|---|---|---|---|
| `.cmp-site-header` | `background-color` | `rgb(255, 255, 255)` | `rgb(255, 255, 255)` | R1 |
| `.cmp-site-header` | `border-bottom` | `1px solid rgb(229, 229, 229)` | same | R1 |
| `.cmp-site-header__inner` | `display` | `flex` | `flex` | R1 |
| `.cmp-site-header__inner` | `justify-content` | `space-between` | `space-between` | R1 |
| `.cmp-site-header__inner` | `min-height` | `72px` | `64px` | R1 |
| `.cmp-site-header__logo-image` | `height` | `44px` | `32px` | R1 |
| `.cmp-site-header__nav` | `display` | `flex` | `none` | R1 |
| `.cmp-site-header__menu-toggle` | `display` | `none` | `flex` | R1 |
| `.cmp-site-header__nav a` | `font-size` | `15px` | n/a (hidden) | R1 |
| `.cmp-site-header__nav a` | `color` | `rgb(26, 26, 26)` | n/a | R1 |
| `.cmp-site-header__utility-icon` | `width` / `height` | `22px` / `22px` | `22px` / `22px` | R1 |

### C.2 `site-footer`

| Selector | Property | Expected @ 1440 | Expected @ 390 | Region |
|---|---|---|---|---|
| `.cmp-site-footer` | `background-color` | `rgb(26, 26, 26)` | `rgb(26, 26, 26)` | R5 |
| `.cmp-site-footer` | `border-top` | `1px solid rgba(255, 255, 255, 0.1)` | same | R5 |
| `.cmp-site-footer__columns` | `display` | `grid` | `grid` | R5 |
| `.cmp-site-footer__columns` | `grid-template-columns` | `repeat(4, 1fr)` | `1fr` (stacked) | R5 |
| `.cmp-site-footer__columns` | `gap` | `32px` | `24px` | R5 |
| `.cmp-site-footer__column-heading` | `font-size` / `color` | `15px` / `rgb(255, 255, 255)` | same | R5 |
| `.cmp-site-footer__link` | `font-size` / `color` | `13px` / `rgb(204, 204, 204)` | same | R5 |
| `.cmp-site-footer__social` | `display` | `flex` | `flex` | R5 |
| `.cmp-site-footer__social` | `justify-content` | `flex-start` | `center` | R5 |
| `.cmp-site-footer__social-icon` | `width` / `height` | `24px` / `24px` | `24px` / `24px` | R5 |
| `.cmp-site-footer__legal-text` | `font-size` / `color` | `12px` / `rgb(153, 153, 153)` | same | R5 |

---

## Reuse manifest (confirms no forbidden custom HTL/dialog on proxies — D12)

| Item | `sling:resourceType` | Custom dialog? | Custom HTL? | Custom `_cq_editConfig`? |
|---|---|---|---|---|
| Hero | `realmac/components/teaser` (proxy → `core/wcm/components/teaser/v2/teaser`) | No | No | No |
| Intro text | `realmac/components/text` (proxy → `core/wcm/components/text/v2/text`) | No | No | No |
| Card grid | `realmac/components/container` (proxy → `core/wcm/components/container/v1/container`) | No | No | No |
| Card | `realmac/components/teaser` (same proxy as Hero) | No | No | No |
| Optional intro heading | `realmac/components/title` (proxy → `core/wcm/components/title/v3/title`) | No | No | No |
| `site-header` | `realmac/components/site-header` (new, standalone) | Yes (own dialog — see `dialog-specifications.md`) | Yes (own HTL — implementation, not this doc) | Not applicable (own component, no super-type) |
| `site-footer` | `realmac/components/site-footer` (new, standalone) | Yes (own dialog) | Yes (own HTL) | Not applicable |
