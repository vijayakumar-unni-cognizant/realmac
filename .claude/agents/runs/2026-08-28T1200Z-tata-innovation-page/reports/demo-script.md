# Demo Script — Tata Innovation Landing Page (realmac)

**Status: LIVE on the real environment.** Run closed as `fail (accepted-gap)` — the page is
demoable now, with several known, disclosed gaps to call out honestly during the walkthrough.

**PR (merged):** https://github.com/vijayakumar-unni-cognizant/realmac/pull/1
**Live Publish URL:** `https://publish-p185256-e1945105.adobeaemcloud.com/content/realmac/us/en/innovation.html`
**Live Author URL:** `https://author-p185256-e1945105.adobeaemcloud.com` (bearer-token auth)

---

## 1. Open with the reference

Show https://www.tata.com/about-us/innovation for 10 seconds — this is the visual target. Call
out: header/nav, full-bleed hero with overlaid title, intro text block, 4-card showcase grid, dark
footer with social row.

## 2. Load the live deployed page

Navigate to the live Publish URL above. Point out, top to bottom:
- **Header** — logo, primary nav, Search + Contact utility icons. Resize below 768px to show the
  mobile menu collapse. *(Honest note: the utility-link icons themselves don't currently link
  anywhere — see F-LINK-01 below.)*
- **Hero** — full-bleed banner with the "Innovation" title overlaid on a dark scrim. Exactly one
  `<h1>` on the page, no CTA button (by design). *(Honest note: on a phone-width viewport, the
  title's contrast against the image is measurably below WCAG AA — F-A11Y-01, known gap.)*
- **Intro text** — the lead paragraph renders larger than the body copy automatically.
- **Showcase grid** — 4 cards. *(Two honest notes here: (1) cards render as `<h2>` rather than
  `<h3>` and without the dedicated card-style border/shadow — CQ-09, accepted; (2) none of the 4
  "Learn More" links actually go anywhere yet — F-LINK-01, accepted, because their target pages
  were never authored as part of this demo run.)*
- **Footer** — link columns, social icons (Facebook/LinkedIn/Instagram work; Contact doesn't yet),
  neutral realmac copyright line.

## 3. Open the page in the AEM Editor (Author)

Show the authoring experience: drag a 5th Teaser into the card-grid container, demonstrate its
Style System auto-scoping to "Innovation Card." Open `site-header`'s dialog on its Experience
Fragment to show the Logo / Navigation / Utility Links tabs.

## 4. Be upfront about what's not verified yet

- **The 18 Playwright specs have never successfully run against any environment** — a harness
  defect (F-HARNESS-01) means the test run aborts before any spec executes. This is not "18 tests
  passed," it's "18 tests unexecuted." Say this plainly if asked about test coverage.
- **Header and footer each render a duplicate semantic landmark** (F-LANDMARK-01) — cosmetically
  invisible, but a real structural/accessibility defect, confirmed live.
- **LCP measures 3015ms** against a 2500ms target (F-PERF-01) — the hero image loads via a
  JS-hydrated pattern rather than eager-loading.

## 5. Close with the honest status

- **PR #1 is merged and live** on a real AEMaaCS environment.
- **Sentinel (the final verification stage) returned FAIL** — 6 new findings plus 1 carried-forward
  gap (CQ-09), all reviewed and formally accepted as known gaps by the human decision-maker rather
  than remediated this run.
- **This run's terminal verdict is `fail (accepted-gap)`** — not "pass," by design, per this
  project's own ADLC contract (a Sentinel failure that gets accepted rather than fixed never
  upgrades to a pass verdict).
- A prioritized follow-up list exists in `reports/final-report.md` §4 for whoever picks this back
  up: fix the Playwright harness first (it's the highest-leverage item — it unblocks verifying
  everything else), then the dangling links, then the landmark duplication, then mobile contrast,
  then LCP.

## Numbers worth citing live
- 5 Auditron Build Gate iterations to reach a green pre-deploy gate.
- 15 total specialist dispatch stages, 8 different specialists, across this run.
- 3 net-new components (site-header, site-footer, innovation-card) against 4 reused Core Component
  Style System variants.
- 13 DAM assets live, all with real binaries + renditions.
- Lighthouse: Performance 93, Accessibility 95, Best Practices 96, SEO 92 (LCP is the one failing
  sub-metric within the Performance category).
- 0/18 Playwright specs executed (harness defect, disclosed, not silently omitted).
