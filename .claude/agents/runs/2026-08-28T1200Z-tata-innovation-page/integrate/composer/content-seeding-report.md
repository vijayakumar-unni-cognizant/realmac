# Content Seeding Report — Tata Innovation Landing Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Agent:** composer
- **Tracks used:** content-seeding (no headless/CF-Model/GraphQL track this run — out of scope per `requirements.yaml`)

---

## Track 1 — DAM asset seeding

All 13 supplied assets from `C:\Users\2489691\Downloads\tata-innovation-assets` seeded to
`/content/dam/realmac/tata-innovation/` as real `dam:Asset` nodes with an actual binary + `original`
rendition (never a bare `dam:Asset` node). See `dam-fixture-manifest.yaml` for the full per-asset
table.

- Byte-for-byte checksum (sha1) of every seeded `_jcr_content/renditions/original` binary was diffed
  against its source fixture — **all 13 match**.
- Raster (JPEG) assets: actual pixel dimensions were measured (not invented) via
  `System.Drawing.Image` and authored as `tiff:ImageWidth`/`tiff:ImageLength` (`{Long}`) alongside
  `dc:format`, `dc:title`, `dc:description`:
  - `about_innovation_banner_desktop_1920x1080.jpg` → 1920×1080
  - `TataChemicals_Desk.jpg`, `TataSteelEurope_Desk.jpg`, `TMETC_Desk.jpg`,
    `tcsinnovation_information_desktop_360x260.jpg` → 360×260 (all four)
- SVG assets (7): `dc:format="image/svg+xml"` + `dc:title`/`dc:description` authored; no
  `tiff:ImageWidth/Length` (not applicable — vector, not raster).
- `video.svg` and `close.svg` are seeded but **not referenced by any component this run** (no
  video-modal/dismiss UI in scope) — per `reference-assets.md`, this is intentional, not a gap.
- **Filter fix required and applied:** `ui.content`'s `META-INF/vault/filter.xml` had
  `/content/dam/realmac` filtered with an `exclude` covering the whole subtree except
  `jcr:content` and one explicit sibling filter for the pre-existing `asset.jpg`. Without a matching
  explicit filter root, the new `tata-innovation/` folder would **not** have been packaged on
  deploy despite existing on disk. Added
  `<filter root="/content/dam/realmac/tata-innovation" mode="merge"/>` following the exact
  convention already used for `asset.jpg`.
- **Orphaned content removed:** the header master XF's `_jcr_content/root/logo/logo.svg` — a stray
  binary file on disk with no corresponding node declared in the XF's `.content.xml` (not part of
  the old Navigation/LanguageNavigation/Search stack, not referenced by any component) — was deleted
  as part of the "genuine replace, not addition" requirement, rather than left as unreferenced
  clutter that would still import as a dangling node.

## Track 2 — Template registration verification (not assumed)

Verified directly against repo state on disk (no live AEM instance available in this environment to
drive the Create Page wizard UI — see Deferred verification below):

- `/content/realmac/.content.xml` → `jcr:content/@cq:allowedTemplates` =
  `[/conf/realmac/settings/wcm/templates/(?!xf-).*]` — confirmed present, verbatim as documented.
- `/content/realmac/us/.content.xml` and `/content/realmac/us/en/.content.xml` → **no**
  `cq:allowedTemplates` property on either (both inherit from `/content/realmac`).
- `landing-page`'s own `cq:Template` node (`.../templates/landing-page/.content.xml`) →
  `status="enabled"` (not `"active"`) and `allowedPaths="[/content/realmac(/.*)?]"` — both confirmed.
- The template name `landing-page` does not start with `xf-`, so it matches the negative-lookahead
  regex; combined with `status="enabled"` and the matching `allowedPaths`, static verification
  **passes**.
- **Path taken:** no override added — the existing permissive regex already covers `landing-page`.
  No edit was made to `/content/realmac/us/en`'s `jcr:content`.
- **Deferred (real-environment) verification:** confirming the template actually appears in the
  Create Page wizard UI at `/content/realmac/us/en` requires a running author instance, which is out
  of scope for this agent (`mvn`/deploy not invoked per run instructions). Flagged for Auditron's
  local SDK install / Sentinel's real-environment stage.

