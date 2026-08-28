# Reference Deconstruction — Tata Innovation Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Reference URL:** https://www.tata.com/about-us/innovation (VISUAL / DESIGN reference only — NO DOM/CSS/HTML transplant)
- **Reference role classification (per S9.a):** `visual-reference-only`.
  - Layout, structure, spacing, grid, and color are the source of truth for fidelity.
  - Copy is authored fresh for the `realmac` demo — do NOT invent Tata brand copy as production content. Composer supplies short representative demo copy that mirrors the reference's *structure* (heading + lead paragraph + 4 named innovation-centre cards), not verbatim Tata content.
  - Supplied local assets (banner, 4 card images, logo, icons) are the visual/content assets to author against.
- **Fidelity gate:** ADLC-SPEC §P6 active. This document is Designforge's authoritative visual input for `component-specifications.md`, Blockwright's SCSS source of truth, and Sentinel's Tier-A visual-diff acceptance basis (deferred this run).
- **Method note:** the reference site ships obfuscated/minified CSS and lazy-hydrated markup; measurements below are best-effort visual approximations from the rendered page + the supplied asset dimensions. Designforge should treat sizes as ranges and pin exact tokens in the `design-token-audit.md`.

---

## Region inventory (top → bottom)

1. Header / primary navigation (chrome)
2. Hero banner (full-bleed image + title overlay)
3. Intro / overview text block (lead quote + strategy body)
4. Innovation showcase card grid (4 business-unit cards)
5. Footer / social chrome

---

## Region 1 — Header / Primary Navigation

| Attribute | Observation |
|---|---|
| Layout intent | Single horizontal row, sticky at top. Logo left-aligned; primary nav + utility icons right-aligned. |
| Split | Logo cluster (~15% width) \| flexible gap \| nav links + utility icons (right cluster). |
| Background | White (`#ffffff`) in light mode; near-black (`#111`/`#1a1a1a`) in dark mode. Thin bottom hairline border (~1px, `#e5e5e5`). |
| Height | ~64–72px desktop. |
| Logo | `tata-logo.svg`, left, ~40–48px tall, vertically centered. |
| Nav items | Medium-weight sans-serif, ~14–16px, dark text (`#1a1a1a`), ~24–32px horizontal gap between items, inline row. |
| Utility icons | Right cluster: search (`search.svg`), contact (`ContactUs.svg`), plus a dark-mode toggle. Monochrome, ~20–24px. |
| Breakpoint behavior | Nav collapses to a hamburger/off-canvas below ~768px; logo stays left, toggle becomes menu button. |
| Overlay/z-index | Header sits above hero (sticky, `z-index` above banner). Does NOT visually overlay the hero image in light mode — it is a solid bar above it. |

## Region 2 — Hero Banner

| Attribute | Observation |
|---|---|
| Layout intent | Full-bleed (100vw) banner image with a single title overlaid. No CTA button. |
| Image | `about_innovation_banner_desktop_1920x1080.jpg`, 16:9 aspect, full width. Renders as a wide band (~420–560px tall desktop; image top-cropped on smaller viewports). |
| Overlay | Subtle dark gradient/scrim over the image (left-to-bottom) to keep the title legible — approx `rgba(0,0,0,0.25–0.45)`. |
| Title | "Innovation" — large, bold, white (`#ffffff`), ~48–64px desktop, ~32–40px tablet, ~28px mobile. Positioned lower-left or center-left of the banner. |
| Pretitle / description | None visible in hero (title only). |
| Breakpoint behavior | Image scales full-width, maintains aspect (letterbox/crop). Title font scales down; stays anchored to the same relative corner. |

## Region 3 — Intro / Overview Text Block

| Attribute | Observation |
|---|---|
| Layout intent | Single centered column, generous max-width (~760–900px), left-aligned text. |
| Background | White (`#ffffff`). |
| Lead text | Opening lead/quote paragraph, larger than body — ~20–22px, regular/medium weight, dark gray (`#333`). Example theme: "Innovation — in thoughts, processes, approaches and strategies — has become a critical factor for Tata companies…". |
| Body text | Following paragraphs ~16px, regular, `#333`/`#444`, line-height ~1.6, describing the three-pronged innovation strategy + Group Innovation Forum. Occasional bold emphasis inline. |
| Spacing rhythm | ~40–60px vertical padding above/below the block; ~16–20px paragraph spacing. |
| Section heading | If present, a secondary heading (~24–28px, bold) introduces the strategy sub-section. |
| Breakpoint behavior | Column narrows to viewport with side gutters (~20–24px) on mobile; font sizes hold roughly constant. |

