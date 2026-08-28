# PLAN — Tata Innovation Landing Page (realmac)

**Run ID:** `2026-08-28T1200Z-tata-innovation-page`
**Intake:** Build a new AEMaaCS page visually matching https://www.tata.com/about-us/innovation (design/layout/typography/spacing reference ONLY — no DOM/CSS transplant), using locally supplied assets at `C:\Users\2489691\Downloads\tata-innovation-assets`, fresh-authored against AEMaaCS patterns, extending existing project components where possible.
**Repo:** `C:\AEM\Repos\realmac` (branch `feature/realmac-landing-page` → PR target `master`)
**Project identifiers:** project=`realmac`, package=`com.realmac.aem.core`, group=`Realmac - Content` (from `.aem-skills-config.yaml`, newly created this session since no prior config file existed).

## Stage plan (ADLC-SPEC §5.1.a)

| # | Stage | Specialist(s) | Mode | Notes |
|---|---|---|---|---|
| 01 | Plan | `strategist` | serial | Fetches reference URL for visual analysis; requirements.yaml + technical-specifications.md; must include `plan/reference-deconstruction.md` per §P6 (reference image/URL in intake). |
| — | **Human checkpoint 1** | Program Agent | — | Architecture review — approve component plan before Designforge. |
| 02 | Design | `designforge` | serial | Component/dialog/template/policy specs, functional + UI test cases, `reference-assets.md`, `authoring-test-cases.md`. Design-only. |
| — | **Human checkpoint 2** | Program Agent | — | Dialog spec confirmation — approve before implementation fan-out. |
| 03 | Implement + Integrate | `blockwright`, `configsmith`, `composer` | parallel | Blockwright: hero/intro/showcase-grid components (extend Core Components), editable template + policies, SCSS, Sling Model unit tests, Playwright spec harness (pre-deploy, no Cypress). Configsmith: any ACL/repoinit needed for DAM seeding service user (likely minimal — same author permissions), security review of new components. Composer: DAM asset seeding (13 files) + sample page authoring at `/content/realmac`. **Bridgesmith skipped** — no external integration in scope (per intake). |
| 04 | Test (pre-release) | `auditron` | serial | Code-quality review, Build Validation Gate (`mvn` #1), unit + integration tests (`mvn` #2). 2-mvn budget. |
| 05 | Release | `pilot` | serial | Raise PR feature/realmac-landing-page → master once Auditron green. Flow suspends (`awaiting_lead_approval`). |
| — | **PAUSE** | Lead (human, out-of-flow) | — | Manual merge → Adobe Git → Cloud Manager deploy. Program Agent asks for Author + Publish URLs + auth mode to resume. |
| — | **Human checkpoint 3** | Program Agent | — | Real-environment validation approval (resume block in DECISIONS.md). |
| 06 | Test (post-deploy) | `sentinel` | serial (deferred) | **Deferred this session per intake — no Author/Publish URLs available yet.** Blockwright still authors Playwright specs pre-deploy so Cloud Manager's Custom UI Testing runs them. Run is `PAUSED — Sentinel outstanding`, not complete, per §P11. |

## Out of scope (per intake, confirmed with human at checkpoint if needed)
- Bridgesmith (no external system integration).
- Composer's headless/CF-model track (server-rendered Sites page only, no CF models needed for hero/intro/showcase-grid — content authored directly via component dialogs, not CF-backed) — confirm with Strategist/Designforge; if Strategist determines CF Models add value (e.g., for the showcase cards), route to Composer's headless track instead.
- Sentinel execution — deferred; flagged as pending environment URLs. Not descoped from PLAN — remains an owed, LAST stage (§P11).
- Real deploy, Cloud Manager pipeline triggers, post-deploy incident triage — out of ADLC scope entirely.

## Reference-fidelity gates active (§P6, since a reference URL + local image assets are in intake)
- Strategist → `plan/reference-deconstruction.md` (per-region breakdown of the Tata page).
- Designforge → Pixel-Verified Acceptance Criteria tables per component; `design/reference-assets.md` manifest of all 13 supplied assets + the reference URL.
- Blockwright → `runtime_style_system_classes: verified` or documented skip reason.
- Auditron → Check 23 (HTL/SCSS parity) zero HIGH findings.
- Sentinel (when it eventually runs) → Visual Verification Tier A against the reference URL at 1440x900 desktop / 390x844 mobile (§P8 default viewports — no override specified in intake).

## DAM asset checkpoint (§P7)
Composer seeds the 13 local assets directly into `/content/dam/realmac/tata-innovation/` — these are real binaries supplied by the human, not placeholders, so the §P7 checkpoint is satisfied by construction (no separate human confirmation needed before Sentinel's eventual Tier A run, since Sentinel is deferred anyway).

## Budget
- mvn calls: 2 max (Auditron only), per §8.1.1.
- No RDE track requested.