## Track 3 — Header/footer master XF content replacement (Human Checkpoint 2)

Both master Experience Fragments had their entire previous content **removed and replaced** (not
added alongside) with exactly one cohesive component instance each:

- `/content/experience-fragments/realmac/us/en/site/header/master` — removed `navigation` +
  `languagenavigation` + `search`; added one `realmac/components/site-header` instance:
  - Logo: `/content/dam/realmac/tata-innovation/tata-logo.svg`, alt text `"Realmac"` (neutral, not
    Tata's brand language).
  - `logoLinkURL` intentionally **omitted** — exercises the model's documented default fallback
    (`/content/realmac/us/en.html`) rather than re-stating it, per AUTH-019.
  - Navigation: root `/content/realmac/us/en`, depth `1`.
  - Utility links (composite multifield, 2 entries, each with label + icon + link URL + a11y label):
    Search (`search.svg` → `/content/realmac/us/en/search.html`, aria "Search") and Contact
    (`ContactUs.svg` → `/content/realmac/us/en/contact.html`, aria "Contact us").
- `/content/experience-fragments/realmac/us/en/site/footer/master` — removed `separator` + `text`
  (which carried Tata's actual copyright/address text); added one `realmac/components/site-footer`
  instance:
  - Columns (4): Company, Innovation, Resources, Legal — each with a heading and 2–3 neutral demo
    links (`/content/realmac/us/en/...`), mirroring the reference's column-based structure without
    inventing Tata-specific URLs.
  - Social links (4, composite multifield): Facebook (`FB.svg`), LinkedIn (`Linkedin.svg`), Instagram
    (`Instagram.svg`), Contact (`ContactUs.svg`) — each with icon + URL + label (used as the a11y
    label).
  - Legal: `"© 2026 Realmac. All rights reserved."` — a neutral realmac copyright line; Tata's
    original copyright/address text was **not** carried forward (S9.a).
  - `footerLogoFileReference` intentionally **omitted** (optional; no footer-specific inverted logo
    asset was supplied).

## Track 4 — Sample page authoring

`/content/realmac/us/en/innovation` authored as a real `cq:Page` using the `landing-page` template.
`us` and `en` were verified as pre-existing `cq:Page` nodes (not `nt:folder`) — only `innovation`
is new.

- **Page Properties:** `jcr:title="Innovation"`; `jcr:description` set to a neutral SEO meta
  description (the template has no structural page-title component, so this is the only place
  page-level SEO text is authored, per D22).
- **Authoring depth:** all authored components sit at `jcr:content/root/container/container/*` —
  two levels inside `root`, matching the template's one editable parsys (D11/C9) — not directly
  under `root`.
- **Hero Teaser** — `cq:styleIds="[20260828101]"` ("Hero"). Title = "Innovation". Image =
  `about_innovation_banner_desktop_1920x1080.jpg`. Alt text authored explicitly. No CTA authored
  (policy disables actions entirely for this variant, per `actionsDisabled="{Boolean}true"` on
  `policy_landing_hero_teaser`).
- **Intro Text** — `cq:styleIds="[20260828102]"` ("Intro Lead"). Two-paragraph original demo copy
  (lead + strategy body) mirroring the reference's narrative *shape* (Region 3) — no Tata sentences
  copied verbatim.
- **Optional secondary heading** — a plain `h2` Title, "Driving Innovation Across Our Businesses",
  placed after the intro text and before the card grid (author's structural choice per this agent's
  decision authority).
- **Card Grid Container** — `cq:styleIds="[20260828103]"` ("Card Grid"), containing exactly 4 Teaser
  instances, each `cq:styleIds="[20260828104]"` ("Innovation Card"):
  1. Tata Chemicals Innovation Centre — `TataChemicals_Desk.jpg`
  2. Tata Steel Europe RD&T — `TataSteelEurope_Desk.jpg`
  3. TCS Innovation Labs — `tcsinnovation_information_desktop_360x260.jpg`
  4. Tata Motors European Technical Centre — `TMETC_Desk.jpg`

  Each card has a short original descriptor (not copied Tata copy), alt text, and one
  arrow-style action link (label "Learn More" + a neutral demo URL under
  `/content/realmac/us/en/innovation/...`).

  **Flag for downstream review:** per the dispatch's explicit allowance, the 4 card titles keep the
  real Tata business-unit names ("Tata Chemicals Innovation Centre", "Tata Steel Europe RD&T", "TCS
  Innovation Labs", "Tata Motors European Technical Centre") as **structural placeholders** — these
  are factual entity names, not invented marketing copy, and the dispatch instructions explicitly
  permit keeping them with this flag rather than requiring the value be genericized.

- Every `cq:styleIds` value was cross-checked against the exact numeric `cq:styleId` authored by
  Configsmith in `ui.content/.../wcm/policies/.content.xml` (C7) — not the CSS class name — and each
  styled instance (hero, intro text, card-grid container, and **each of the 4 card teasers
  individually** — C13) carries its own `cq:styleIds` attribute.

## Reference integrity

- Every `fileReference`/`iconFileReference`/`logoFileReference` authored this run resolves to one of
  the 13 seeded DAM assets under `/content/dam/realmac/tata-innovation/` — no dangling asset paths.
- Both `fragmentVariationPath` references on the sample page (`experiencefragment-header`,
  `experiencefragment-footer`) resolve to the two master XFs edited in Track 3.
- `cq:template` on the sample page resolves to the `landing-page` template scaffolded by Blockwright.
- Every `sling:resourceType` used (`realmac/components/{teaser,text,title,container,page,
  experiencefragment,site-header,site-footer}`) resolves to a component that exists in `ui.apps`.

## Data-setup integrity

- No literal commas appear in any authored bracket-notation multi-value property this run (only
  `cq:styleIds="[<single-numeric-id>]"` arrays were authored, each with exactly one element) — the
  AUTH-011 escaping hazard is not exercised, consistent with the authoring-test-cases.md note.
- `ui.content`'s filter roots for this run's content (`/content/realmac`, the new
  `/content/dam/realmac/tata-innovation`, `/content/experience-fragments/realmac`) all use
  `mode="merge"`, which updates existing nodes on redeploy rather than only adding missing ones —
  satisfies the redeploy-reachability gate (AUTH-015 analog). Real-instance read-back after an actual
  `mvn ... -PautoInstallPackage` deploy is deferred to Auditron/Sentinel (no `mvn` invoked by this
  agent per run instructions).

## Smoke-render request (for Pilot)

Target URL: `/content/realmac/us/en/innovation.html`

Expected on render (no "Please configure" / empty-component placeholders):
- Header chrome: `.cmp-site-header` with visible logo, nav (depth 1 under `/content/realmac/us/en`),
  and two utility icon links (search, contact).
- Hero: `.cmp-teaser--hero` with the banner image and the "Innovation" H1, no CTA rendered.
- Intro: `.cmp-text--intro-lead` with a larger lead paragraph followed by body copy; the optional H2
  sub-heading below it.
- Card grid: `.cmp-container--card-grid` containing 4 `.cmp-teaser--innovation-card` instances (H3
  titles, images, short descriptors, one arrow-link action each).
- Footer chrome: `.cmp-site-footer` with 4 link columns, 4 social icons, and the neutral realmac
  copyright line — no Tata copyright/address text.

## Outputs

- `ui.content/src/main/content/jcr_root/content/dam/realmac/tata-innovation/` (13 DAM assets)
- `ui.content/src/main/content/jcr_root/content/experience-fragments/realmac/us/en/site/header/master/.content.xml`
- `ui.content/src/main/content/jcr_root/content/experience-fragments/realmac/us/en/site/footer/master/.content.xml`
- `ui.content/src/main/content/jcr_root/content/realmac/us/en/innovation/.content.xml`
- `ui.content/src/main/content/META-INF/vault/filter.xml` (added one filter root for the new DAM folder)
- `runs/2026-08-28T1200Z-tata-innovation-page/integrate/composer/dam-fixture-manifest.yaml`
- `runs/2026-08-28T1200Z-tata-innovation-page/integrate/composer/content-seeding-report.md` (this file)
- `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/composer.yaml`
