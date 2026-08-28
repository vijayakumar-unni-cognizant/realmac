agent: auditron
stage: Test (pre-release) — code quality + Build Validation Gate + unit + integration tests
run-id: 2026-08-28T1200Z-tata-innovation-page

input-packet: |
  You are `auditron` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`
  in the `realmac` AEMaaCS project (repo root `C:\AEM\Repos\realmac`, branch
  `feature/realmac-landing-page`). All 4 code-producing specialists (blockwright, configsmith,
  composer) have completed with status: pass. (bridgesmith was not dispatched this run — no
  external integration in scope.) You are the Test stage gate before Pilot raises the release PR.

  ## Required reading before you start
    - `C:\AEM\Repos\realmac\.aem-skills-config.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/{strategist,designforge,blockwright,configsmith,composer}.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/functional-test-cases.md` (46 cases,
      TC-001..TC-046, ALL `executor: auditron` — you own every single one, none deferred to sentinel)
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/authoring-test-cases.md` (AUTH-001..020 —
      review for any that are settleable pre-deploy against your own local SDK install; the rest are
      Sentinel's, deferred)
    - `runs/2026-08-28T1200Z-tata-innovation-page/DECISIONS.md` — read the full file; it contains the
      independently-verified state of every prior stage and the specific carry-forward items below

  ## Carry-forward items from prior stages — you MUST address every one of these explicitly in your
  handoff (do not silently drop any):

  1. **Re-verify Configsmith's security review against actual code.** Configsmith's security review
     (0 high / 0 medium / 4 low findings, all accepted) was performed against Designforge's design
     artifacts, BEFORE Blockwright's `site-header`/`site-footer` components + Sling Models existed on
     disk. That code is now on disk. Re-run the relevant security checks (pathfield rootPath scoping,
     no stored-XSS surface on `legalText`/textfields, `allowUpload=false` on fileupload fields, no
     service-user/ACL/repoinit need) against the ACTUAL committed code, not just re-cite Configsmith's
     design-time review. Record your own independent verdict per finding (L1-L4) in your handoff.

  2. **Pre-existing `<mysite>` vs `<realmac>` naming oddity** in the `xf-web-variation` template's XF
     Root policy resourceType-fallback segment (`templates/xf-web-variation/policies/.content.xml`).
     Zero current functional impact (confirmed by both Configsmith and the Program Agent — the only
     nested resource under `site-header`, the embedded Navigation, is a synthetic HTL resource, not
     policy-resolved). Record as an observed pre-existing item in your report for future-hardening
     awareness — do NOT remediate it this run (out of scope, human-approved via the run's design gate).

  3. **Confirm the `filter.xml` addition** — Composer added
     `<filter root="/content/dam/realmac/tata-innovation" mode="merge"/>` to
     `ui.content/src/main/content/META-INF/vault/filter.xml` (a new, separate filter root, matching
     the existing project convention for the sibling `/content/dam/realmac/asset.jpg` filter, made
     necessary because the parent `/content/dam/realmac` filter otherwise excludes everything except
     its own `jcr:content`). The Program Agent independently read-verified this is necessary +
     sufficient for the DAM path, and that NO additional filter change was needed for the new sample
     page or the two XF master fragments (they're already covered by the existing broad
     `/content/realmac` and `/content/experience-fragments/realmac` filter roots). **Your job:**
     prove this with your actual Build Validation Gate run — confirm the `all` package assembles with
     all 13 DAM assets + the new page + the amended header/footer XF content actually present in the
     built package (not just filtered-in on paper).

  4. **Live template-registration confirmation.** Composer verified `landing-page`'s
     `cq:allowedTemplates` coverage via static repo inspection only (no running AEM instance was
     available to it) — result: `pass_no_override_needed`. Now that YOUR Build Validation Gate
     installs to a local AEM SDK, confirm this live: after `mvn -PautoInstallSinglePackage`, hit the
     local author instance's Create Page wizard data (or the equivalent `wcm/core/content/sites`
     JSON/servlet call) at `/content/realmac/us/en` and confirm `landing-page` is genuinely offered.
     If it is not, that is a build-validation-gate FAIL, not a Sentinel deferral — Sentinel only
     re-confirms this against the real environment later; it does not discover it for the first time.

  5. **C11/C4/C7/C13 static content checks** against the newly authored content: all 13 DAM assets
     (binary + rendition presence, no bare `dam:Asset`), the header/footer master XF content (exactly
     one `site-header`/`site-footer` instance each, no orphaned legacy nodes), and the sample page
     (`/content/realmac/us/en/innovation` — correct template reference, correct authoring depth,
     resolving `cq:styleIds` against the actual installed policy nodes).

  6. **Also note (non-blocking, no action needed from you):** an apparently-unused, empty
     `_jcr_content/` directory exists on disk alongside
     `.../site/header/master/.content.xml` (whose `jcr:content` subtree is already fully
     inline-serialized in that XML file). Flag in a dead-file/orphan-file scan note if your review
     tooling catches it; not a build or policy issue.

  ## Standard Auditron responsibilities (per ADLC-SPEC §4.7 / auditron.md — you know these; stated
  here only for run-specific emphasis)

  - **Review track (no mvn):** changed-file inventory across all 3 code-producing handoffs
    (blockwright, configsmith, composer — bridgesmith N/A this run); run the `review` skill at run
    level; cross-file consistency checks — HTL `data-sly-use` ↔ Sling Model accessor names (verify
    `SiteHeaderModel`/`SiteFooterModel` accessor names EXACTLY match `handoffs/designforge.yaml §
    components_specified.accessors`), BEM ↔ SCSS (verify `.cmp-site-header`/`.cmp-site-footer` classes
    referenced in HTL match the SCSS partials Blockwright authored), policy allowlist ↔ actual
    components (verify every `realmac/components/*` name in the 6 new policies resolves to a real
    component on disk), `cq:allowedTemplates` regex ↔ actual template (item 4 above), dialog fields ↔
    `@ValueMapValue`/`@ChildResource` (verify every JCR property name in
    `dialog-specifications.md` has a matching Sling Model accessor), no orphan files (item 6 above),
    Composer-seeded pages resolve. Lint (eslint/tsc if applicable to the new SCSS/JS). Dead-code/TODO
    scan.
  - **Build Gate (mvn call #1 of 2):** `mvn -q clean install -PautoInstallSinglePackage` — compiles,
    runs unit tests, packages, deploys to local AEM SDK. Apply the 3-signal build-success detection
    (exit code, `all` zip presence + size, surefire summary) per ADLC-SPEC §P2 — do not gate on any
    single signal alone. Record `build_hash` for Pilot.
  - **Tests (mvn call #2 of 2, if needed for integration):** confirm `SiteHeaderModelTest.java` /
    `SiteFooterModelTest.java` (wcm.io AEM Mocks) actually pass and genuinely discharge TC-038..045
    (US-009) — not just compile. Author/execute any integration test needed for the sample page smoke
    (AEM Testing Clients) if you judge it adds real coverage beyond the unit tests + curl-based
    structural checks already planned for TC-001..037/046.
  - **Functional-TC attribution ledger (MANDATORY, `test/auditron/coverage.md`):** attribute ALL 46
    TC-### IDs from `functional-test-cases.md` into `auditron_executed` / `deferred_to_sentinel` /
    `blocked`, by ID, with evidence per ID (curl output excerpt, test class + method name, or policy
    JSON snippet). Every one of the 46 is `executor: auditron` per the design doc — do not defer any
    to Sentinel unless you can name the specific real-environment-only precondition that makes it
    genuinely unexecutable pre-deploy (NFR/perf/a11y-scan/visual-diff cases are already excluded from
    this file's scope — they live in Sentinel's separate NFR tracks). `total` in your handoff MUST
    equal 46 (verify by your own count of the file, not by memory).

  ## Gate reminders (do not violate)
    - Zero severity ≥ high findings, or each accepted in DECISIONS.md (route back to the Program
      Agent for a human decision if you find one — do not self-accept a high finding).
    - BUILD SUCCESS (3-signal).
    - Zero unit/integration test failures.
    - `total == 46` and `auditron_executed + deferred_to_sentinel + blocked == 46` in
      `test/auditron/coverage.md` AND in `handoffs/auditron.yaml § tests.functional_test_cases`.
    - At most 2 `mvn` calls during your entire dispatch (§8.1.1) — record each invocation's purpose
      in your handoff. If you genuinely need a 3rd, STOP and escalate to the Program Agent for a
      budget-extension decision (§P4) rather than silently running it.
    - Playwright is explicitly NOT yours to execute — Sentinel executes it later against the real
      environment. You may (and should) statically confirm the 18 specs Blockwright authored are
      discoverable (`npx playwright test --list`) as part of your review track, but do not run them.

  ## Outputs required from you
    - `runs/2026-08-28T1200Z-tata-innovation-page/test/auditron/code-quality-report.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/test/auditron/changed_files.txt`
    - `runs/2026-08-28T1200Z-tata-innovation-page/test/auditron/test-report.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/test/auditron/coverage.md` (the 46-ID ledger —
      ALWAYS required, independent of whether new unit tests were authored)
    - Any new/updated test classes under `core/src/test/` or `it.tests/src/main/` your review
      determines are genuinely needed
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/auditron.yaml`

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\auditron.yaml

gate-criteria: |
  - Zero severity >= high findings, or each accepted in DECISIONS.md.
  - 3-signal BUILD SUCCESS (exit code 0, all-zip present + size-plausible, surefire all-pass).
  - Zero unit/integration test failures.
  - Cross-file consistency checks all pass (HTL<->Sling Model accessors, BEM<->SCSS,
    policy<->component resolution, cq:allowedTemplates<->actual template, dialog fields<->
    @ValueMapValue).
  - Functional-TC ledger: total == 46 (independently counted), buckets sum to 46.
  - All 5 numbered carry-forward items above explicitly addressed in the handoff (not silently
    dropped).
  - At most 2 mvn invocations, each with recorded purpose.
  - No Playwright execution by auditron (static discovery-only is fine).
