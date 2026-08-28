# Sentinel Coverage Matrix — `2026-08-28T1200Z-tata-innovation-page`

Census performed mechanically by Sentinel before executing anything, per contract §"Execute every
scenario" rule 0. `total` below is `total_from_file` in every case — no total was inherited from any
upstream handoff.

## ID census

| Artifact | Extraction command | `total_from_file` |
|---|---|---|
| `design/ui-test-scenarios.md` | `grep -oE '\bUI-[0-9]+\b' design/ui-test-scenarios.md \| sort -u` | 18 (UI-001..UI-018) |
| `design/authoring-test-cases.md` | `grep -oE '\bAUTH-[0-9]+\b' design/authoring-test-cases.md \| sort -u` | 20 (AUTH-001..AUTH-020) |
| `design/functional-test-cases.md` | `grep -oE '\bTC-[0-9]+\b' design/functional-test-cases.md \| sort -u` | 46 (TC-001..TC-046) |

---

## UI-001..UI-018 (`design/ui-test-scenarios.md`) — Playwright execution track

**Track status: `blocked_harness_defect`.** `ui.tests/test-module`'s `playwright.config.js` declares a
top-level `globalSetup` (`global-setup.js`) that performs a Granite `j_security_check` **credentials**-mode
login (`admin`/`admin` defaults) unconditionally for the **entire** Playwright binary, regardless of
`--project` filtering. This run's real Author tier auth mode is **bearer-token only** (per the resume
block) — there is no local `admin`/`admin` account, no `AEM_AUTHOR_USERNAME`/`AEM_AUTHOR_PASSWORD` was ever
supplied (correctly — bearer-token is the declared mode), and the login attempt 403s
(`AEM author login failed (HTTP 403)`, confirmed via `npm test` run, `/tmp/aem-playwright.log`). Because
Playwright's top-level `globalSetup` is not scoped per-project, this single auth failure aborts the run
**before any of the 18 specs execute** — including the 16 anonymous Publish-tier specs that need no author
auth at all. This is reported as finding **F-HARNESS-01** (severity high, routed to `blockwright`) — see
`sentinel-report.md`. Per contract, Sentinel did **not** modify `global-setup.js` or `playwright.config.js`
to work around this.

Sentinel additionally authored a small, clearly-labelled **standalone** script
(`test/sentinel/sentinel-standalone-a11y-visual-sweep.mjs` — never placed inside `ui.tests/test-module`'s
`tests/` discovery path, and removed from `ui.tests/test-module` after use; `git status --short ui.tests/`
confirmed byte-identical to pre-Sentinel state) using Playwright's own pinned Chromium via the library API
(not the CLI test runner, so it does not invoke the broken `globalSetup`) to still deliver the Sentinel-owned
a11y-deep sweep and Tier-A screenshot capture — see the A11y and Visual sections of `sentinel-report.md`.
This does **not** discharge execution of the 18 Blockwright-authored specs below, which require the
Playwright **Test Runner** (`test()`/`expect()` registration) and cannot be run any other way.

Where Sentinel had independent, non-Playwright evidence (direct HTML/DOM inspection via authenticated
`curl`) bearing on what a given spec asserts, that evidence is noted in the Manual cross-check column —
**this is informational only and does NOT change `result` from `blocked` to `pass`/`fail`**, per the
explicit instruction to mark un-runnable tracks "blocked-with-reason rather than pass or fail."