## Region 4 — Innovation Showcase Card Grid

| Attribute | Observation |
|---|---|
| Layout intent | Grid of 4 cards. Each card = image (top) + title + short descriptor + arrow-link CTA (bottom). |
| Grid columns | Desktop: 2 columns (2×2). Tablet: 2 columns. Mobile: 1 column (stacked). |
| Card gap | ~20–30px gutter between cards, both axes. |
| Card image | ~4:3 / ~1.38:1 aspect (supplied cards are 360×260 ≈ 1.38:1). Image fills card width, top of card. |
| Card images | `TataChemicals_Desk.jpg`, `TataSteelEurope_Desk.jpg`, `TMETC_Desk.jpg`, `tcsinnovation_information_desktop_360x260.jpg`. |
| Card title | Bold sans-serif, ~18–22px, dark (`#1a1a1a`), 1–2 lines. |
| Card body | Optional short descriptor, ~14px, `#555`. |
| Card CTA | Arrow-icon link (→) bottom-right of card, or the whole card is clickable. No filled button. |
| Card surface | White card on white/very-light-gray page; subtle border or shadow, slight corner rounding (~4–6px) or square. |
| Named cards | Tata Chemicals Innovation Centre · Tata Steel Europe RD&T · TCS Innovation Labs · Tata Motors European Technical Centre. |
| Breakpoint behavior | 2-col → 1-col at ~768px; card image aspect preserved; title/body reflow. |

## Region 5 — Footer / Social Chrome

| Attribute | Observation |
|---|---|
| Layout intent | Multi-column link footer with a social-icon row and a copyright/legal bar at the bottom. |
| Background | Dark (`#1a1a1a`/near-black) with white (`#fff`) / light-gray (`#ccc`) text and icons. |
| Columns | 4–5 columns of grouped links (business verticals, community, about, newsroom/careers). Column headings bold ~14–16px; link items regular ~12–14px, ~8–10px vertical spacing. |
| Social row | Horizontal row of monochrome icons: `FB.svg`, `Linkedin.svg`, `Instagram.svg` (+ X/Twitter, YouTube in reference), and `ContactUs.svg`. ~24×24px, ~16–20px gap. |
| Logo | Optional footer logo (`tata-logo.svg`, white/inverted variant), top of footer or aside social row. |
| Legal bar | Small text (~12px, `#999`) at very bottom: "© 2019-2027 Tata Sons Private Limited. All Rights Reserved." — for demo, use a neutral realmac copyright line, NOT Tata's. |
| Breakpoint behavior | Columns stack (4→2→1) as viewport narrows; social row centers on mobile. |
| Overlay/accent | Thin top divider separating footer from page body. |

---

## Cross-region design tokens (best-effort — Designforge to finalize in `design-token-audit.md`)

| Token | Approx value |
|---|---|
| Body font | System/neutral sans-serif stack (reference uses a branded sans; demo uses project default sans) |
| Body color | `#333` / `#444` on light |
| Heading color (light) | `#1a1a1a` |
| Inverse text (footer/hero) | `#ffffff` / `#ccc` |
| Page background | `#ffffff` |
| Footer background | `#1a1a1a` |
| Hero scrim | `rgba(0,0,0,0.25–0.45)` |
| Accent / link | Blue-ish link accent (~`#0a66c2`-range) — confirm |
| Section vertical rhythm | 40–60px |
| Content max-width | ~900px (text), ~1200px (grid/container) |
| Card radius | 0–6px |
| Desktop breakpoint | ≥1200px |
| Tablet breakpoint | 768–1199px |
| Mobile breakpoint | <768px |

Breakpoints intentionally align with the existing `page-content` template's `cq:responsive` config (phone <768, tablet <1200) so authored responsive behavior is consistent with the project.
