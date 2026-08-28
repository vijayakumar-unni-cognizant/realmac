agent: sentinel
stage: Test (post-deploy, NFR enforcement) — LAST STAGE of this ADLC run
run-id: 2026-08-28T1200Z-tata-innovation-page

input-packet: |
  You are `sentinel` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`
  in the `realmac` project. The Lead has merged PR #1, deployed to a real AEMaaCS environment, and
  supplied both tier URLs. Read `DECISIONS.md` in full before starting — it contains the complete
  5-iteration Auditron history, the accepted CQ-09 known gap, and the resume block with the exact
  environment details. You are the LAST stage of this ADLC run; your consolidated report is the
  terminal acceptance verdict. No agent runs after you.

  ## Environment targets + authentication (from the DECISIONS.md resume block)
    - **Author URL:** `https://author-p185256-e1945105.adobeaemcloud.com` — auth: Bearer token.
      Read the token from the repo-root `.env` file's `AEM_AUTHOR_BEARER_TOKEN` variable AT RUNTIME
      — do NOT hardcode it, do NOT echo/print it, do NOT write it into any report or handoff. Send
      it as `Authorization: Bearer <token>` on every Author-tier request.
    - **Publish URL:** `https://publish-p185256-e1945105.adobeaemcloud.com` — auth: none
      (anonymous).
    - **Tier mandate (do not mix these up):** authoring-provision / data-setup checks run against
      **Author** (the only tier with an authoring UI). Playwright UI execution, all NFR tracks
      (performance, a11y, SEO, best-practices, observability), and Visual Verification all run
      against **Publish** (the only tier that exercises the CDN + Dispatcher). Never derive one URL
      from the other; both are supplied explicitly.
    - **Sample page under test:** `/content/realmac/us/en/innovation.html` — on Publish for all
      NFR/UI/visual tracks; the Author equivalent for authoring-provision checks.
    - **Build reference:** the merged commit SHA was not separately supplied by the Lead. Record
      whatever build/version identifier the live environment itself exposes (e.g. the AEM version-
      info endpoint, or response headers) as the actual deployed build reference in your report —
      do not fabricate or assume it matches `e260c64` without checking.

  ## Track 1 — Playwright UI execution (Publish, cross-browser + mobile emulation)
  Execute the 18 Playwright specs Blockwright authored under `ui.tests/test-module/tests/`
  (`UI-001` through `UI-018` — you EXECUTE them, you did not author them). They are parameterized
  via env vars: `AEM_AUTHOR_URL`, `AEM_PUBLISH_URL`, `AEM_AUTHOR_BEARER_TOKEN` (all present in
  `.env`) — point them at the real environment via these vars, not hardcoded URLs. Run across
  Chromium, Firefox, and WebKit, plus mobile emulation, per your standard contract. Report
  pass/fail per spec ID, and fold this into the `coverage-matrix.md` you owe against
  `design/ui-test-scenarios.md`'s full UI-001..UI-018 census (execute == total, no sampling).

  ## Track 2 — NFR baseline (Publish)
    - **Performance:** one Lighthouse run covering all 4 categories (Performance — including LCP,
      CLS, TTFB, and clientlib bundle-weight regression check; Accessibility; Best Practices; SEO).
      Targets from `plan/requirements.yaml`: LCP ≤2500ms, INP ≤200ms, CLS ≤0.1.
    - **Accessibility:** deep `@axe-core` full-ruleset sweep (not just critical/serious) against the
      sample page. Target: WCAG 2.1 AA.
    - **SEO:** deep check — `<title>`, meta description, canonical, OpenGraph core tags, JSON-LD
      parseability (if any), `robots.txt`, sitemap presence.
    - **Observability:** baseline check per `plan/requirements.yaml`'s observability scope (this run
      did not introduce any Adobe Launch/Analytics requirement beyond project defaults — confirm
      whatever the project's existing observability baseline is, don't invent a new requirement).

  ## Track 3 — Visual Verification Tier A (Publish, vs. the original reference)
  Reference URL: `https://www.tata.com/about-us/innovation` (visual/design reference only, per
  `plan/reference-deconstruction.md` — layout/typography/spacing fidelity is what's being judged,
  never DOM/CSS/copy equivalence). Capture at the §P8 default viewports: **1440×900 desktop** and
  **390×844 mobile**. Diff against `design/design-token-audit.md`'s pinned tokens and
  `component-specifications.md`'s Pixel-Verified Acceptance Criteria tables, region by region
  (header, hero, intro, card grid, footer).

  ## Track 4 — Headless GraphQL content-parity: N/A
  This run is server-rendered Sites only — no Content Fragment Models, no persisted queries, no
  GraphQL endpoint. Record this track explicitly as `not_applicable` in your report and handoff,
  with the stated reason ("server-rendered Sites page, no CF/GraphQL in scope this run") — do not
  silently omit the section.

  ## Track 5 — Authoring-provisions (Author, bearer token)
  Execute `design/authoring-test-cases.md`'s `AUTH-001` through `AUTH-020` against the real Author
  tier: model→editor field parity for `site-header`/`site-footer`/`innovation-card`'s dialogs;
  multi-value authorability (utility links, footer columns/links, social links — true schema lists,
  not just widget flags); required-field enforcement; data-setup integrity verified by reading the
  stored node back; reference integrity (DAM asset paths resolve); redeploy-update semantics;
  edit round-trip; publish/activation state for the sample page and both XF masters.

  ## CRITICAL — correctly attribute CQ-09 vs. a genuinely new clientlib-CSS finding
  The 4 showcase cards are KNOWN to render Core Teaser's `h2` default (not `h3`) and lack the
  `.cmp-teaser--innovation-card` style class — this is a **human-accepted, low-severity, documented
  known gap** (DECISIONS.md, 2026-08-28T20:50Z), NOT a new blocking failure. If your heading-level /
  visual-fidelity / a11y-heading-order checks observe this specific symptom, cite the DECISIONS.md
  timestamp and classify it `accepted_known_gap`, not `fail`.

  HOWEVER — distinguish this from a DIFFERENT, potentially NEW problem: CQ-03 (the local-only
  Zscaler pom edits that skipped `ui.frontend`'s build) was explicitly LOCAL-ONLY and was correctly
  EXCLUDED from the PR commit (verified by the Program Agent). Cloud Manager's pipeline builds
  `ui.frontend` normally with real network/npm access, so the compiled Style System CSS for
  `cmp-teaser--hero`, `cmp-text--intro-lead`, `cmp-container--card-grid`, `_site-header.scss`, and
  `_site-footer.scss` SHOULD genuinely be present and loading on the real Publish tier. If you find
  the clientlib CSS is missing or those selectors don't resolve AT ALL on Publish (as opposed to
  just the one specific, already-known `.cmp-teaser--innovation-card`/h3 gap), that IS a new,
  real, BLOCKING finding — it would mean Cloud Manager's frontend build did not run as expected.
  Do not conflate the two; report them separately with clearly different severities.

  ## Outputs required from you
    - `runs/2026-08-28T1200Z-tata-innovation-page/test/sentinel/sentinel-report.md` +
      `sentinel-report.html` — ONE consolidated report, one section per track (UI, Performance,
      Best Practices, A11y, SEO, Observability, Authoring-provisions, GraphQL-parity [N/A], Visual).
    - `runs/2026-08-28T1200Z-tata-innovation-page/test/sentinel/coverage-matrix.md` — per-ID
      execution record across `ui-test-scenarios.md` (UI-001..018) and `authoring-test-cases.md`
      (AUTH-001..020), executed == total for both.
    - `runs/2026-08-28T1200Z-tata-innovation-page/test/sentinel/reference-extract-tata-innovation.md`
      — the concrete extraction from the reference URL, making the visual-parity diff auditable.
    - Machine artifacts for drill-down: `lighthouse-innovation.json`, `axe-innovation.json`,
      `screenshots/` (both viewports).
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/sentinel.yaml`

  ## Gate reminders
    - `total` for both `ui-test-scenarios.md` and `authoring-test-cases.md` must be established by
      your own mechanical ID census (grep), never a declared/inherited number.
    - A per-ID `not_applicable` is allowed only with a per-ID reason; a blanket track-level N/A is
      only acceptable for the GraphQL track (genuinely out of scope, stated above) — not for any
      other track.
    - Do not upgrade your own overall `status` from `fail` to `pass` on the strength of the CQ-09
      deferral if you find any OTHER unaccepted correctness defect — CQ-09 is the only pre-accepted
      gap this run; anything else you find is a fresh finding needing its own disposition.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\sentinel.yaml

gate-criteria: |
  - Both tier URLs used correctly per the tier mandate (authoring-provisions -> Author;
    everything else -> Publish); no track run on the wrong tier.
  - Playwright: 18/18 specs executed (not sampled) across Chromium/Firefox/WebKit + mobile.
  - Lighthouse run covers all 4 categories; LCP/INP/CLS measured against stated targets.
  - Deep axe (full ruleset, not just critical/serious) + deep SEO checks performed.
  - Visual Tier A captured at both 1440x900 and 390x844 against the reference URL.
  - GraphQL track explicitly recorded not_applicable with a stated reason.
  - Authoring-provisions: all 20 AUTH-### cases executed against Author.
  - CQ-09 correctly classified accepted_known_gap wherever observed, citing DECISIONS.md; any
    missing/non-resolving clientlib CSS on Publish reported as a SEPARATE, new, blocking finding,
    not conflated with CQ-09.
  - coverage-matrix.md: executed == total_from_file for both ui-test-scenarios.md and
    authoring-test-cases.md, census taken by the agent's own grep.
  - No secret (bearer token) written into any report, handoff, or artifact.
  - handoffs/sentinel.yaml present with a terminal status (pass, or fail with per-finding
    disposition) — this is the run's terminal acceptance verdict.
