agent: auditron
stage: Test (pre-release) — Build Validation Gate re-run, iteration 5, SANITY-ONLY
run-id: 2026-08-28T1200Z-tata-innovation-page
mvn-budget: EXACTLY 1 mvn call granted by the human for this dispatch (see DECISIONS.md — the human
  does not want another full remediation loop, only a sanity check). Do NOT spend a 2nd call. Do NOT
  request a budget extension mid-dispatch — if you believe you genuinely need one, stop and report
  back to the Program Agent instead of running it.

input-packet: |
  You are `auditron` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  This is a SANITY-ONLY iteration 5, not a full remediation-verification cycle. Read `DECISIONS.md`
  in full before starting — it has the complete history and the human's explicit decisions this
  round: CQ-08 and CQ-10 were fixed directly by the human in the versioned source (independently
  verified by the Program Agent already); CQ-09 is DOWNGRADED to LOW and formally ACCEPTED as a
  documented known gap (human explicit decision) — it is NOT a blocking finding this iteration.

  ## Scope — exactly 5 things, nothing more
  a. **Confirm CQ-08 in the VERSIONED SOURCE** (not any running instance): read
     `ui.apps/src/main/content/jcr_root/apps/realmac/components/innovation-card/.content.xml`
     yourself and confirm it has `imageDelegate="realmac/components/image"`.
  b. **Confirm CQ-10 in the VERSIONED SOURCE**: read
     `ui.content/.../settings/wcm/policies/.content.xml` and confirm
     `titleFromPage="{Boolean}false"` + `descriptionFromPage="{Boolean}false"` are present on BOTH
     `policy_landing_hero_teaser` and `policy_landing_card_teaser` (under `<innovation-card>`); AND
     read `content/realmac/us/en/innovation/.content.xml` and confirm the same two properties are
     present on all 5 teaser/innovation-card instances (the hero + all 4 cards).
  c. **Run ONE `mvn clean install -PautoInstallSinglePackage`** — single invocation, full (non-`-q`)
     output captured to a log file in that same call. Apply the standard 3-signal build-success
     detection (exit code, `all` zip presence + freshly-produced-this-run size, surefire summary).
     Record `build_hash` if green.
  d. **Record CQ-09 as an ACCEPTED LOW finding**, not a blocking high, in your findings summary and
     in `test/auditron/coverage.md` / `code-quality-report.md` — cite the human's acceptance
     decision recorded in `DECISIONS.md` (cards render Core Teaser's h2 default + no distinct
     `.cmp-teaser--innovation-card` style class, due to AEM's nested-container Content Policy
     resolution limitation; human explicitly accepted this as a low-severity cosmetic gap).
  e. **Update `test/auditron/coverage.md`** to reflect current reality: the functional-TC ledger
     total must still be 46 (re-verify by your own grep); TC-012/TC-015 (the two that were failing
     specifically because of CQ-09's missing card styling) should be re-attributed as
     `blocked`/`accepted-known-gap` rather than `fail` outright, since the underlying finding is now
     an accepted gap, not an unresolved defect — use your own judgment on the exact ledger status
     label, but the rationale must trace to the human's CQ-09 acceptance, not silently disappear.
     You do NOT need to re-verify every other previously-passing TC from scratch — carry those
     forward from iteration 4's ledger with their existing evidence and iteration attribution,
     since nothing in this dispatch's scope touches them.

  ## Explicitly OUT of scope this dispatch
    - No live curl sweep of every render item — the human is independently verifying render
      behavior in their own running AEM instance. A quick opportunistic smoke check IS fine if your
      local SDK happens to be reachable after the one mvn call, but do not treat it as required, and
      do not let it become an excuse to spend a 2nd mvn call or re-open findings outside this
      dispatch's 5-item scope.
    - No new remediation, no new findings investigation beyond confirming a/b/c above. If you
      discover something alarming while doing the scoped work (e.g., the build genuinely breaks),
      report it plainly — but do not go looking for new problems this round.

  ## Gate verdict
  If (c) achieves 3-signal BUILD_SUCCESS and (a)/(b) confirm CQ-08/CQ-10 are genuinely in the
  versioned source, this gate is GREEN — zero remaining unaccepted high findings (CQ-01 through
  CQ-08, CQ-10 all resolved/accepted across iterations 1-5; CQ-09 accepted-low this iteration).
  State this explicitly and hand off `build_hash` for Pilot. If any of a/b/c fails, say so plainly
  and do NOT fabricate a green verdict — report back to the Program Agent instead.

  ## Outputs required from you
    - Updated `test/auditron/{code-quality-report.md,test-report.md,coverage.md}`
    - Updated `handoffs/auditron.yaml`

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\auditron.yaml

gate-criteria: |
  - CQ-08 confirmed present in versioned source (imageDelegate on innovation-card).
  - CQ-10 confirmed present in versioned source (titleFromPage/descriptionFromPage=false on both
    policies + all 5 content instances).
  - Exactly 1 mvn invocation; 3-signal BUILD_SUCCESS achieved.
  - CQ-09 recorded as accepted-low, not blocking.
  - coverage.md total == 46, updated to reflect CQ-09's accepted status for TC-012/TC-015.
  - handoffs/auditron.yaml status reflects a GREEN gate (zero unaccepted highs) with build_hash, or
    an honest non-green report if a/b/c did not hold — no fabricated pass.
