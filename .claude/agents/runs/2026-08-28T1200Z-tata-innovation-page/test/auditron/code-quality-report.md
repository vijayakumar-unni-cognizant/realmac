# Code Quality Report — tata-innovation-page (Iteration 5 — SANITY ONLY)

- **status:** PASS
- **run:** 2026-08-28T1200Z-tata-innovation-page
- **url:** http://localhost:4502

> This is a SANITY-ONLY dispatch (iteration 5), not a full remediation-verification cycle, per
> explicit Program Agent instruction and the human decisions recorded in DECISIONS.md
> (2026-08-28T20:50Z). Scope was deliberately narrow: (a) confirm CQ-08 in the versioned source,
> (b) confirm CQ-10 in the versioned source, (c) run exactly ONE `mvn clean install
> -PautoInstallSinglePackage` and apply the 3-signal Build Validation Gate check, (d) record CQ-09 as
> an accepted-low finding (not blocking), (e) update the functional-TC ledger to reflect CQ-09's
> accepted status. **All 5 items discharged. Zero remaining unaccepted HIGH findings. GATE IS GREEN.**

## Track matrix

| Track | Verdict | Detail |
|---|---|---|
| CQ-08 versioned-source confirmation | PASS | `imageDelegate="realmac/components/image"` present in `apps/realmac/components/innovation-card/.content.xml`, read directly this iteration |
| CQ-10 versioned-source confirmation | PASS | `titleFromPage="{Boolean}false"` + `descriptionFromPage="{Boolean}false"` present on both `policy_landing_hero_teaser` and `policy_landing_card_teaser` (under `<innovation-card>`) in `settings/wcm/policies/.content.xml`, AND on all 5 teaser/innovation-card content instances in `content/realmac/us/en/innovation/.content.xml` — read directly this iteration |
| Build Validation Gate (3-signal) | BUILD_SUCCESS | exit=0; all.zip fresh 1,249,225 bytes (produced 21:09:42, inside this build's 21:08:50-21:10:49 window); surefire 20/20 across 7 test classes, 0 failures/errors |
| CQ-09 disposition | ACCEPTED-LOW | downgraded from HIGH to LOW per explicit human decision (DECISIONS.md 2026-08-28T20:50Z); recorded as a known, documented, non-blocking cosmetic gap, not a blocking finding this iteration |
| Functional-TC ledger | COMPLETE | 46 total = 40 auditron_executed (38 pass / 2 accepted_known_gap / 0 fail) + 0 deferred + 6 blocked — re-verified own census this iteration |
| mvn budget | COMPLIANT | exactly 1 of 1 granted call used |
| Bonus opportunistic live smoke check | PASS (informational only) | local SDK happened to be reachable after the mvn call; confirmed CQ-08/CQ-10 fixes are live-effective (images render, own title/description render) and CQ-09's symptom is still present as expected for an accepted gap (0 style-class occurrences, h2 not h3) — not required by dispatch scope, did not consume mvn budget, did not expand scope |

## Findings

### [LOW] CQ-09 — innovation-card's own nested Content Policy never applies at runtime — ACCEPTED KNOWN GAP
- **Issue:** The 4 showcase cards render with Core Teaser's hardcoded `<h2>` default heading level (not the intended `<h3>`) and without the distinct `.cmp-teaser--innovation-card` style class, because AEM's Content Policy resolution does not reliably apply across 2 stacked levels of author-dropped (non-structural) container nesting.
- **Evidence:** Bonus live re-check this iteration (opportunistic, not required): `grep -c "cmp-teaser--innovation-card"` on the rendered page returns 0; heading levels observed are 1×`<h1>` (hero, correct) + 4×`<h2>` (cards, should be `<h3>`). Root cause independently analyzed and confirmed correct by the Program Agent at the Stage-12 gate entry (DECISIONS.md) — the mapping XML and policy content are both correct on disk and live; the platform's runtime resolution is the limiting factor, not an authoring or configuration mistake.
- **Cause:** AEM's Content Policy resolution for editable-template nested mappings is reliable for ONE level of resourceType-based disambiguation from a structural ancestor, but becomes unreliable across TWO stacked levels of non-structural (author-dropped) ancestors — a narrow, genuine AEM platform characteristic, not a configuration defect in this run's authored artifacts.
- **Human decision (DECISIONS.md 2026-08-28T20:50Z):** CQ-09 DOWNGRADED to LOW and formally ACCEPTED as a documented known gap. The human explicitly declined to pursue the Option-1 thin-real-component fix (deterministic HTL-level h3 + style-class override) previously recommended by the Program Agent, accepting this as a low-severity cosmetic gap instead.
- **Recommended fix (not actioned, informational only):** if revisited in a future run — Option 1, a thin real `innovation-card` component that deterministically bakes `<h3>` + the `.cmp-teaser--innovation-card` class into its own rendering, independent of Content Policy resolution succeeding for these 2 presentational properties.
- **Route:** none (accepted; no further action this run)
- **Status:** accepted

### [INFO] CQ-08 — imageDelegate on innovation-card — reconfirmed fixed in versioned source
- **Issue:** n/a — versioned-source confirmation only, per dispatch scope item (a).
- **Evidence:** Read `ui.apps/src/main/content/jcr_root/apps/realmac/components/innovation-card/.content.xml` directly this iteration: `imageDelegate="realmac/components/image"` present on the component's `.content.xml`. Bonus live re-check: 5 occurrences of `.cmp-teaser__image` on the rendered page (1 hero + 4 cards) — all cards now render images correctly.
- **Cause:** n/a (fixed directly by the human in the versioned source per DECISIONS.md 2026-08-28T20:50Z, independently re-verified here)
- **Recommended fix:** none — holding
- **Route:** none
- **Status:** resolved

### [INFO] CQ-10 — titleFromPage/descriptionFromPage explicit false — reconfirmed fixed in versioned source
- **Issue:** n/a — versioned-source confirmation only, per dispatch scope item (b).
- **Evidence:** Read `settings/wcm/policies/.content.xml` directly this iteration: `titleFromPage="{Boolean}false"` + `descriptionFromPage="{Boolean}false"` present on both `policy_landing_hero_teaser` (lines 382-383) and `policy_landing_card_teaser` under `<innovation-card>` (lines 409-410). Read `content/realmac/us/en/innovation/.content.xml` directly: both properties present on all 5 teaser/innovation-card instances (hero teaser lines 29-30; card_0 lines 59-60; card_1 lines 77-78; card_2 lines 95-96; card_3 lines 113-114) — belt-and-suspenders (policy default + explicit per-instance authored value). Bonus live re-check: all 4 cards render their OWN authored `jcr:title` and `jcr:description` text (not the action-link text / page meta-description).
- **Cause:** n/a (fixed directly by the human in the versioned source per DECISIONS.md 2026-08-28T20:50Z, independently re-verified here)
- **Recommended fix:** none — holding
- **Route:** none
- **Status:** resolved

### [INFO] CQ-01 through CQ-07 — all previously-fixed/accepted findings, not re-verified this iteration (out of scope)
- **Issue:** n/a — this dispatch's scope was explicitly limited to 5 items (CQ-08, CQ-10 source-confirmation; 1 mvn Build Gate call; CQ-09 acceptance recording; ledger update). CQ-01 through CQ-07 were exhaustively verified fixed/accepted across iterations 1-4 (see DECISIONS.md for the full independent verification trail) and are carried forward unchanged per this dispatch's explicit "no new remediation, no new findings investigation beyond confirming a/b/c" instruction.
- **Evidence:** n/a — carried forward from prior iterations' independent verification.
- **Cause:** n/a
- **Recommended fix:** none — holding
- **Route:** none
- **Status:** resolved / accepted (per prior iterations)

## Gate verdict

Zero remaining unaccepted HIGH findings. CQ-01 through CQ-08 and CQ-10 are all resolved/accepted
across iterations 1-5. CQ-09 is accepted-low as of this iteration, per explicit human decision.
Build Validation Gate achieved genuine 3-signal BUILD_SUCCESS with exactly 1 mvn invocation (of the
1 granted for this sanity dispatch). **GATE IS GREEN.** Pilot may proceed with build_hash
`e260c64ec27823a644bd7c02fc157c09453b45fc` (HEAD at build time; working tree carries this run's
uncommitted source changes, which Pilot will stage and commit per its own dispatch packet — see the
mandatory PR-hygiene reminders already recorded in DECISIONS.md regarding excluding the 3 local-only
Zscaler build-skip pom edits and the unrelated `.gitignore` change from the release PR commit).
