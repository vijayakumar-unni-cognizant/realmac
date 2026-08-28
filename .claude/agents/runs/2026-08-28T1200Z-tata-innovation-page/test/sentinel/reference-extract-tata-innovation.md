# Reference Extract — https://www.tata.com/about-us/innovation

Fetched via `WebFetch` this dispatch. Per intake scope (`plan/reference-deconstruction.md`,
`design/reference-assets.md`), this reference is **visual/layout reference only** — layout intent,
typography, and spacing fidelity are judged; verbatim DOM/CSS/copy transplant was never in scope and
content differences below are expected, not defects, unless they break an explicitly pinned layout
intent from `design/design-token-audit.md`.

## (1) Header / navigation
- Logo top-left, clickable to home.
- Horizontal nav, side-by-side at desktop width: Business / Community / About Us / Newsroom / Careers /
  Worldwide / Contact Us.
- Utility controls top-right: dark/light toggle, search icon, filter icon.
- Mobile: menu button collapses nav.

## (2) Hero
- Headline: "Innovation".
- Full-width hero background image (innovation theme).
- Headline text overlaid on the image, moderate-opacity treatment.

## (3) Lead paragraph
- Full-width paragraph immediately below the hero; describes innovation as a strategic priority.

## (4) Section heading
- "Group-wide Innovation Forum" — introduces the card/grid section.

## (5) Card grid
- Layout intent: 4 cards, 2×2 at desktop width.
- Each card: bold title, description paragraph, image, right-arrow CTA linking to a resource page.
- Cards: Tata Chemicals Innovation Centre / Tata Steel Europe RD&T / TCS Innovation Labs / Tata Motors
  European Technical Centre.

## (6) Footer
- Multi-column horizontal layout at desktop width (Business/Community/About/Newsroom/Careers columns).
- 5 social icons (Facebook/LinkedIn/Twitter/YouTube/Instagram).
- Legal/copyright line + repeated legal links at the very bottom.

---

## Region-by-region comparison against the deployed build (`/content/realmac/us/en/innovation.html`)

| Region | Reference layout intent | Deployed (Publish) | Verdict |
|---|---|---|---|
| Header | Logo top-left; horizontal nav; utility icons top-right | Logo top-left; horizontal nav (1 item, "Innovation" — expected, single-page demo scope); 2 utility icons top-right (Search, Contact) | Layout intent **matches**. Content volume reduced — expected per Checkpoint-1-approved scope, not a fidelity miss |
| Hero | Full-width bg image; headline overlaid, moderate-opacity treatment | Full-width bg image; headline overlaid with a dark scrim gradient (desktop) | Layout intent **matches at desktop**. **Diverges at mobile** — headline sits below the image on a plain light-gray block, not overlaid — see F-A11Y-01 (contrast) and Visual section, `major` |
| Lead paragraph | Full-width paragraph below hero | Full-width paragraph (2 `<p>`) below hero, `max-width:840px` centered | Layout intent **matches** |
| Section heading | "Group-wide Innovation Forum" | "Driving Innovation Across Our Businesses" | Role matches (intro heading before card grid); text differs — **expected**, reference is visual-only, not verbatim copy |
| Card grid | 4 cards, 2×2 at desktop | 4 cards, confirmed 2×2 (`grid-template-columns:repeat(2,1fr)`), 1-col <768px | Layout intent **matches**. CTA styling **diverges**: reference has a visible right-arrow CTA; deployed cards render **no CTA link at all** (plain "Learn More" text, no anchor, no arrow) — see F-LINK-01, `high`, correctness-class (dangling link target suppresses the anchor+arrow entirely; the CSS rule for the arrow exists and is even present in the clientlib but never applies since the `<a class="cmp-teaser__action-link">` element never renders) |
| Footer | Multi-column horizontal, 5 social icons, legal line | 4-column horizontal (`repeat(4,1fr)`), 4 social icons, legal line "© 2026 Realmac. All rights reserved." | Layout intent **matches** (column/social/legal composition). Column/icon count reduced — expected, approved design-pack scope (4 columns per `dialog-specifications.md`, not 5). **7 of 9 footer links and 1 of 4 social links render as unlinked plain text** — see F-LINK-01 |

## Screenshots
- `screenshots/innovation-desktop.png` (1440×900)
- `screenshots/innovation-mobile.png` (390×844)
