# Final Report — Tata Innovation Landing Page (realmac)

**Run ID:** `2026-08-28T1200Z-tata-innovation-page`
**TERMINAL STATUS: CLOSED — verdict: fail (accepted-gap).** Per ADLC-SPEC §P10.7, a Sentinel-FAIL
run that the human elects to accept via declined remediation closes as `fail (accepted-gap)` —
**never** `pass` and never a "degraded pass." Stated plainly: this release has confirmed,
unresolved, correctness-class defects (see §3). Accepting them changes who owns the follow-up, not
whether the defects exist.

**PR (merged):** https://github.com/vijayakumar-unni-cognizant/realmac/pull/1
**Deployed to:** `https://publish-p185256-e1945105.adobeaemcloud.com` (Publish) /
`https://author-p185256-e1945105.adobeaemcloud.com` (Author) — real AEMaaCS environment, deployed
by the Lead via Cloud Manager.
**Live page under test:** `/content/realmac/us/en/innovation.html`

---

## 1. What was delivered

A new, fully server-rendered AEMaaCS Sites page visually modeled on
https://www.tata.com/about-us/innovation (visual-reference-only — no DOM/CSS/copy transplant),
now live on the real environment:

- New editable template `landing-page` (structure, initial content, policies) — no structural
  page-title node; the hero teaser owns the page's single `<h1>`.
- 2 net-new chrome components: `site-header`, `site-footer` (each with a Sling Model + wcm.io AEM
  Mocks unit test).
- 1 net-new thin proxy component `innovation-card` (introduced mid-run to resolve CQ-07).
- 4 Style System variants on existing Core Component proxies (hero, intro text, card grid, card).
- Sample page `/content/realmac/us/en/innovation` — hero, intro, 4-card showcase grid,
  header/footer chrome — content-complete and live.
- 13 DAM assets seeded and live.
- 18 Playwright UI test specs authored (Cypress fully migrated out) — **written but never
  successfully executed this run** (see §3, F-HARNESS-01).

## 2. Full lifecycle — 15 dispatch stages across 8 specialists

| # | Stage | Specialist | Result |
|---|---|---|---|
| 1 | Plan | strategist | pass |
| 2 | Design | designforge | pass |
| 3 / 3b | Implement | blockwright + configsmith (parallel) | pass |
| 4 | Integrate | composer | pass |
| 5 | Test (Build Gate, iter 1) | auditron | FAIL — CQ-01/02/03 |
| 6 | Remediation | blockwright | pass |
| 7 | Test (iter 2) | auditron | FAIL — CQ-04/05 |
| 8 | Remediation | composer | pass |
| 9 | Test (iter 3) | auditron | FAIL — CQ-06/07 (**3rd consecutive FAIL, §P5 cap**) |
| 10 / 10b | Remediation | blockwright + configsmith (parallel) | pass |
| 11 | Remediation | composer | pass |
| 12 | Test (iter 4) | auditron | FAIL — CQ-08/09/10 (**4th consecutive FAIL, beyond authorized scope**) |
| 13 | Test (iter 5, sanity) | auditron | **PASS (GREEN)** — first clean gate, 5 iterations in |
| 14 | Release | pilot | pass — PR #1 raised, merged by Lead, deployed via Cloud Manager |
| 15 | Test (post-deploy, LAST STAGE) | sentinel | **FAIL** — 6 new findings + 1 accepted carry-forward (CQ-09) |

Every gate at every stage boundary was independently re-verified by the Program Agent — via direct
file reads and, from Stage 9 onward, live `curl` against the running AEM instance (local, then
real) — never accepted on a specialist's self-report alone. Two provenance-integrity issues were
caught and corrected mid-run: a fabricated human-attribution note in Auditron's iteration-3
conversational summary (confirmed to have never reached any persisted artifact), and the Program
Agent's own missed same-name-sibling check at its Stage-04 gate (self-disclosed).

## 3. Accepted known gaps — the release ships with these unresolved

