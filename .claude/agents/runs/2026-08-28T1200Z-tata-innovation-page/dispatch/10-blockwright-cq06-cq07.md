agent: blockwright
stage: Implement (remediation — CQ-06 HTL fix + CQ-07 new proxy component, iteration 4 prep)
run-id: 2026-08-28T1200Z-tata-innovation-page
parallel-with: 10b-configsmith-cq07.md (no file-level dependency — both consume the same
  human-approved spec; dispatch together)

input-packet: |
  You are `blockwright` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  Two fixes this dispatch. Read `DECISIONS.md` in full first — it has the complete CQ-06/CQ-07
  history and the human's approved plan revision.

  ## Fix 1 — CQ-06 (HIGH): footer link columns render empty
  File: `ui.apps/src/main/content/jcr_root/apps/realmac/components/site-footer/site-footer.html`

  Current (buggy):
  ```
  <nav class="cmp-site-footer__column" data-sly-repeat.column="${model.columns}"
       data-sly-test="${column.hasContent}" aria-label="${column.heading}">
    ...
    <li data-sly-repeat.link="${column.links}" data-sly-test="${link.hasContent}">
  ```
  Per HTL block-statement precedence, `data-sly-test` is evaluated BEFORE `data-sly-repeat` binds
  its loop variable when both sit on the same element — so `column`/`link` don't exist yet at the
  point the test runs, and the entire block is suppressed. Confirmed live: the columns div renders
  completely empty.

  **Fix:** mirror the ALREADY-CORRECT idiom used 12 lines below for the social list (`<ul
  data-sly-list.social="${model.socialLinks}"><li data-sly-test="${social.hasContent}">`) — split
  the test onto an element NESTED INSIDE the repeat, not the same element:
  ```
  <nav class="cmp-site-footer__column" data-sly-repeat.column="${model.columns}"
       aria-label="${column.heading}">
      <sly data-sly-test="${column.hasContent}">
          <h3 class="cmp-site-footer__column-heading">${column.heading}</h3>
          <ul class="cmp-site-footer__link-list">
              <li data-sly-repeat.link="${column.links}">
                  <sly data-sly-test="${link.hasContent}">
                      <a class="cmp-site-footer__link" href="${link.url}">${link.label}</a>
                  </sly>
              </li>
          </ul>
      </sly>
  </nav>
  ```
  (Use `<sly>` — a non-rendering HTL element — so no extra wrapper tag is introduced; adjust exactly
  to match your project's existing `<sly>` usage conventions if there's an established pattern
  elsewhere in this codebase.) Do not change anything else in this file.

  ## Fix 2 — CQ-07 (HIGH, human-approved plan revision): new proxy component
  `realmac/components/innovation-card`

  AEM's runtime Content Policy resolution does not differentiate `realmac/components/teaser` at 2
  different author-droppable nesting depths (hero vs. card-grid) under the `landing-page` template's
  mapping tree — confirmed via live evidence (all 4 cards render the hero's policy: h1 heading, zero
  `.cmp-teaser--innovation-card` styling). The human approved giving the cards a DISTINCT
  `resourceType` so their own policy resolves unambiguously, via a thin proxy — NOT a fork.

  **Create** `ui.apps/src/main/content/jcr_root/apps/realmac/components/innovation-card/.content.xml`:
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0"
      xmlns:jcr="http://www.jcp.org/jcr/1.0"
      jcr:primaryType="cq:Component"
      jcr:title="Innovation Card"
      sling:resourceSuperType="realmac/components/teaser"
      componentGroup="Realmac - Content"/>
  ```
  Verified for you already (Program Agent, recorded in DECISIONS.md): `realmac/components/teaser`
  itself has `sling:resourceSuperType="core/wcm/components/teaser/v2/teaser"` and
  `imageDelegate="realmac/components/image"`. Point `innovation-card`'s `resourceSuperType` at
  `realmac/components/teaser` (the PROJECT's own proxy) — NOT directly at Core's v2 teaser — so it
  transparently inherits the project's `imageDelegate` override and any future project-level teaser
  customization. This is a PURE proxy: **no HTL file, no `_cq_dialog`, no Sling Model, no dialog of
  its own** — it inherits 100% of `realmac/components/teaser`'s rendering, dialog, and Java model via
  the resourceSuperType chain. Do not author anything beyond the one `.content.xml` file above
  unless you find a concrete reason a proxy-only definition is insufficient (if so, explain why in
  your handoff before adding anything).

  **SCSS impact — confirm, don't re-author:** the existing `cmp-teaser--innovation-card` SCSS partial
  (authored earlier this run) targets Core Teaser's own output classes (`.cmp-teaser`,
  `.cmp-teaser__image`, etc., restyled under a `.cmp-teaser--innovation-card` style-variant class).
  Since `innovation-card` inherits `realmac/components/teaser`'s HTL verbatim (which itself inherits
  Core Teaser's HTL), the RENDERED DOM output for an `innovation-card` instance will emit the exact
  same `.cmp-teaser`/`.cmp-teaser__*` BEM classes — the existing SCSS needs NO changes. Confirm this
  in your handoff rather than assuming; flag clearly if you find any reason the proxy's rendered
  output would differ from a plain teaser's.

  **Unit test:** since this is a pure proxy with no new Java code or dialog, no new Sling Model unit
  test is needed. If you judge some other test adds real value (e.g. a component-registration smoke
  check), your call — state your reasoning either way in the handoff.

  ## Do NOT touch
    - `pom.xml`, `ui.apps/pom.xml`, `ui.frontend/pom.xml` (CQ-03, still out of scope for everyone).
    - Any policy XML (Configsmith's parallel task — dispatched separately this same round).
    - The sample page content (Composer's task, sequenced after both of you).
    - `site-header.html` or any other component file.

  ## After both fixes
    - Do NOT run `mvn` yourself.
    - Update `handoffs/blockwright.yaml` with a `remediation_iteration_3` (or similarly-named) block
      covering: the CQ-06 HTL diff, the new innovation-card component's exact file(s), the
      SCSS-impact confirmation, and explicit confirmation you touched nothing outside this scope.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\blockwright.yaml

gate-criteria: |
  - site-footer.html: data-sly-test no longer shares an element with data-sly-repeat for either the
    column <nav> or the link <li>; mirrors the social-list's existing correct idiom.
  - realmac/components/innovation-card exists with sling:resourceSuperType="realmac/components/teaser"
    and nothing else beyond a minimal .content.xml (no forked HTL/dialog/model), unless explicitly
    justified otherwise in the handoff.
  - SCSS-impact confirmation stated explicitly (existing .cmp-teaser--innovation-card partial needs
    no changes, or a clear explanation if it does).
  - No policy XML, no sample-page content, no pom.xml files touched.
  - No mvn invocation.
  - handoffs/blockwright.yaml updated.
