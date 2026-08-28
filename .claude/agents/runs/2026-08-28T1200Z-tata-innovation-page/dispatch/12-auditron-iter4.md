agent: auditron
stage: Test (pre-release) — Build Validation Gate re-run, iteration 4
run-id: 2026-08-28T1200Z-tata-innovation-page
status: WRITTEN BUT NOT YET DISPATCHED — held pending dispatch/11-composer-cq07.md passing its gate
  (which itself is held pending dispatch/10 + 10b). Do not emit until the Program Agent has
  validated the full chain on disk.
mvn-budget: FRESH 2-call budget already granted by the human for this dispatch (see DECISIONS.md
  "Human decisions on the 4-point P5 escalation").

input-packet: |
  You are `auditron` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  This is iteration 4 (human-authorized past the § P5 3-fail cap) of the Build Validation Gate,
  after: Blockwright's CQ-06 fix + new innovation-card proxy component; Configsmith's policy
  re-keying; Composer's content re-typing of the 4 cards. Read `DECISIONS.md` in full before
  starting.

  ## Mandatory live re-verification
  After a fresh `mvn clean install -PautoInstallSinglePackage` (single non-`-q` invocation, full
  output captured — same discipline as prior iterations), LIVE-VERIFY via curl against your local
  SDK install:
    1. Footer link columns render populated (not empty) — all 4 columns with headings + nested
       links, mirroring the sample page's authored content. (CQ-06 re-verification.)
    2. Exactly ONE `<h1>` tag on the sample page (the hero's), and exactly FOUR DISTINCT DOM
       elements carrying the `.cmp-teaser--innovation-card` class (one per card_0/card_1/card_2/
       card_3 — Composer renamed the sibling nodes from teaser/teaser_1/_2/_3 to card_0..3 this
       iteration), each rendering `<h3>` (not `<h1>`) for its title, in the authored order (Tata
       Chemicals, Tata Steel Europe, TCS Innovation Labs, Tata Motors ETC). (CQ-07 re-verification
       — this is the core check — and simultaneously the CQ-05 re-verification: 4 distinct elements,
       not 1, must be counted by DOM element/node identity, not merely by class-string occurrence
       count, since a stray duplicate class string elsewhere in the page would not prove 4 distinct
       cards actually rendered.)
    3. The hero teaser is UNAFFECTED — still resolves `policy_landing_hero_teaser`
       (titleType=h1, `.cmp-teaser--hero` class, no CTA) exactly as before. Confirm the CQ-07 fix
       did not regress the hero.
    4. CQ-01/CQ-02/CQ-03/CQ-04 all still hold (quick reconfirmation, not full re-investigation —
       these have been independently confirmed fixed/accepted for 1-3 iterations running). CQ-05 is
       covered by item 2 above (4 distinct card elements), not a separate check.

  ## Required: re-attribute the 5 previously-failed functional test cases
  Re-attempt TC-002, TC-012, TC-015, TC-022, TC-031 against the fresh install with fresh live
  evidence. Total must still equal 46 (re-verify by your own grep). The 6 previously-blocked IDs
  (TC-003, 005, 009, 014, 020, 025) remain your call whether to author fixtures for or continue
  disclosing as blocked.

  ## If this iteration is clean
  This is the gate immediately before Pilot raises the release PR. If BUILD_SUCCESS + all live
  verification items pass + zero remaining high findings (or each explicitly accepted in
  DECISIONS.md), say so explicitly in your handoff and hand off `build_hash` for Pilot — this would
  be the first genuinely green stage-05 gate of this run, after 3 consecutive FAILs.

  ## Standard responsibilities (unchanged)
    - 3-signal build-success detection per §P2.
    - `test/auditron/coverage.md` ledger: total==46, buckets sum to 46, fresh by-ID evidence for the
      5 re-attempted IDs.
    - At most 2 mvn invocations this dispatch.
    - No Playwright execution.

  ## Outputs required from you
    - Updated `test/auditron/{code-quality-report.md,test-report.md,coverage.md}`
    - Updated `handoffs/auditron.yaml`

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\auditron.yaml

gate-criteria: |
  - 3-signal BUILD SUCCESS.
  - All 4 mandatory live-verification items confirmed with fresh evidence.
  - functional_test_cases ledger: total == 46, buckets sum to 46, all 5 previously-failing IDs
    re-attempted with fresh evidence.
  - Zero severity >= high findings remaining.
  - At most 2 mvn invocations.
  - handoffs/auditron.yaml present with build_hash if green.