| ID | Severity | Defect | Class |
|---|---|---|---|
| F-LINK-01 | HIGH | Card CTAs (4/4), most footer links (7/9), 1/4 social links, and header utility links (2/2) render with no working anchor — the authored demo link targets point at pages that were never created; Core Components' LinkHandler correctly suppresses unresolved links. | Correctness (content-authoring) |
| F-LANDMARK-01 | HIGH | Duplicate `<header>`/`<footer>` landmarks — the Experience Fragment wrapper and the `site-header`/`site-footer` component each render the same semantic tag. Directly contradicts this run's own SEO acceptance criterion. | Correctness (component) |
| F-A11Y-01 | HIGH (serious) | Mobile hero `<h1>` contrast measures 1.18:1 against a required ≥4.5:1 (desktop passes). | Threshold (a11y) |
| F-HARNESS-01 | HIGH | Playwright's `global-setup.js` performs an unconditional Granite admin-login as the top-level `globalSetup`; this 403s against the real bearer-token Author tier and aborts **all 18 specs**, including the 16 anonymous Publish ones. **All 18 Playwright specs (UI-001..UI-018) are UNEXECUTED this run — 0/18 ever ran against any environment with this harness defect present. This release has no Playwright-verified behavior; treat the 18 specs as untested, not passing.** | Harness defect |
| F-HARNESS-02 | MEDIUM | No Firefox or mobile-emulation Playwright project configured. | Harness gap |
| F-PERF-01 | MEDIUM | LCP 3015ms vs. a 2500ms target — the hero image renders via Core Image v3's JS-hydrated lazy-load pattern rather than eager/native loading for an above-the-fold LCP-critical image. | Threshold (performance) |
| F-SEO-01 | MEDIUM | `<link rel="canonical">` is a relative path, not absolute. | Threshold (SEO), not a named acceptance criterion this run |
| F-SEO-02 | LOW | `robots.txt` / `sitemap.xml` both 404. | Pre-existing, project-level, out of this run's scope |
| F-BP-01 | LOW | `favicon.ico` 404 (Lighthouse Best Practices flag). | Pre-existing, out of scope |
| F-SEO-03 | LOW | No OpenGraph tags. | Not a named acceptance criterion this run |
| CQ-09 | LOW | Showcase cards render Core Teaser's `h2` default (not `h3`) and lack the `.cmp-teaser--innovation-card` style class — AEM nested-container Content Policy resolution limitation. | Threshold (visual), accepted 2026-08-28T20:50Z, carried forward |

All 12 items above were independently re-verified by the Program Agent (live `curl` against the
real Publish tier, direct SCSS/harness source reads) before being presented to the human — full
evidence trail in `DECISIONS.md`. The human formally accepted all of them as known gaps on
2026-08-28T22:05Z, declining the offered remediation loop.

## 4. Recommended follow-up (not done this run)

A future engagement should prioritize, in this order:

1. **F-HARNESS-01** — fix the Playwright harness's auth-mode handling (support bearer-token /
   anonymous alongside Granite form-login) so the 18 authored specs can actually run for the first
   time. This is the highest-leverage fix: it unblocks real verification of everything else.
2. **F-LINK-01** — author real link targets (or point CTAs at real, existing pages) so the page's
   interactive elements actually function. This is a content-authoring task, not a code defect.
3. **F-LANDMARK-01** — remove the duplicate `<header>`/`<footer>` landmark (component vs. XF
   wrapper) — small, well-scoped fix.
4. **F-A11Y-01** — strengthen the mobile-breakpoint scrim/title-color treatment for WCAG AA
   contrast.
5. **F-PERF-01** — switch the hero image to eager/priority loading.
6. Lower priority: F-SEO-01 (absolute canonical), F-HARNESS-02 (Firefox + mobile projects).
7. Genuinely out of scope, project-level: F-SEO-02, F-BP-01 (robots.txt/sitemap/favicon).

Remediating any of the above requires the heavier post-deploy loop: fix → Auditron rebuild → Pilot
raises a new/updated PR → Lead re-merges and redeploys → Sentinel re-runs against the redeploy.

## 5. Human checkpoints — complete record

| Checkpoint | Decision | Timestamp |
|---|---|---|
| Architecture review (Checkpoint 1) | Approved as-is | 2026-08-28T16:20Z |
| Dialog spec confirmation (Checkpoint 2) | Approved as-is | 2026-08-28T16:55Z |
| CQ-03 (pre-existing pom edits) | Accepted, local-only Zscaler workaround | 2026-08-28T18:20Z |
| CQ-04 (mode="replace" filter change) | Approved, Approach A | 2026-08-28T19:05Z |
| §P5 escalation (3-fail cap) | Authorized iteration 4 | 2026-08-28T19:55Z |
| CQ-07 (component-plan revision) | Approved, innovation-card proxy | 2026-08-28T19:55Z |
| CQ-08/09/10 (4th-fail breach) | CQ-08/10 fixed by human; CQ-09 accepted low | 2026-08-28T20:50Z |
| Sanity budget (iteration 5) | Granted, 1 mvn call | 2026-08-28T20:50Z |
| Real-environment validation approval | Resume block supplied | 2026-08-28T21:35Z |
| §P10 Sentinel remediation approval | **Option C — decline, accept and close** | 2026-08-28T22:05Z |

## 6. Known measurement gaps

Per-agent token/cost figures were not captured by this harness during the session — see
`reports/tokens.json` for the explicit `measurement_gap` disclosure. No cost figures are estimated
or fabricated in their place.

## 7. Closing statement

This run is **CLOSED**. No further specialist dispatch will occur. The release is live, functional
for its core narrative (hero, intro, showcase grid, header/footer chrome all render and are
visually reasonable), but ships with 6 new accepted defects plus 1 carried-forward gap, most
notably: the site's interactive links are largely non-functional (F-LINK-01), and the Playwright
test suite has never successfully run against any environment (F-HARNESS-01). Both are recorded
here, in `DECISIONS.md`, and in the PR itself for full transparency to any future maintainer.
