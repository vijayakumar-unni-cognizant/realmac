# Test Report — tata-innovation-page (Iteration 5 — SANITY ONLY)

- **status:** PASS
- **run:** 2026-08-28T1200Z-tata-innovation-page
- **url:** http://localhost:4502

> 3-signal **BUILD_SUCCESS** (exit=0, all.zip fresh 1,249,225 bytes produced 21:09:42 inside the
> 21:08:50-21:10:49 build window, surefire 20/20 across 7 test classes / 0 failures / 0 errors).
> CQ-08 and CQ-10 independently re-confirmed present in the versioned source (see
> code-quality-report.md). CQ-09 recorded as accepted-low per explicit human decision — not blocking.
> Functional-TC ledger: 46 total — 40 auditron_executed (38 pass / 2 accepted_known_gap / 0 fail),
> 0 deferred_to_sentinel, 6 blocked. mvn calls used: **1 of 1 granted** (exactly on budget).
> **Gate is GREEN — zero remaining unaccepted HIGH findings.**

## Scores

- Build Gate: **100** (3/3 signals)
- Unit Tests: **100** (20/20 pass)
- Source Confirmation (CQ-08/CQ-10): **100** (2/2 confirmed present in versioned source)
- Functional TCs: **100** (38 pass + 2 accepted_known_gap = 40/40 executed non-failing; 0 fail)

## Track matrix

| Track | Verdict | Detail |
|---|---|---|
| 3-signal build | BUILD_SUCCESS | exit=0; all.zip 1,249,225 bytes fresh (produced 21:09:42, 62s into the build, well inside the 21:08:50-21:10:49 window); surefire 20/20 across 7 test classes, 0 failures/errors; all 10 reactor modules reached SUCCESS |
| CQ-08 source confirmation | PASS | `imageDelegate="realmac/components/image"` present on `apps/realmac/components/innovation-card/.content.xml`, read directly |
| CQ-10 source confirmation | PASS | `titleFromPage`/`descriptionFromPage`={Boolean}false present on both policies AND all 5 content instances, read directly |
| CQ-09 disposition | ACCEPTED-LOW | per human decision DECISIONS.md 2026-08-28T20:50Z — not blocking |
| mvn budget | 1 of 1 used | exactly on budget, no overage, no 2nd call requested or made |
| Bonus opportunistic live smoke check | INFORMATIONAL | local SDK reachable after the build; confirmed CQ-08/CQ-10 fixes are live-effective and CQ-09's accepted symptom is unchanged as expected — not required, did not consume budget |

## Build log evidence

```
mvn clean install -PautoInstallSinglePackage > /tmp/aem-build-iter5.log 2>&1
exit=0

Reactor Summary:
Realmac 1.0.0-SNAPSHOT ............................. SUCCESS [  1.314 s]
Realmac - Core 1.0.0-SNAPSHOT ...................... SUCCESS [ 21.094 s]
Realmac - Repository Structure Package 1.0.0-SNAPSHOT SUCCESS [  2.470 s]
Realmac - UI apps 1.0.0-SNAPSHOT ................... SUCCESS [  6.554 s]
Realmac - UI content 1.0.0-SNAPSHOT ................ SUCCESS [  4.030 s]
Realmac - UI config 1.0.0-SNAPSHOT ................. SUCCESS [  0.733 s]
Realmac - All 1.0.0-SNAPSHOT ....................... SUCCESS [ 39.501 s]
Realmac - UI Frontend 1.0.0-SNAPSHOT ............... SUCCESS [  1.114 s]   (skip=true no-op, CQ-03 accepted local constraint)
Realmac - Integration Tests 1.0.0-SNAPSHOT ......... SUCCESS [ 19.420 s]
com.adobe.cq.cloud.testing.ui.cypress - UI Tests 0.0.1-SNAPSHOT SUCCESS [  6.866 s]
BUILD SUCCESS
Total time:  01:49 min

Tests run: 20, Failures: 0, Errors: 0, Skipped: 0 (7 test classes: LoggingFilterTest,
SimpleResourceListenerTest, HelloWorldModelTest, SiteFooterModelTest(7), SiteHeaderModelTest(8),
SimpleScheduledTaskTest, SimpleServletTest)
```

## Functional test cases: 46 total — 40 auditron_executed (38 pass / 2 accepted_known_gap / 0 fail), 0 deferred_to_sentinel, 6 blocked

Full per-ID ledger (also in coverage.md, carried forward from iteration 4 with 2 re-attributions this
iteration): TC-001 pass, TC-002 pass, TC-003 blocked, TC-004 pass, TC-005 blocked, TC-006 pass,
TC-007 pass, TC-008 pass, TC-009 blocked, TC-010 pass, TC-011 pass, **TC-012 accepted_known_gap**,
TC-013 pass, TC-014 blocked, **TC-015 accepted_known_gap**, TC-016 pass, TC-017 pass, TC-018 pass,
TC-019 pass, TC-020 blocked, TC-021 pass, TC-022 pass, TC-023 pass, TC-024 pass, TC-025 blocked,
TC-026 pass, TC-027 pass, TC-028 pass, TC-029 pass, TC-030 pass, TC-031 pass, TC-032 pass, TC-033
pass, TC-034 pass, TC-035 pass, TC-036 pass, TC-037 pass, TC-038 pass, TC-039 pass, TC-040 pass,
TC-041 pass, TC-042 pass, TC-043 pass, TC-044 pass, TC-045 pass, TC-046 pass.

Bucket sum check: 40 (38 pass / 2 accepted_known_gap) + 0 (deferred) + 6 (blocked) = 46 =
total_from_file (re-verified this iteration via
`grep -oE '\bTC-[0-9]+\b' design/functional-test-cases.md | sort -u | wc -l` = 46). LEDGER COMPLETE.

**Re-attribution this iteration:** TC-012 and TC-015 were `fail` at the end of iteration 4, both
tracing to CQ-08/CQ-09/CQ-10. This iteration independently re-confirmed CQ-08 and CQ-10 are now fixed
in the versioned source (and, via the bonus opportunistic live check, are fixed live as well — cards
render their own image, own title, own description). The ONLY remaining symptom for these 2 cases is
CQ-09 (no distinct `.cmp-teaser--innovation-card` style class; heading renders `<h2>` not `<h3>`),
which is a human-accepted-low, documented known gap (DECISIONS.md 2026-08-28T20:50Z) — not an open
defect. Reporting these 2 IDs as a plain unqualified "fail" would misrepresent an accepted, disclosed
gap as an active failure; reporting them as a silent "pass" would hide the gap entirely. `accepted_known_gap` is
used to make both the current behavior and its provenance (human acceptance, not oversight) explicit
and traceable. No other IDs were re-verified this iteration — carried forward from iteration 4's
ledger per this dispatch's explicit scope boundary ("You do NOT need to re-verify every other
previously-passing TC from scratch").

## No silent scope narrowing

This dispatch's scope was deliberately and explicitly bounded to exactly 5 items by the Program
Agent's own dispatch packet (CQ-08/CQ-10 source confirmation, 1 Build Gate run, CQ-09 acceptance
recording, ledger update) — this is stated here, not absorbed silently. 44 of the 46 ledger rows are
carried forward from iteration 4's evidence without re-verification this iteration, per the dispatch's
own explicit instruction. This is a disclosed, human-directed scope reduction, not a self-imposed one.
No live curl sweep of every render item was performed as a requirement (one bonus opportunistic check
was performed, disclosed above, at zero mvn-budget cost).
