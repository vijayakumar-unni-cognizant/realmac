agent: designforge
stage: Design
run-id: 2026-08-28T1200Z-tata-innovation-page

input-packet: |
  You are `designforge` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`
  in the `realmac` AEMaaCS project (repo root `C:\AEM\Repos\realmac`, branch
  `feature/realmac-landing-page`). Human Checkpoint 1 (architecture review) has been APPROVED AS-IS
  — no changes to the plan below.

  ## Required reading before you start
    - `C:\AEM\Repos\realmac\.aem-skills-config.yaml` (project=realmac, package=com.realmac.aem.core,
      group="Realmac - Content")
    - `runs/2026-08-28T1200Z-tata-innovation-page/plan/requirements.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/plan/technical-specifications.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/plan/reference-deconstruction.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/strategist.yaml`

  ## Task
  Convert the approved work breakdown into implementation-ready design artifacts. Design-only — no
  code, no dialogs written as files-to-be-deployed, markdown specs only.

  Per Strategist's component triage (approved as-is at Human Checkpoint 1):
    - **2 net-new components** (chrome only): `site-header`, `site-footer` — full component +
      dialog specs required, group "Realmac - Structure".
    - **4 Style System variants** on existing Core Component proxies — NO new component, NO custom
      HTL/dialog on the proxy itself:
      - Hero banner → `realmac/components/teaser` variant `cmp-teaser--hero`
      - Intro/overview text → `realmac/components/text` variant `cmp-text--intro-lead`
      - Card grid → `realmac/components/container` variant `cmp-container--card-grid`
      - Showcase card ×4 → `realmac/components/teaser` variant `cmp-teaser--innovation-card`
    - **New editable template** `landing-page` (reusing template-type `page`) — structure: root
      container proxy → EF header ref → single editable container-proxy parsys (hero + intro +
      card-grid authored here) → EF footer ref. NO structural Title.

  ## Reference-fidelity gate (ADLC-SPEC §P6 — ACTIVE, since a reference URL + local image assets are
  in this run's intake)
  You MUST produce, in addition to the standard design pack:
    - `design/design-token-audit.md` — finalize exact typography/spacing/color tokens from
      `plan/reference-deconstruction.md`'s best-effort ranges into pinned values (font sizes, line
      heights, colors, breakpoints, spacing scale) that Blockwright's SCSS will implement verbatim.
    - Every component spec in `design/component-specifications.md` MUST include a
      "Pixel-Verified Acceptance Criteria" table with computed-style expectations at BOTH desktop
      (1440px viewport, per §P8 default) and mobile (390px viewport) breakpoints — cite the
      `reference-deconstruction.md` region number for every value.
    - `design/reference-assets.md` — the manifest of the reference URL AND every one of the 13
      locally supplied asset files (see note below on the "8 vs 13" count correction).

  ## Correction carried forward from Strategist's handoff (non-blocking data note)
  Strategist's technical-specifications.md §6 says "8 supplied binaries" but its own itemized list
  (and the verified folder listing) is actually 13 files:
    about_innovation_banner_desktop_1920x1080.jpg (hero),
    TataChemicals_Desk.jpg, TataSteelEurope_Desk.jpg, TMETC_Desk.jpg,
    tcsinnovation_information_desktop_360x260.jpg (4 cards),
    tata-logo.svg, search.svg, video.svg, close.svg, ContactUs.svg, FB.svg, Instagram.svg,
    Linkedin.svg (logo + 7 icons).
  Use 13 as the authoritative count in `reference-assets.md`. `video.svg` and `close.svg` are not
  referenced by any US-### acceptance criterion (no video-modal / dismiss UI in this run's scope) —
  mark them explicitly `supplied, not-used-this-run` in the manifest rather than omitting them
  silently (so Composer still seeds them to the DAM for completeness, and Auditron/Sentinel don't
  flag them as an unexplained gap later).

  ## Outputs required from you (all under
  `runs/2026-08-28T1200Z-tata-innovation-page/design/`)
    - `component-specifications.md` — full contracts for site-header + site-footer (Sling Model
      accessor names, HTL semantic structure, BEM class names) PLUS the 4 Style System variant specs
      (selector name, CSS scope, computed-style Pixel-Verified Acceptance Criteria table per
      breakpoint, citing reference-deconstruction.md region numbers).
    - `dialog-specifications.md` — per-component dialog field layout for site-header (logo asset,
      nav root path + structureDepth, utility-link multifield with icon) and site-footer (link
      columns multifield [heading+links], social multifield [icon+url], legal text field).
    - `template-design.md` — `landing-page` template structure spec (page-level cq:policy, EF chrome
      refs, container proxy parsys, Style System hooks, cq:responsive breakpoints carried from
      reference-deconstruction.md, cq:allowedTemplates registration paths).
    - `policy-mapping.md` — every parsys/container area with EXPLICIT allowed components/groups (no
      `*`); content policy per component + Style System variant.
    - `authoring-guidelines.md` — how a content author fills in hero/intro/cards/header/footer.
    - `design-token-audit.md` — pinned tokens (per §P6 above).
    - `functional-test-cases.md` — test cases (`TC-###`) traced to every US-### acceptance criterion,
      including Sling Model unit-test cases for site-header/site-footer.
    - `ui-test-scenarios.md` — Playwright-ready scenarios (`UI-###`, framework-neutral) covering
      hero render, card grid responsive collapse, header/footer presence + mobile menu collapse,
      accessibility landmarks — these will be authored (not executed) by Blockwright and eventually
      executed by Sentinel against the real environment.
    - `authoring-test-cases.md` — authoring-provision / data-setup cases (`AUTH-###`) for the new
      site-header/site-footer dialogs + the landing-page template (required — this run creates new
      authoring surfaces).
    - `reference-assets.md` — the manifest described above (reference URL + all 13 local assets,
      each marked used/not-used-this-run with the component that consumes it).
    - `handoffs/designforge.yaml`

  ## Gate reminders
    - Every parsys/container area lists explicit components or groups — no `*`.
    - Every requirement ID (US-001..US-010) maps to >=1 functional test case.
    - Every visual/user-journey requirement has a UI-test scenario.
    - Dialog specs must be considered CONFIRMED by you before Blockwright scaffolds — do not leave
      ambiguous field types.
    - Output directory contains markdown only — no code.
    - No custom HTL/dialog proposed on top of a Core Component proxy for the 4 reuse items — Style
      System variants only.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\designforge.yaml

gate-criteria: |
  - Every component in the approved work breakdown has a spec + dialog spec (site-header,
    site-footer) OR a Style System variant spec (hero, intro, card-grid, card).
  - Dialog spec confirmed for every new component (no ambiguous field types).
  - Every parsys/container area in policy-mapping.md lists explicit components/groups — no wildcard.
  - Every requirement ID (US-001..US-010) maps to >=1 functional test case.
  - Every visual/user-journey requirement has a UI-test scenario.
  - design-token-audit.md pins concrete values (not ranges) for every token used by a Pixel-Verified
    Acceptance Criteria table.
  - reference-assets.md accounts for all 13 supplied local assets + the reference URL.
  - authoring-test-cases.md present (new authoring surfaces created this run).
  - Output directory contains markdown only.
