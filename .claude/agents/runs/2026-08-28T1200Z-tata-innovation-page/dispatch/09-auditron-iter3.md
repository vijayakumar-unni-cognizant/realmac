agent: auditron
stage: Test (pre-release) — Build Validation Gate re-run, iteration 3
run-id: 2026-08-28T1200Z-tata-innovation-page
status: WRITTEN BUT NOT YET DISPATCHED — held pending Composer's remediation handoff
  (dispatch/08-composer-remediation.md) passing its own gate. Do not emit the DISPATCH-REQUEST for
  this packet until the Program Agent has validated Composer's CQ-04/CQ-05 fixes on disk.
mvn-budget: FRESH 2-call budget already granted by the human for this dispatch (see DECISIONS.md
  "Human decision on CQ-04 ... + mvn budget"). Same discipline as iteration 2: one non-`-q`
  invocation capturing full output; reserve the 2nd call only if it serves a genuine additional
  purpose.

input-packet: |
  You are `auditron` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  This is iteration 3 of the Build Validation Gate, after Composer's remediation of CQ-04 (targeted
  filter.xml entries) and CQ-05 (unique sibling node names for the 4 showcase cards). Read
  `DECISIONS.md` in full before starting.

  ## What changed since your last dispatch
    - CQ-05: the 4 card-grid `<teaser>` siblings in
      `content/realmac/us/en/innovation/.content.xml` were renamed to unique node names
      (`teaser`/`teaser_1`/`teaser_2`/`teaser_3` or similar), properties/children/order preserved.
    - CQ-04: Composer added exactly 3 targeted `mode="replace"` filter.xml entries after its own
      5-area completeness investigation:
      `/conf/realmac/settings/wcm/policies/realmac` (note: no `jcr:content` segment — Composer
      confirmed the actual on-disk policies file has `<realmac>` as a DIRECT child of the `cq:Page`
      root, not nested under `jcr:content`; the Program Agent independently re-verified this by
      reading the file's first 10 lines),
      `/content/experience-fragments/realmac/us/en/site/header/master/jcr:content`, and
      `/content/experience-fragments/realmac/us/en/site/footer/master/jcr:content`. Areas 2
      (landing-page template subtree) and 4 (sample page) were investigated and determined NOT to
      need a new entry (both are brand-new node paths that deploy fine under the existing broad
      merge root). Confirm the 3 broad pre-existing merge roots and Composer's Stage-04 DAM entry
      are UNCHANGED (they are, per the Program Agent's independent `filter.xml` read) — a quick
      sanity check, not a re-investigation.

  ## Reconfirm CQ-01/CQ-02/CQ-03 still hold (quick sanity checks, not full re-investigation —
  these were already independently confirmed fixed/accepted in iteration 2)
    - CQ-01: `site-footer.html` still uses `data-sly-repeat` (not `data-sly-list`) on `<nav>`/`<li>`.
    - CQ-02: `site-header.html` still uses the plain `data-sly-resource="${model.navigationResource}"`
      with no `@`-options; `ui.apps/pom.xml`'s `allowedExpressionOptions` still has only the original
      4 entries.
    - CQ-03: `git diff --stat` on `pom.xml`/`ui.apps/pom.xml`/`ui.frontend/pom.xml` is still identical
      to the pre-remediation state (no new hunks from anyone).
    - Confirm the 3 new CQ-04 `mode="replace"` filter entries actually took effect on this fresh
      install (this is largely the same evidence as live-verification items 1-3 below, but state it
      explicitly as its own confirmation).

  ## Mandatory live re-verification (this is the core of this dispatch — do not skip any item)
  After a fresh `mvn clean install -PautoInstallSinglePackage`, LIVE-VERIFY via curl/direct repo
  read against your local SDK install that EACH of the following actually deployed and renders
  correctly — this is what makes iteration 3 meaningful, not just a green build:
    1. All 6 new content policies resolve live (no 404) — `policy_landing_content`,
       `policy_landing_hero_teaser`, `policy_landing_intro_text`, `policy_landing_card_grid`,
       `policy_landing_card_teaser`, `policy_landing_button`.
    2. The XF-Root policy amendment (`policy_1575040440977`) is live with
       `components` including `group:Realmac - Structure`.
    3. Both XF masters serve the NEW components, not legacy: header master renders
       `<header class="cmp-site-header">` (not Navigation/LanguageNavigation/Search); footer master
       renders `<footer class="cmp-site-footer">` (not Separator/Text).
    4. The `landing-page` template is offered live in the Create Page wizard context
       (`status=enabled`, `allowedPaths` matches) — re-confirm, don't just carry forward iteration
       2's pass.
    5. The sample page `/content/realmac/us/en/innovation` renders ALL of: hero (`.cmp-teaser--hero`,
       exactly one `<h1>`), intro text (`.cmp-text--intro-lead`), the card-grid
       (`.cmp-container--card-grid`) with EXACTLY 4 distinct `.cmp-teaser--innovation-card` elements
       (not 1 — this is the direct CQ-05 re-verification), and both header/footer chrome.

  ## Required: re-attribute the 19 previously-failed + 6 previously-blocked functional test cases
  Re-attempt every one of the 25 non-passing IDs from iteration 2 (TC-001, 002, 003, 005, 006, 007,
  009, 010, 011, 012, 013, 014, 015, 016, 017, 018, 019, 020, 021, 022, 024, 025, 029, 031, 034)
  against the fresh install, with fresh live evidence per ID — not a carry-forward of the old
  fail/blocked reason. Total must still equal 46 (re-verify by your own grep of
  `design/functional-test-cases.md`). The 6 that were "blocked" due to missing dedicated fixtures
  (TC-003, 005, 009, 014, 020, 025) remain legitimately blocked unless you choose to author the
  missing fixture yourself as part of this dispatch — your call, but disclose either way.

  ## Standard responsibilities (unchanged)
    - 3-signal build-success detection per §P2.
    - Zero severity >= high findings, or each accepted in DECISIONS.md.
    - `test/auditron/coverage.md` ledger: total==46, buckets sum to 46, fresh by-ID evidence.
    - At most 2 mvn invocations this dispatch (fresh grant) — capture full output in one call.
    - No Playwright execution (Sentinel's job, still deferred pending real env URLs).

  ## Outputs required from you
    - Updated `test/auditron/{code-quality-report.md,test-report.md,coverage.md}`
    - Updated `handoffs/auditron.yaml` — this dispatch's gate is the last one before Pilot raises the
      PR, so if this passes clean, say so explicitly and hand off `build_hash` for Pilot.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\auditron.yaml

gate-criteria: |
  - 3-signal BUILD SUCCESS.
  - All 5 live-verification items above confirmed with fresh evidence (not carried forward).
  - functional_test_cases ledger: total == 46, buckets sum to 46, all 25 previously-non-passing IDs
    re-attempted with fresh evidence.
  - Zero severity >= high findings remaining (CQ-04/CQ-05 confirmed fixed live).
  - At most 2 mvn invocations.
  - handoffs/auditron.yaml present with build_hash if green (Pilot's precondition).
