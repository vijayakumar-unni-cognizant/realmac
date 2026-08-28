agent: blockwright
stage: Implement (remediation re-dispatch, iteration 2 on the stage-05 gate)
run-id: 2026-08-28T1200Z-tata-innovation-page

input-packet: |
  You are `blockwright` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  Auditron's Build Validation Gate FAILED (iteration 1) with 2 findings that are yours to fix. A 3rd
  finding (CQ-03) was investigated and RESOLVED by the human — it is explicitly NOT yours to touch.
  Read this whole packet before making any change.

  ## Context you need: CQ-03 is CLOSED, do not touch these 3 files
  The human confirmed that `pom.xml` (root), `ui.apps/pom.xml`, and `ui.frontend/pom.xml` already
  carry 3 PRE-EXISTING, INTENTIONAL, human-authored, LOCAL-BUILD-ONLY edits (this local dev
  environment sits behind a Zscaler proxy that blocks the frontend-maven-plugin's node/npm download;
  the human manually builds `ui.frontend` and syncs the compiled clientlib into `ui.apps` out of
  band). These are NOT a defect and NOT yours to fix or revert. You MUST NOT edit `pom.xml` (root)
  or `ui.frontend/pom.xml` at all in this dispatch. Whether you may touch a small piece of
  `ui.apps/pom.xml` depends entirely on which CQ-02 option you use — see below; `ui.apps/pom.xml`
  ALSO carries one of the 3 local-only hacks (a commented-out `com.realmac:realmac.ui.frontend:zip`
  dependency), so any edit you make there must be surgical and must not touch that commented-out
  dependency block.

  ## Fix 1 — CQ-01 (HIGH): data-sly-list misuse in site-footer.html
  File: `ui.apps/src/main/content/jcr_root/apps/realmac/components/site-footer/site-footer.html`

  Current (buggy):
  ```
  <nav class="cmp-site-footer__column" data-sly-list.column="${model.columns}"
       data-sly-test="${column.hasContent}" aria-label="${column.heading}">
    ...
    <li data-sly-list.link="${column.links}" data-sly-test="${link.hasContent}">
  ```
  `data-sly-list` repeats only the element's CONTENT, not the host element — placed directly on
  `<nav>`/`<li>` (the elements that need to repeat N times), it collapses all columns into one
  `<nav>` and all links into one `<li>`.

  Fix: change both occurrences to `data-sly-repeat` (which DOES repeat the host element):
  ```
  <nav class="cmp-site-footer__column" data-sly-repeat.column="${model.columns}"
       data-sly-test="${column.hasContent}" aria-label="${column.heading}">
    ...
    <li data-sly-repeat.link="${column.links}" data-sly-test="${link.hasContent}">
  ```
  Do not change anything else in this file. `site-header.html`'s own `data-sly-list.link` (on the
  `<ul>` wrapper, with the `<li>` repeating as the wrapper's content) is the CORRECT idiom already —
  do not touch it.

  ## Fix 2 — CQ-02 (HIGH): HTL unknown-option build failure on site-header.html
  Root cause: `site-header.html` embeds `realmac/components/navigation` via a synthetic resource
  with custom `data-sly-resource` options:
  ```
  <div data-sly-resource="${'navigation' @ resourceType='realmac/components/navigation',
                             navigationRoot=model.navigationRoot,
                             structureDepth=model.navigationStructureDepth}"></div>
  ```
  `navigationRoot`/`structureDepth` are not declared in `ui.apps/pom.xml`'s htl-maven-plugin
  `allowedExpressionOptions`, so validation (warnings-as-errors) rejects them as unknown options.

  **The human has a STRONG PREFERENCE for Option B below — attempt it first.** Reason: `ui.apps/pom.xml`
  also carries one of the 3 untouchable local-only hacks (see above), so entangling a legitimate pom
  edit into that same file creates a PR-hygiene problem later (Pilot would have to cherry-pick one
  hunk of that file and exclude another). Option B avoids touching any pom entirely.

  ### Option B (PREFERRED) — refactor the HTL so no custom data-sly-resource option is needed
  Change the approach so `site-header.html`'s embed of the navigation component requires zero custom
  `@`-options. The standard AEMaaCS idiom for this: have `SiteHeaderModel` (the Sling Model, not the
  HTL) construct and expose a ready-made `Resource` for the embedded navigation — e.g. a
  `ResourceWrapper` (or a hand-built `ValueMap`-backed synthetic resource) whose `ValueMap` ALREADY
  contains `navigationRoot` and `structureDepth` as if they had been authored directly on that
  resource, plus the correct `sling:resourceType` (`realmac/components/navigation`). Expose it via a
  new accessor, e.g. `getNavigationResource()` returning `org.apache.sling.api.resource.Resource`.
  Then the HTL becomes:
  ```
  <div data-sly-resource="${model.navigationResource}"></div>
  ```
  — a plain resource reference, no `@`-options block at all, so the htl-maven-plugin's
  `allowedExpressionOptions` validation is never triggered for this line. Add a focused unit test
  (wcm.io AEM Mocks) asserting the returned resource's `ValueMap` carries the correct
  `navigationRoot`/`structureDepth`/`sling:resourceType` values, and that adapting that resource to
  the navigation component's own Sling Model (if it's Java-backed) or rendering it resolves the
  intended `realmac/components/navigation` script.

  If, after attempting this, you find it is NOT cleanly achievable in this codebase's actual idiom
  (e.g. `realmac/components/navigation` truly cannot be driven by a synthetic resource for some
  concrete technical reason you discover) — fall back to Option A, but explain in your handoff
  exactly why Option B did not work.

  ### Option A (FALLBACK ONLY) — declare the options in ui.apps/pom.xml
  Add `navigationRoot` and `structureDepth` to the existing `<allowedExpressionOptions>` block in
  `ui.apps/pom.xml`'s htl-maven-plugin `validate-htl-scripts` execution (currently: `cssClassName`,
  `decoration`, `decorationTagName`, `wcmmode`). This is a SURGICAL, single-block edit — do NOT
  touch, re-indent, or otherwise disturb the nearby commented-out `<dependency>` block for
  `com.realmac:realmac.ui.frontend:zip` elsewhere in that same file (that is one of the 3 untouchable
  local-only hacks). If you use this fallback, say so explicitly and clearly in your handoff, and
  flag precisely which lines you changed so Pilot can later stage only that hunk when it commits the
  PR (Pilot has separately been told about this constraint).

  ## Do NOT touch (repeated for emphasis)
    - `pom.xml` (root) and `ui.frontend/pom.xml` — CQ-03, human-confirmed local-only, out of scope.
    - The commented-out `realmac.ui.frontend` zip `<dependency>` block in `ui.apps/pom.xml` — same.
    - Any file outside `site-header.html`, `site-footer.html`, `SiteHeaderModel.java` (only if you
      use Option B), and (only if you fall back to Option A) the one `<allowedExpressionOptions>`
      block in `ui.apps/pom.xml`.

  ## After your fixes
    - Do NOT run `mvn` yourself — Auditron re-verifies in the next stage-05 dispatch.
    - You MAY run a syntax-only SCSS check if useful (e.g. `npx sass --no-source-map <file> -` as a
      dry-run) but do NOT attempt a full npm install / webpack build — that is Zscaler-blocked in
      this environment and is the human's own out-of-band manual step, not yours.
    - Update `handoffs/blockwright.yaml` with a `remediation` block: which option you used for CQ-02
      (B or A, with reasoning if you fell back to A), exact diff summary for both fixes, and explicit
      confirmation you did not touch any of the 3 untouchable files/blocks.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\blockwright.yaml

gate-criteria: |
  - site-footer.html: both data-sly-list occurrences on <nav> and <li> changed to data-sly-repeat;
    site-header.html's own data-sly-list (on the <ul>) left unchanged.
  - CQ-02 resolved via Option B (preferred: SiteHeaderModel exposes a ready-made Resource, HTL uses
    a plain data-sly-resource reference with no @-options) OR, only as a documented fallback, Option
    A (ui.apps/pom.xml allowedExpressionOptions gets exactly 2 new entries, nothing else in that file
    touched).
  - No edits to root pom.xml or ui.frontend/pom.xml.
  - No edit to the commented-out realmac.ui.frontend dependency block in ui.apps/pom.xml.
  - No mvn invocation, no npm install / webpack build, by blockwright.
  - handoffs/blockwright.yaml updated with a remediation block naming which CQ-02 option was used.
