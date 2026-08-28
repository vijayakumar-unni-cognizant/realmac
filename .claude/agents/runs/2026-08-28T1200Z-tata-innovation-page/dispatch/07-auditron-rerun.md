agent: auditron
stage: Test (pre-release) — Build Validation Gate re-run, iteration 2
run-id: 2026-08-28T1200Z-tata-innovation-page
mvn-budget: FRESH 2-call budget granted by the human for this dispatch (see DECISIONS.md "Q2 — mvn
  budget RESOLVED"). This is a new grant for iteration 2, not additive to the 4 calls already spent
  in iteration 1. Stay within 2 calls this time — see the explicit instruction below on how.

input-packet: |
  You are `auditron` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  This is a RE-RUN of the Build Validation Gate (iteration 2) after Blockwright's remediation of
  CQ-01 and CQ-02. Read `DECISIONS.md` in full before starting — it contains independently-verified
  detail on everything below; do not re-derive what is already settled there.

  ## What changed since your last dispatch
    - CQ-01 FIXED: `site-footer.html`'s `data-sly-list` → `data-sly-repeat` on both `<nav>` (columns)
      and `<li>` (links). Independently verified by the Program Agent.
    - CQ-02 FIXED via Option B (no pom edit): `SiteHeaderModel.java` now exposes
      `getNavigationResource()` — a `ResourceWrapper` whose `ValueMap` already carries
      `navigationRoot`/`structureDepth`/`sling:resourceType` — and `site-header.html`'s embedded
      navigation is now a plain `data-sly-resource="${model.navigationResource}"` with zero custom
      `@`-options. The htl-maven-plugin validation trigger is gone entirely; `ui.apps/pom.xml`'s
      `allowedExpressionOptions` was NOT touched (still exactly `cssClassName`, `decoration`,
      `decorationTagName`, `wcmmode`). Independently verified by the Program Agent.
    - CQ-03 RESOLVED (human decision, NOT a code change): the 3 pre-existing pom.xml edits (root
      `pom.xml` frontend-plugin skip; `ui.frontend/pom.xml` `skipAssembly=true`;
      `ui.apps/pom.xml`'s commented-out `realmac.ui.frontend` zip dependency) are CONFIRMED
      INTENTIONAL, human-authored, LOCAL-BUILD-ONLY workarounds for a Zscaler-restricted environment
      that blocks the frontend-maven-plugin's node/npm download at build time. The human manually
      runs `npm install`/webpack and syncs the compiled clientlib into `ui.apps` out-of-band. These
      are **NOT a defect** — treat them as an accepted local constraint, not a build failure or a
      fidelity gap. Do NOT flag them as a new finding; do NOT ask Blockwright or anyone to revert
      them. (They remain excluded from the eventual PR — that is Pilot's job later, not yours.)

  ## Critical instruction: how the CQ-03 constraint changes your Build Gate interpretation
  Because `ui.frontend`'s reactor build is skipped and `ui.apps` no longer depends on its output zip,
  your `mvn -q clean install -PautoInstallSinglePackage` run will NOT rebuild or repackage the
  `ui.frontend` clientlib from source this dispatch. This is EXPECTED and ACCEPTED, not a regression
  to chase. Concretely:
    - Do NOT treat an unchanged/stale/absent freshly-built clientlib.css as a build failure.
    - DO still run and pass everything else the reactor normally validates for the modules that ARE
      wired into the reactor (core compile + unit tests, ui.apps HTL validation + packaging,
      ui.content packaging, ui.config, the `all` package assembly, and any integration-test module
      you judge adds real coverage).
    - For frontend correctness, DO run a syntax-only validation of the new/changed SCSS partials
      (e.g. `npx sass --no-source-map <partial-path> /dev/null`-style dry compiles, one per new/
      changed partial, run directly via `npx` — NOT a full `npm install`/webpack build, which is
      exactly what is Zscaler-blocked in this environment). This confirms the SCSS is syntactically
      valid without needing the blocked network path.
    - Explicitly RECORD in your handoff that full clientlib/visual verification (does the compiled
      CSS actually reach the browser, does it visually match the reference) is DEFERRED to (a) Cloud
      Manager's own frontend build in the eventual pipeline run, which uses pristine, unmodified poms
      with real network access, and (b) Sentinel's post-deploy Visual Verification Tier A against the
      real environment. State this as accepted deferred scope, not as an open failure.

  ## mvn budget discipline (fresh 2-call grant — do not repeat the prior overage)
  Last dispatch you used 4 mvn invocations (1 official + 3 diagnostic re-runs to extract `-q`-
  suppressed warning text). This time: run the Build Gate ONCE, WITHOUT `-q`, and redirect full
  stdout+stderr to a log file in the SAME invocation (e.g.
  `mvn clean install -PautoInstallSinglePackage > /tmp/aem-build-iter2.log 2>&1`) so you have the
  complete, unsuppressed output from a single process — no need for a second diagnostic pass just to
  see warnings. Reserve your 2nd mvn call for an actual second purpose (e.g. a dedicated
  `it.tests` verify run) if and only if you judge it adds real coverage; do not spend it on
  re-diagnosing the same command. If you find yourself wanting a 3rd invocation for any reason, STOP
  and escalate to the Program Agent for a budget decision (§P4) rather than silently running it.

  ## Required: re-attribute the 25 previously-blocked functional test cases
  Last dispatch's ledger: 21 auditron_executed + 0 deferred_to_sentinel + 25 blocked = 46
  (`test/auditron/coverage.md`). All 25 were blocked with the single named reason "page not deployed
  (Build Gate failure)" or "template never installed this dispatch." Now that the Build Gate should
  reach a real BUILD SUCCESS and install content to your local SDK:
    - Re-attempt every one of the 25 previously-blocked IDs (TC-001..007, TC-009..012, TC-014..022,
      TC-024, TC-025, TC-027, TC-031, TC-034) against your fresh local install. Curl the rendered
      `/content/realmac/us/en/innovation.html` and the local Create Page wizard data
      (`/content/realmac/us/en.json` or equivalent) as needed per each case's original steps in
      `design/functional-test-cases.md`.
    - Any ID that discharges cleanly, mark `auditron_executed` with fresh evidence (a new curl
      excerpt/computed-style check, not a citation of the prior blocked reason).
    - If any ID is STILL genuinely blocked after a real build success (e.g. a case needing a fixture
      you have not authored, per TC-003/TC-005/TC-009/TC-014/TC-020/TC-025's original "requires a
      dedicated test fixture" language), keep it `blocked` with its own specific, still-accurate
      reason — do not silently mark it executed if the actual precondition still isn't met.
    - The total MUST still equal 46 (re-verify by your own grep of `design/functional-test-cases.md`,
      do not assume last dispatch's count is still correct without re-checking).

  ## Re-confirm the fixes are effective + CQ-03 remains untouched
    - Confirm CQ-01: `site-footer.html` renders N separate `<nav>`/`<li>` elements for N columns/
      links once content is authored (your existing sample page at
      `/content/realmac/us/en/innovation`'s footer master XF already has 4 columns + 4 socialLinks —
      use it as the fixture; do not author a new one).
    - Confirm CQ-02: the Build Gate's htl-maven-plugin validation step no longer fails on
      `site-header.html`.
    - Confirm CQ-03: `git diff --stat` on `pom.xml`, `ui.apps/pom.xml`, `ui.frontend/pom.xml` is
      IDENTICAL to the pre-remediation state (no new hunks) — this is a quick sanity check, not a
      new investigation.

  ## Standard responsibilities (unchanged from your original dispatch — restated briefly)
    - 3-signal build-success detection (exit code, `all` zip presence + freshly-produced-this-run
      size, surefire summary) per §P2 — all three, not one.
    - Zero severity ≥ high findings, or each accepted in DECISIONS.md.
    - `test/auditron/coverage.md` ledger: total==46, buckets sum to 46, by-ID evidence.
    - Do NOT execute Playwright (Sentinel's job) — static discovery only, if you touch it at all.

  ## Outputs required from you
    - Updated `runs/2026-08-28T1200Z-tata-innovation-page/test/auditron/code-quality-report.md`
    - Updated `runs/2026-08-28T1200Z-tata-innovation-page/test/auditron/test-report.md`
    - Updated `runs/2026-08-28T1200Z-tata-innovation-page/test/auditron/coverage.md`
    - Updated `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/auditron.yaml` (new `status`,
      updated `mvn_invocations` — expect 1 or 2, a fresh `build_gate` block, a fresh
      `functional_test_cases` attribution block, and an explicit CQ-03-handling note as described
      above)

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\auditron.yaml

gate-criteria: |
  - 3-signal BUILD SUCCESS (exit code 0; all-zip present, freshly produced this run, plausible size;
    surefire all-pass across every module that reached test execution).
  - Zero severity >= high findings remaining (CQ-01/CQ-02 confirmed fixed; CQ-03 confirmed accepted,
    not re-raised as a finding).
  - functional_test_cases ledger: total == 46 (re-verified by grep), buckets sum to 46, each
    previously-blocked ID re-attempted with a fresh, specific result (executed or still-blocked with
    a named precondition — no silent carry-forward of the old blocked reason without re-checking).
  - At most 2 mvn invocations this dispatch, each with a stated purpose.
  - CQ-03 files (pom.xml, ui.apps/pom.xml, ui.frontend/pom.xml) confirmed untouched by this dispatch
    (git diff --stat identical to pre-remediation state).
  - No Playwright execution.
  - handoffs/auditron.yaml present with an explicit CQ-03-acceptance note (not flagged as a new
    finding or failure).
