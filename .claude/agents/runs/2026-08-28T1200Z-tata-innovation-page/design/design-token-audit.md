# Design Token Audit — Tata Innovation Landing Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Source:** `plan/reference-deconstruction.md` (best-effort ranges) — this document PINS every range to
  a single concrete value. Blockwright's SCSS MUST implement these values verbatim (no re-deriving
  from the reference). Auditron statically diffs SCSS against this file; Sentinel Tier-A visual diff
  (deferred) verifies the rendered page against it at resume.
- **Viewport convention for Pixel-Verified Acceptance Criteria tables (component-specifications.md):**
  desktop = 1440px viewport (falls in the project's existing `≥1200px` desktop breakpoint band),
  mobile = 390px viewport (falls in the project's existing `<768px` phone band). Tablet (768–1199px)
  is cited as an informative third column where the reference calls out tablet-specific scaling but
  is not required by the gate.
- **Dark-mode note:** the reference ships a dark-mode toggle; per `requirements.yaml § out_of_scope`
  ("Dark-mode toggle behavior is treated as optional/low-priority enhancement, not a fidelity blocker"),
  this audit pins **light mode only**. No dark-mode tokens are specified this run.

---

## 1. Colors

| Token | Pinned value | Role | Region |
|---|---|---|---|
| `$color-page-bg` | `#ffffff` | Page / intro background | R3 |
| `$color-heading` | `#1a1a1a` | Headings, nav text, card titles (light mode) | R1, R4 |
| `$color-body` | `#333333` | Intro lead + body paragraph text | R3 |
| `$color-body-muted` | `#555555` | Card descriptor text | R4 |
| `$color-hairline` | `#e5e5e5` | Header bottom hairline, card border | R1, R4 |
| `$color-inverse-fg` | `#ffffff` | Hero title, footer column headings | R2, R5 |
| `$color-inverse-fg-muted` | `#cccccc` | Footer link text | R5 |
| `$color-footer-bg` | `#1a1a1a` | Footer background | R5 |
| `$color-footer-divider` | `rgba(255,255,255,0.1)` | Footer top divider | R5 |
| `$color-legal` | `#999999` | Footer legal/copyright text | R5 |
| `$color-scrim-top` | `rgba(0,0,0,0.25)` | Hero scrim gradient start (top) | R2 |
| `$color-scrim-bottom` | `rgba(0,0,0,0.45)` | Hero scrim gradient end (bottom, behind title) | R2 |
| `$color-accent-link` | `#0a66c2` | Card arrow-CTA link, inline text links | R4 (cross-region) |

## 2. Typography

Font family reuses the existing project default (`ui.frontend/src/main/webpack/site/_variables.scss`
`$font-family`) — `"Helvetica Neue", Helvetica, Arial, sans-serif` — per reference-deconstruction's own
note ("reference uses a branded sans; demo uses project default sans").

| Token | Desktop (≥1200px) | Tablet (768–1199px) | Mobile (<768px) | Role | Region |
|---|---|---|---|---|---|
| `$font-size-hero-title` | 56px / weight 700 | 36px / weight 700 | 28px / weight 700 | Hero "Innovation" title | R2 |
| `$font-size-nav` | 15px / weight 500 | 15px / weight 500 | n/a (collapsed) | Header primary nav links | R1 |
| `$font-size-intro-lead` | 21px / weight 400 / line-height 1.5 | 21px / weight 400 / line-height 1.5 | 21px / weight 400 / line-height 1.5 | Intro first paragraph | R3 |
| `$font-size-intro-body` | 16px / weight 400 / line-height 1.6 | 16px / weight 400 / line-height 1.6 | 16px / weight 400 / line-height 1.6 | Intro subsequent paragraphs | R3 |
| `$font-size-intro-heading` | 26px / weight 700 | 26px / weight 700 | 22px / weight 700 | Optional intro sub-heading (Title cmp, h2) | R3 |
| `$font-size-card-title` | 20px / weight 700 | 20px / weight 700 | 18px / weight 700 | Card title | R4 |
| `$font-size-card-body` | 14px / weight 400 | 14px / weight 400 | 14px / weight 400 | Card descriptor | R4 |
| `$font-size-card-cta` | 14px / weight 500 | 14px / weight 500 | 14px / weight 500 | Card arrow-link | R4 |
| `$font-size-footer-heading` | 15px / weight 700 | 15px / weight 700 | 15px / weight 700 | Footer column heading | R5 |
| `$font-size-footer-link` | 13px / weight 400 | 13px / weight 400 | 13px / weight 400 | Footer link item | R5 |
| `$font-size-legal` | 12px / weight 400 | 12px / weight 400 | 12px / weight 400 | Footer legal/copyright | R5 |

> Reference note: "font sizes hold roughly constant on mobile" for intro/footer/legal — those tokens
> are intentionally identical across all three breakpoints above.

## 3. Spacing scale

| Token | Pinned value | Role | Region |
|---|---|---|---|
| `$space-section-vertical-desktop` | 56px | Intro block top/bottom padding, desktop/tablet | R3 |
| `$space-section-vertical-mobile` | 32px | Intro block top/bottom padding, mobile | R3 |
| `$space-paragraph` | 20px | Paragraph spacing (lead → body, body → body) | R3 |
| `$space-gutter-mobile` | 20px | Mobile side gutter (intro column) | R3 |
| `$space-card-gap` | 24px | Card grid gutter, row + column | R4 |
| `$space-card-padding` | 20px 20px 24px | Card content inner padding | R4 |
| `$space-hero-inset-desktop` | 48px | Hero title inset from left/bottom edge, desktop | R2 |
| `$space-hero-inset-mobile` | 24px | Hero title inset from left/bottom edge, mobile | R2 |
| `$space-header-nav-gap` | 28px | Header nav item horizontal gap | R1 |
| `$space-utility-icon-gap` | 16px | Header utility icon row gap | R1 |
| `$space-footer-column-gap-desktop` | 32px | Footer column grid gap, desktop | R5 |
| `$space-footer-column-gap-mobile` | 24px | Footer column stack gap, mobile | R5 |
| `$space-social-gap` | 18px | Footer social icon row gap | R5 |
| `$content-max-width-text` | 840px | Intro text column max-width | R3 |
| `$content-max-width-grid` | 1200px | Card grid / general content container max-width | R4 (cross-region) |

## 4. Radii

| Token | Pinned value | Role | Region |
|---|---|---|---|
| `$radius-card` | 6px | Card surface + card image top corners | R4 |

> **Deviation from generic guardrail default:** the project-wide card-radius guardrail default is
> 16px (D19). This run's reference explicitly measures a 0–6px range (R4), so **6px is pinned instead
> of the generic 16px default** — the reference measurement takes precedence over the generic
> guardrail because it is the more specific, sourced value. Recorded here for Auditron's Check 23
> traceability.

## 5. Breakpoints

Reused verbatim from the existing `page-content` template's `cq:responsive` config (per
`technical-specifications.md §4`, and per the reference-deconstruction cross-region token table):

| Breakpoint | Value | Convention |
|---|---|---|
| Mobile / phone | `< 768px` | matches `cq:responsive/breakpoints/phone` width=768 |
| Tablet | `768px – 1199px` | matches `cq:responsive/breakpoints/tablet` width=1200 |
| Desktop | `≥ 1200px` | implicit default arrangement |

Pixel-Verified Acceptance Criteria tables use **1440px** (desktop) and **390px** (mobile) as concrete
viewport probes within these bands.

## 6. Shadows

None. Reference Region 4 offers "subtle border or shadow" as alternatives; **border is pinned**
(`1px solid $color-hairline`) — no `box-shadow` token is defined this run. If Blockwright's SCSS adds
a shadow it is a deviation from this audit and must be flagged back to Designforge.

## 7. Layout patterns

| Pattern | Desktop/Tablet | Mobile | Region |
|---|---|---|---|
| Header row | `display:flex; justify-content:space-between; align-items:center;` | same, with nav replaced by menu-toggle button | R1 |
| Hero overlay | `position:relative; height:480px (tablet 360px); display:flex; align-items:flex-end;` image `position:absolute; inset:0;` | `height:280px` | R2 |
| Card grid | CSS Grid, `grid-template-columns: repeat(2, 1fr); gap:24px;` (2 cols desktop AND tablet per reference: "Tablet: 2 columns") | `grid-template-columns: 1fr;` (1 col <768px) | R4 |
| Card | `display:flex; flex-direction:column;` image visual order first via `order:-1` (Core Teaser v2 DOM emits content div before image div — see `component-specifications.md § SCSS invariants`) | same | R4 |
| Footer columns | CSS Grid, `grid-template-columns: repeat(4, 1fr); gap:32px;` | `grid-template-columns: 1fr; gap:24px;` (stacked) | R5 |

## 8. Z-index scale

| Token | Value | Role |
|---|---|---|
| `$z-hero-image` | 1 | Hero background image layer |
| `$z-hero-scrim` | 2 | Hero scrim gradient (`::after` on `.cmp-teaser__image`) |
| `$z-hero-content` | 3 | Hero title/content layer, above scrim |
| `$z-header` | 10 | Header row (sticky, above hero per R1 "Header sits above hero (sticky)") |

---

## Traceability

Every value above cites the `reference-deconstruction.md` region it pins. Component
`Pixel-Verified Acceptance Criteria` tables in `component-specifications.md` reference this file's
token names directly — no value in those tables is invented outside this audit.
