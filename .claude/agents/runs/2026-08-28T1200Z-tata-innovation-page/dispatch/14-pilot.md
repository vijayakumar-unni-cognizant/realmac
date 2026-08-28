agent: pilot
stage: Release — raise PR
run-id: 2026-08-28T1200Z-tata-innovation-page
status: WRITTEN BUT NOT YET DISPATCHED — held pending dispatch/13-auditron-iter5-sanity.md
  returning a GREEN gate (handoffs/auditron.yaml status: pass, build_hash present). Do not emit the
  DISPATCH-REQUEST for this packet until the Program Agent has validated that on disk.

input-packet: |
  You are `pilot` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page` in
  the `realmac` repo (`C:\AEM\Repos\realmac`, remote `origin` =
  https://github.com/vijayakumar-unni-cognizant/realmac.git, current branch
  `feature/realmac-landing-page`, default branch `master`).

  ## Precondition (verify before doing anything else)
  `handoffs/auditron.yaml` must show `status: pass` with a real `build_hash` (from the sanity
  iteration-5 dispatch). If it does not, STOP and report back — do not raise a PR against a
  non-green gate.

  ## CRITICAL — staging exclusions (read before running any `git add`)
  This run's working tree contains 3 pre-existing, human-authored, LOCAL-BUILD-ONLY pom.xml edits
  (CQ-03, fully documented in `DECISIONS.md`) that must NEVER reach the PR:
    - `pom.xml` (root) — frontend-maven-plugin node/npm install skip
    - `ui.apps/pom.xml` — commented-out `com.realmac:realmac.ui.frontend:zip` dependency
    - `ui.frontend/pom.xml` — `skipAssembly=true`
  These exist ONLY so the human can build locally behind a Zscaler-restricted network that blocks
  the frontend-maven-plugin's node/npm download. They must be EXCLUDED from your commit entirely so
  Cloud Manager's pipeline runs its normal, unmodified `ui.frontend` -> `ui.apps` clientlib build.
  **Do not `git add` these 3 files. Do not include them in the commit under any circumstance.**

  Additionally, the working tree shows a modified `.gitignore`. This is UNRELATED to this feature —
  it was open/touched by the human for reasons outside this run's scope, not authored by any
  specialist, and not part of this run's deliverable. **Do NOT assume it belongs in this PR. Leave
  it unstaged/excluded** unless the human explicitly tells you otherwise before you commit.

  Also exclude `.claude/agents/runs/2026-08-28T1200Z-tata-innovation-page/` (this run's own
  orchestration scaffolding — PLAN.md, DECISIONS.md, dispatch packets, handoffs, reports) from the
  PR commit. NOTE: the Program Agent checked and this path is technically NOT git-ignored (the
  project's `.gitignore` has `!.claude/agents/runs/`, which un-ignores the whole `runs/` subtree,
  not just its top-level placeholder — broader than `runs/README.md`'s stated intent that only the
  top-level directory stay tracked). Since you are staging explicit, named paths (not `git add -A`
  or `git add .`), this won't get swept in accidentally — just don't add it deliberately either. If
  the human separately asks to preserve this run for audit purposes (per `runs/README.md`'s "when
  to commit a specific run" exception), that would be a distinct, explicit `git add -f`-style
  request — not part of this feature PR.

  Concretely: run `git status` yourself first, identify the exact file list, and stage ONLY:
    - New/modified files under `core/` (SiteHeaderModel, SiteFooterModel, FooterColumn, FooterLink,
      UtilityLink, SocialLink + their unit tests)
    - New files under `ui.apps/` (site-header, site-footer, innovation-card components)
    - New/modified files under `ui.content/` (landing-page template, DAM assets, XF master content,
      sample page, filter.xml, the consolidated policies file, the landing-page policy mapping file)
    - New/modified files under `ui.frontend/src/main/webpack/` (the SCSS partials + `_site-header.js`)
      — but NOT `ui.frontend/pom.xml`
    - New/modified files under `ui.tests/` (the Playwright migration + specs) — but review whether
      any of these changes are genuinely part of THIS run's Playwright-authoring deliverable vs.
      unrelated tooling churn; when in doubt, include only what Blockwright's handoff explicitly
      attributes to this run.
    - Do NOT stage: `pom.xml`, `ui.apps/pom.xml`, `ui.frontend/pom.xml`, `.gitignore`.

  If you are genuinely uncertain whether a given changed file belongs in this PR, list it explicitly
  in your handoff under an "excluded, uncertain" note rather than silently including or excluding it
  — do not guess silently either direction.

  ## Standard responsibilities (per your contract)
    - Confirm the working tree is otherwise clean enough to commit (aside from the deliberate
      exclusions above) and `HEAD` is `feature/realmac-landing-page`, not `master`.
    - Push the branch to `origin`.
    - Open the PR via `gh pr create --base master --head feature/realmac-landing-page` (or the
      GitHub REST API fallback if `gh` is unavailable — resolve `owner/repo` canonically from the
      API, not from the `origin` URL string).
    - PR body must include: a run summary (Tata-innovation-page-style landing page: hero, intro,
      4-card showcase grid, header/footer chrome, new `landing-page` template); Auditron's evidence
      (3-signal BUILD_SUCCESS, build_hash, the functional-TC ledger summary); an explicit
      "not yet NFR-validated" note (Sentinel is deferred pending Author/Publish real environment
      URLs — see DECISIONS.md); the accepted-known-gap note for CQ-09 (showcase cards render with
      Core Teaser's h2 default heading level and without a distinct card-style class, due to an AEM
      nested-container Content Policy resolution limitation — human-accepted, documented in
      DECISIONS.md, not a blocker); and the post-merge Lead checklist (merge -> sync to Adobe Git ->
      deploy to the real environment -> supply Author + Publish URLs + auth mode to resume the ADLC
      into Sentinel).
    - Return `status: awaiting_lead_approval` and STOP — never merge, deploy, wait, or poll.

  ## Outputs required from you
    - `runs/2026-08-28T1200Z-tata-innovation-page/deploy/pr-request.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/deploy/pr-body.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/pilot.yaml`

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\pilot.yaml

gate-criteria: |
  - Auditron status: pass with build_hash, confirmed before Pilot starts.
  - The 3 CQ-03 pom.xml files are NOT in the commit.
  - .gitignore is NOT in the commit unless the human explicitly said otherwise.
  - PR opened from feature/realmac-landing-page against master via gh or the REST API.
  - PR body includes the Sentinel-deferred note and the CQ-09 accepted-gap note.
  - status: awaiting_lead_approval returned; no merge/deploy attempted.
  - handoffs/pilot.yaml + deploy/pr-request.md + deploy/pr-body.md present.