| ID | Spec file | Tier | Executed (Playwright) | Result | Manual cross-check (informational only) |
|---|---|---|---|---|---|
| UI-001 | `publish/ui-001-hero-desktop.spec.js` | Publish | no | blocked_harness_defect | Hero renders, h1 "Innovation", scrim overlay dark on desktop (screenshot: `screenshots/innovation-desktop.png`) — looks consistent with intent |
| UI-002 | `publish/ui-002-hero-tablet.spec.js` | Publish | no | blocked_harness_defect | Not captured at tablet width this dispatch (only 1440×900 / 390×844 per §P8 defaults) |
| UI-003 | `publish/ui-003-hero-mobile.spec.js` | Publish | no | blocked_harness_defect | **Mobile hero title fails color-contrast (1.18:1)** — see F-A11Y-01; screenshot `screenshots/innovation-mobile.png` |
| UI-004 | `publish/ui-004-hero-no-cta.spec.js` | Publish | no | blocked_harness_defect | Hero action-container/action-link confirmed CSS `display:none` (by design) — consistent |
| UI-005 | `publish/ui-005-intro-typography.spec.js` | Publish | no | blocked_harness_defect | `.cmp-text--intro-lead` present in DOM and in clientlib CSS (4 occurrences) |
| UI-006 | `publish/ui-006-card-grid-desktop.spec.js` | Publish | no | blocked_harness_defect | 2×2 grid confirmed in clientlib CSS (`grid-template-columns:repeat(2,1fr)`) and screenshot |
| UI-007 | `publish/ui-007-card-grid-tablet.spec.js` | Publish | no | blocked_harness_defect | Not captured at tablet width this dispatch |
| UI-008 | `publish/ui-008-card-grid-mobile.spec.js` | Publish | no | blocked_harness_defect | 1-col stack confirmed in clientlib CSS (`@media(max-width:767px){grid-template-columns:1fr}`) |
| UI-009 | `publish/ui-009-card-cta-navigation.spec.js` | Publish | no | blocked_harness_defect | **Spec requires `.cmp-teaser__action-link` visible+href — 0/4 cards render this element at all** (raw "Learn More" text, no anchor); this spec would deterministically fail. See F-LINK-01 |
| UI-010 | `publish/ui-010-header-desktop.spec.js` | Publish | no | blocked_harness_defect | **Spec asserts `page.locator('header').toHaveCount(1)` — DOM has 2 `<header>` elements**; would deterministically fail. See F-LANDMARK-01 |
| UI-011 | `publish/ui-011-header-mobile-collapse.spec.js` | Publish | no | blocked_harness_defect | Menu-toggle button + `data-cmp-hook-site-header="menuToggle"` present in DOM |
| UI-012 | `publish/ui-012-footer-desktop.spec.js` | Publish | no | blocked_harness_defect | **Spec asserts `page.locator('footer').toHaveCount(1)` — DOM has 2 `<footer>` elements**; would deterministically fail. See F-LANDMARK-01 |
| UI-013 | `publish/ui-013-footer-mobile-stack.spec.js` | Publish | no | blocked_harness_defect | 1-col footer stack confirmed in clientlib CSS |
| UI-014 | `publish/ui-014-landmarks.spec.js` | Publish | no | blocked_harness_defect | Duplicate banner/contentinfo landmarks confirmed via Sentinel's own axe sweep (5 moderate violations) — likely fails |
| UI-015 | `publish/ui-015-a11y-scan.spec.js` | Publish | no | blocked_harness_defect | Sentinel ran an equivalent **full-ruleset** axe sweep standalone (not this exact in-spec assertion) — see A11y section. Critical/serious count at mobile viewport: 1 serious (color-contrast) |
| UI-016 | `publish/ui-016-keyboard-focus.spec.js` | Publish | no | blocked_harness_defect | Tab order would not reach header utility links / card actions — those render as non-focusable `<img>`/text with no anchor. Likely fails. See F-LINK-01 |
| UI-017 | `author/ui-017-site-header-dialog-roundtrip.spec.js` | Author | no | blocked_harness_defect | Sentinel independently performed an equivalent edit round-trip via authenticated `curl` (Sling POST + JCR read-back + render re-check) — see AUTH-016 below; genuinely proves the underlying capability this spec targets, but is not this spec executing |
| UI-018 | `author/ui-018-site-footer-dialog-roundtrip.spec.js` | Author | no | blocked_harness_defect | Not independently exercised (Sentinel's round-trip check targeted site-header only) |

**executed = 0, total = total_from_file = 18, blocked = 18, pass = 0, fail = 0, na = 0.**
Track reported `incomplete` in the handoff — not `pass`, per contract (never report pass with unexecuted IDs).

---

## AUTH-001..AUTH-020 (`design/authoring-test-cases.md`) — Author-tier verification

Executed via: model/type introspection (`_cq_dialog.infinity.json` reads), node read-back
(`.infinity.json` on the XF master `jcr:content` subtrees and the card-grid container), a genuine
authoring-API round-trip (Sling POST servlet write → JCR read-back → author-render re-check → restore),
and `curl` probes against both Author and Publish. All via `Authorization: Bearer $AEM_AUTHOR_BEARER_TOKEN`
read at runtime from the repo-root `.env`; the token value is never printed or written to any artifact.

| ID | Result | Evidence |
|---|---|---|
| AUTH-001 | pass | `apps/realmac/components/site-header/_cq_dialog.infinity.json` fetched live: logoFileReference (fileupload, required), logoAlt (textfield, required), logoLinkURL (pathfield, optional), navigationRoot (pathfield, required), navigationStructureDepth (numberfield, required), utilityLinks (multifield, composite=true) with nested label/iconFileReference/linkURL/ariaLabel all required — matches `dialog-specifications.md` field table exactly, nothing dropped |
| AUTH-002 | pass | `apps/realmac/components/site-footer/_cq_dialog.infinity.json` fetched live: columns (composite multifield) → heading (required) + nested links (composite multifield) → label/url (required); socialLinks (composite multifield) → iconFileReference/url/label (required); footerLogoFileReference (fileupload, optional); legalText (textfield, required) — matches spec exactly |
| AUTH-003 | pass | Header master XF `jcr:content` read-back: `utilityLinks` has 2 genuine child nodes `item0`/`item1`, each with its own label/iconFileReference/linkURL/ariaLabel properties (not a comma-joined string) |
| AUTH-004 | pass | Footer master XF read-back: `columns` has 4 genuine child nodes `item0..item3` (Company/Innovation/Resources/Legal) |
| AUTH-005 | pass | Nested `columns/item*/links` each have their own independent child-node sets (item0: 3 links, item1: 2, item2: 2, item3: 2) — nested multifield add/remove is independent per column, confirmed structurally |
| AUTH-006 | pass | `socialLinks` has 4 genuine child nodes (Facebook/LinkedIn/Instagram/Contact) |
| AUTH-007 | pass (structural) | `required="{Boolean}true"` confirmed present in the live dialog JSON for all 4 named fields — this is the mechanism that drives Coral's client-side save-block. A live dialog save-attempt was not independently driven (no authenticated Playwright/browser session was available this dispatch — see the harness defect above); depth is dialog-schema verification, not a live UI interaction |
| AUTH-008 | pass (structural) | Same method: `legalText` required=true; each `columns` item's `heading` required=true; each `links` item's `label`+`url` required=true, confirmed in live dialog JSON |
| AUTH-009 | pass (structural) | `utilityLinks` item's `label`/`iconFileReference`/`linkURL`/`ariaLabel` all required=true together, confirmed in live dialog JSON |
| AUTH-010 | pass | All 13 assets confirmed present under `/content/dam/realmac/tata-innovation/` via Author `.1.json`; each asset's `_jcr_content/renditions/original` read back with non-zero byte size (1048–280432 bytes across the 13); each of the 13 asset paths also resolves 200 on Publish |
| AUTH-011 | na | Per `authoring-test-cases.md`'s own stated rationale: this run's authored values (template paths, DAM paths, URLs) contain no literal commas, and none of site-header/site-footer's list fields are bracket-notation `String[]` (they are composite multifields stored as child nodes) — the hazard is structurally absent from this run. No `cq:allowedTemplates` override was added either. Confirmed unchanged from design-time reasoning |
| AUTH-012 | pass | Header logoFileReference (`tata-logo.svg`) and both utilityLinks iconFileReference (`search.svg`, `ContactUs.svg`) resolve to real, seeded DAM assets (confirmed in AUTH-010's asset list) |
| AUTH-013 | pass | Footer socialLinks iconFileReference (`FB.svg`, `Linkedin.svg`, `Instagram.svg`, `ContactUs.svg`) all resolve; `footerLogoFileReference` correctly absent (optional, not authored) |
| AUTH-014 | pass | Hero `fileReference`=`about_innovation_banner_desktop_1920x1080.jpg`; card_0..3 `fileReference` = `TataChemicals_Desk.jpg`, `TataSteelEurope_Desk.jpg`, `tcsinnovation_information_desktop_360x260.jpg`, `TMETC_Desk.jpg` — all resolve to seeded DAM assets |
| AUTH-015 | pass (carried forward) | Genuine `mode="replace"` filter update-semantics already independently verified by the Program Agent + Auditron across DECISIONS.md Stage 08/09 (CQ-04 fix). Sentinel did not independently re-trigger a redeploy against the real environment this dispatch — redeploying is Cloud Manager's pipeline, outside Sentinel's own execution scope; citing the prior independent verification rather than re-asserting untested |
| AUTH-016 | pass | **Genuine live edit round-trip performed by Sentinel this dispatch**: POST `logoAlt=Sentinel-Roundtrip-<ts>` to the header master XF's `site-header` node via Sling POST servlet (bearer token) → JCR read-back confirmed new value → Author-tier render of the XF confirmed `alt="Sentinel-Roundtrip-<ts>"` on the rendered `<img>` → value restored to `Realmac` and re-verified |
| AUTH-017 | pass | Sample page (200), all 13 DAM assets (200), and both XF masters (200) all resolve live on Publish |
| AUTH-018 | na | No empty-required-field content instance exists in the deployed content this run (Composer authored fully-populated instances only) — same disclosed fixture gap Auditron recorded for TC-003/005/009 (unchanged since iteration 2, DECISIONS.md); Sentinel does not author new test fixtures (verification-only agent) |
| AUTH-019 | pass | `logoLinkURL` omitted → header logo `<a>` resolves to `/content/realmac/us/en.html` (site root fallback, confirmed in rendered DOM); `footerLogoFileReference` omitted → no footer `<img>` logo element rendered at all (confirmed absent in DOM) |
| AUTH-020 | na | No authored empty-list instance (`utilityLinks=[]`/`columns=[]`/`socialLinks=[]`) exists in deployed content — same disclosed fixture gap as TC-014/020/025 (unchanged since iteration 2) |

**executed = 20, total = total_from_file = 20, pass = 17, na = 3, fail = 0, blocked = 0.**

---

## Functional-TC ledger (`design/functional-test-cases.md`) — attribution cross-check only

Not re-executed by Sentinel wholesale. Per contract, TCs Auditron **demonstrably executed with evidence**
in its own handoff are `auditron_owned`; the rest are `sentinel_owned`. Auditron's iteration-5 handoff
(`handoffs/auditron.yaml`) names, with evidence, 40 of 46 IDs (38 pass + 2 `accepted_known_gap`: TC-012,
TC-015, citing DECISIONS.md 2026-08-28T20:50Z for CQ-09) — independently re-verified present and
internally consistent (own recount: 38+2=40). The remaining 6 (TC-003, TC-005, TC-009, TC-014, TC-020,
TC-025) are `blocked` in Auditron's own ledger — no dedicated empty/edge-state fixture was authored this
run for any of them. Sentinel does not author test fixtures (verification-only), so these remain `na` here
for the same, disclosed reason (matching AUTH-018/AUTH-020 above).

`total_from_file = 46` (independently re-confirmed by Sentinel's own `grep` census). `auditron_owned = 40`,
`sentinel_owned = 6` (na, fixture gap). `40 + 6 = 46`.

No functional TC required re-execution by Sentinel this dispatch — none of Auditron's passing IDs
concerned the NEW findings Sentinel surfaced this stage (dangling links, duplicate landmarks, mobile
contrast, harness auth defect) because those are UI-rendering-integration-level defects outside
`functional-test-cases.md`'s scope (that artifact tests Sling Model / HTL behavior in isolation, not
cross-component page composition) — see `sentinel-report.md` for the new findings' own evidence trail.
