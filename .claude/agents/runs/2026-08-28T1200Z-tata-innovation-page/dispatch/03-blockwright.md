agent: blockwright
stage: Implement (code/build branch)
run-id: 2026-08-28T1200Z-tata-innovation-page
parallel-with: 03b-configsmith.md (no dependency between the two — dispatch together)

input-packet: |
  You are `blockwright` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`
  in the `realmac` AEMaaCS project (repo root `C:\AEM\Repos\realmac`, branch
  `feature/realmac-landing-page`). Human Checkpoint 2 (dialog spec confirmation) has been APPROVED
  AS-IS — the dialog specs below are locked, do not re-propose or re-prompt for them.

  ## Required reading before you start
    - `C:\AEM\Repos\realmac\.aem-skills-config.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/component-specifications.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/dialog-specifications.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/template-design.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/policy-mapping.md` (for context on what
      Configsmith is authoring in parallel — you do NOT author policies yourself, but your template
      `structure/` must be consistent with the policy paths it references)
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/design-token-audit.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/ui-test-scenarios.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/plan/reference-deconstruction.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/designforge.yaml`

  ## Task — 4 tracks

  ### Track 1: Components — `site-header` / `site-footer` (net-new, group "Realmac - Structure")
  Author under `ui.apps/src/main/content/jcr_root/apps/realmac/components/`:
    - `site-header/` — `.content.xml` (componentGroup="Realmac - Structure"), `_cq_dialog/` per
      `dialog-specifications.md § site-header` EXACTLY as specified (3 tabs: Logo/Navigation/Utility
      Links; field names, resource types, required flags all pinned — do not deviate), HTL rendering
      via `com.realmac.aem.core.models.SiteHeaderModel` (accessors:
      getLogoFileReference, getLogoAlt, getLogoLinkURL, getNavigationRoot,
      getNavigationStructureDepth, getUtilityLinks, hasContent — exact names, per
      `handoffs/designforge.yaml § components_specified`).
    - `site-footer/` — same pattern, `_cq_dialog/` per `dialog-specifications.md § site-footer` (3
      tabs: Columns/Social/Legal), Sling Model `com.realmac.aem.core.models.SiteFooterModel`
      (accessors: getFooterLogoFileReference, getColumns, getSocialLinks, getLegalText, hasContent).
    - Sling Models under `core/src/main/java/com/realmac/aem/core/models/` implementing standard
      `@Model` adaptables (`Resource` or `SlingHttpServletRequest` per project convention — check an
      existing model for the pattern), `@ValueMapValue`/`@ChildResource` mapped to the exact dialog
      field JCR names in `dialog-specifications.md`.
    - wcm.io AEM Mocks unit tests for both models under `core/src/test/java/com/realmac/aem/core/models/`
      — cover: field population, multifield/nested-multifield mapping (utilityLinks; columns with
      nested links; socialLinks), `hasContent`/empty-state behavior, default values (e.g.
      navigationStructureDepth default 1). These discharge functional test cases TC-038..TC-045
      (US-009) from `functional-test-cases.md` — author tests that make those IDs genuinely pass,
      not just compile.
    - HTL templates emit BEM classes matching `component-specifications.md` (e.g. `.cmp-site-header`,
      `.cmp-site-header__logo`, `.cmp-site-header__nav`, `.cmp-site-header__utility-links`, and the
      `site-footer` equivalents) — these are what your SCSS in Track 3 will target.
    - The embedded navigation inside `site-header` is a **synthetic HTL resource** per
      `component-specifications.md § A.1` — it does not go through policy resolution; implement
      exactly as specified there (do not introduce a real child resource requiring its own policy).

  ### Track 2: `landing-page` editable template — STRUCTURE only (Configsmith owns policies)
  Author under `ui.content/src/main/content/jcr_root/conf/realmac/settings/wcm/templates/landing-page/`:
    - `structure/.content.xml` — copy `template-design.md § Structure` VERBATIM (the XML block is
      already fully specified — root container, EF header ref, main-landmark container, innermost
      editable container at `root/container/container`, EF footer ref, `cq:responsive` breakpoints
      phone=768/tablet=1200). Do NOT add a structural `<title>` node (D22 — absent by design).
    - `.content.xml` (template definition) — `allowedPaths="[/content/realmac(/.*)?]"`, template type
      reference to the existing `/conf/realmac/settings/wcm/template-types/page`.
    - `initial/.content.xml` — minimal initial content matching `structure/` (no authored components;
      Composer authors the sample page content later).
    - Verify the forbidden-attributes checklist in `template-design.md § Structure` (no
      `editable=true` on root, no `editable=false`/`decoration=false` on EF refs, only the innermost
      container is editable=true).
    - **Do NOT author `policies/` under this template directory or under
      `settings/wcm/policies/jcr:content/realmac/components/...`** — that is Configsmith's track,
      dispatched in parallel. If you need a policy path to exist for a smoke test, reference it by
      name only; do not create the policy node yourself (avoids a merge collision with Configsmith).

  ### Track 3: ui.frontend SCSS — 4 Style System variants + 2 chrome components
  Author/extend SCSS partials in `ui.frontend/src/main/webpack/` (exact path — check existing
  `_teaser.scss`/`_container.scss`/`_text.scss` conventions and follow them):
    - `cmp-teaser--hero` — full-bleed image, dark scrim overlay, large white title, per
      `design-token-audit.md` pinned values (colors, hero title sizes at 1440px/390px, scrim
      rgba, breakpoint behavior) and `component-specifications.md`'s Pixel-Verified Acceptance
      Criteria tables. NO CTA styling needed (actions disabled by policy).
    - `cmp-text--intro-lead` — lead-paragraph vs body-paragraph typography split (first-child
      selector or similar CSS-only mechanism — no HTL change, the split must be automatic per
      `authoring-guidelines.md § 4`), centered column max-width ~900px per token audit.
    - `cmp-container--card-grid` — CSS Grid, 2 columns desktop/tablet, 1 column <768px, gap per
      token audit.
    - `cmp-teaser--innovation-card` — image-top card, title, optional descriptor, arrow-style CTA
      link (style the existing `.cmp-teaser__action` element — do not restructure the Core Teaser
      DOM), card radius/border per token audit (note: 6px radius, a deliberate deviation from the
      generic 16px default — this is intentional, keep it).
    - `_site-header.scss` / `_site-footer.scss` — new partials for the 2 net-new components, BEM
      classes matching your HTL output in Track 1, responsive collapse per
      `reference-deconstruction.md` Region 1 (header, hamburger <768px) and Region 5 (footer,
      column stacking 4→2→1).
    - Import all new partials into the project's main SCSS entry point.
    - Selectors target Core Component output classes only (`.cmp-teaser`, `.cmp-container`,
      `.cmp-image`, `.cmp-text`) plus your own new BEM classes for the 2 chrome components — no DOM
      restructuring, no `!important` unless an existing project convention already uses it.

  ### Track 4: Playwright spec AUTHORING (pre-deploy) — do NOT execute
  In `ui.tests/`:
    - If the harness is Cypress-based or missing, migrate/scaffold to Playwright (once per project —
      check current state first and report `harness_state_on_entry` in your handoff).
    - Author one `*.spec.js`/`*.spec.ts` per scenario ID in `design/ui-test-scenarios.md` (UI-001
      through UI-018), parameterized for author + publish tiers (do not hardcode a URL — use an env
      var / config the way the existing harness does).
    - Validate ONLY via `npx playwright test --list` (static validation — no live env, no `mvn`,
      you do not execute the suite).
    - Ensure `cypress_fully_removed: true` if a migration happened, and record
      `scenario_coverage.unmapped: []` in your handoff (every UI-### ID has a corresponding spec).

  ## Gate reminders (do not violate)
    - `.aem-skills-config.yaml` must read `configured: true` (it does).
    - Dialog specs are already confirmed — do not re-prompt or alter field names/types.
    - No HTL file embeds runtime clientlibs directly.
    - No per-component runtime clientlib folder under `ui.apps/.../clientlibs/` (site.css only, via
      ui.frontend's compiled clientlib).
    - SCSS uses BEM + existing `_variables.scss` design tokens where they overlap with
      `design-token-audit.md`'s pinned values; introduce new tokens only for values not already
      covered by project variables.
    - No deprecated AEM API (re-scan with `best-practices` skill before finishing).
    - Configs (if any OSGi config is genuinely needed — none expected this run) land in
      `ui.config`, never `ui.apps`.
    - You do NOT invoke `mvn` — build verification is Auditron's job (stage 05).
    - You do NOT author `ui.content` policies (Configsmith's parallel track) or DAM/page content
      (Composer's sequenced-after track).

  ## Outputs required from you
    - Components under `ui.apps/src/main/content/jcr_root/apps/realmac/components/{site-header,site-footer}/`
    - Sling Models + unit tests under `core/src/main/java/...` and `core/src/test/java/...`
    - `landing-page` template structure under `ui.content/.../settings/wcm/templates/landing-page/`
    - SCSS partials under `ui.frontend/src/main/webpack/...`
    - Playwright specs under `ui.tests/test-module/tests/` (or the project's existing UI-test path)
    - `runs/2026-08-28T1200Z-tata-innovation-page/implement/blockwright/` — reuse decisions, any
      before/after notes
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/blockwright.yaml` — MUST include
      `ui_tests.harness_state_on_entry`, `cypress_fully_removed`, `scenario_coverage.unmapped`,
      and `runtime_style_system_classes: verified` (or a documented skip reason — local SDK
      unreachable is acceptable at this stage since Auditron does the actual local install).

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\blockwright.yaml

gate-criteria: |
  - site-header + site-footer components exist with HTL + dialog (matching dialog-specifications.md
    exactly) + Sling Model + passing wcm.io AEM Mocks unit test.
  - landing-page template structure matches template-design.md verbatim (no structural title node,
    forbidden-attributes checklist satisfied).
  - 4 Style System SCSS variants + 2 chrome SCSS partials authored, BEM-based, no DOM restructuring.
  - Playwright specs exist 1:1 with design/ui-test-scenarios.md IDs (UI-001..UI-018), statically
    validated via `npx playwright test --list`, not executed.
  - No mvn invocation by blockwright.
  - No policy/DAM/page-content authored by blockwright (those are configsmith's / composer's tracks).
  - handoffs/blockwright.yaml present with all required fields.
