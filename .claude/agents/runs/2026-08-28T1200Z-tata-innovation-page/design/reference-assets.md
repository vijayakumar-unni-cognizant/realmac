# Reference Assets Manifest — Tata Innovation Landing Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Count correction carried forward from Strategist/dispatch:** the authoritative supplied-asset
  count is **13** (Strategist's `technical-specifications.md §6` narrative said "8 supplied binaries"
  but its own itemized list — and the verified local folder listing — is 13 files). This manifest uses
  13 as authoritative.

---

## Reference URL

| Source | Role | Strictness | Consumes into |
|---|---|---|---|
| `https://www.tata.com/about-us/innovation` | `visual-reference-only` (per `handoffs/strategist.yaml § reference_role_classification`) — layout/structure/spacing/color are source of truth; **copy is NOT sourced from this reference** | Directional — layout/spacing/color tokens pinned in `design-token-audit.md` are approximations of this reference (reference ships obfuscated CSS; ranges were visually estimated then pinned). DOM/CSS/HTML transplant is explicitly **forbidden**. | `design-token-audit.md` (all token categories), `component-specifications.md` (Pixel-Verified Acceptance Criteria tables, citing `reference-deconstruction.md` region numbers) |

No `source-content-inventory.md` is produced for this run: the reference's role is
`visual-reference-only`, not `content-source-of-truth` — per the Designforge workflow, that inventory
is required "whenever a reference source carries content." This reference does not carry content;
Composer authors neutral demo copy mirroring the reference's *structure* only (per S9.a, confirmed in
`reference-deconstruction.md § Reference role classification` and `plan/requirements.yaml § Q-002`).

---

## Locally supplied assets (13 files, folder `C:\Users\2489691\Downloads\tata-innovation-assets`)

| # | Filename | Used this run? | Consuming component / section | Match strictness |
|---|---|---|---|---|
| 1 | `about_innovation_banner_desktop_1920x1080.jpg` | Yes | Hero teaser (`cmp-teaser--hero`) `fileReference` — US-001 | Exact — this is the authoritative hero image binary, seeded verbatim to DAM |
| 2 | `TataChemicals_Desk.jpg` | Yes | Card teaser #1 (`cmp-teaser--innovation-card`) `fileReference` — US-003 | Exact — seeded verbatim |
| 3 | `TataSteelEurope_Desk.jpg` | Yes | Card teaser #2 `fileReference` — US-003 | Exact — seeded verbatim |
| 4 | `TMETC_Desk.jpg` | Yes | Card teaser #3 `fileReference` — US-003 | Exact — seeded verbatim |
| 5 | `tcsinnovation_information_desktop_360x260.jpg` | Yes | Card teaser #4 `fileReference` — US-003 | Exact — seeded verbatim |
| 6 | `tata-logo.svg` | Yes | `site-header` `logoFileReference` — US-004 (demo-only use; see licensing risk below) | Exact — seeded verbatim |
| 7 | `search.svg` | Yes | `site-header` utility link icon ("Search") — US-004 | Exact — seeded verbatim |
| 8 | `video.svg` | **Supplied, not-used-this-run** | No video-modal UI is in this run's scope (`requirements.yaml`/`technical-specifications.md` — no video component/US covers it) | N/A — seeded to DAM for completeness per dispatch note, not referenced by any component this run |
| 9 | `close.svg` | **Supplied, not-used-this-run** | No dismiss/modal UI is in this run's scope | N/A — seeded to DAM for completeness, not referenced this run |
| 10 | `ContactUs.svg` | Yes | `site-header` utility link icon ("Contact") **and** `site-footer` social-row icon ("Contact") — US-004, US-005 | Exact — seeded verbatim, used in two places |
| 11 | `FB.svg` | Yes | `site-footer` social link icon ("Facebook") — US-005 | Exact — seeded verbatim |
| 12 | `Instagram.svg` | Yes | `site-footer` social link icon ("Instagram") — US-005 | Exact — seeded verbatim |
| 13 | `Linkedin.svg` | Yes | `site-footer` social link icon ("LinkedIn") — US-005 | Exact — seeded verbatim |

**Totals:** 13/13 accounted for. 11 used this run (image assets + 5 of 7 icons, note `ContactUs.svg`
used twice); 2 (`video.svg`, `close.svg`) explicitly marked `supplied, not-used-this-run` rather than
omitted — Composer still seeds all 13 to `/content/dam/realmac/tata-innovation/` so Auditron/Sentinel
do not flag the unreferenced two as an unexplained gap.

---

## Seeding target

All 13 assets seed to `/content/dam/realmac/tata-innovation/` with real binaries + original
renditions (C11 — no `dam:Asset` node without a rendition). See `authoring-test-cases.md` AUTH-010
for the data-setup-integrity check.

---

## Licensing flag (carried forward, non-blocking for design/build)

`tata-logo.svg` and the Tata-branded imagery are supplied for **demo use only** in this `realmac`
project. Per `technical-specifications.md § Risks`, this is a Human/Lead decision
(`plan/requirements.yaml § Q-004`) to replace before any non-demo/production use — not a Designforge,
Blockwright, Configsmith, Composer, or Auditron gate.
