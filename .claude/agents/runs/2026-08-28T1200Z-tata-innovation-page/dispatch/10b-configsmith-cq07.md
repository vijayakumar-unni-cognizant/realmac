agent: configsmith
stage: Implement (remediation — CQ-07 policy re-keying, iteration 4 prep)
run-id: 2026-08-28T1200Z-tata-innovation-page
parallel-with: 10-blockwright-cq06-cq07.md (no file-level dependency — both consume the same
  human-approved spec; dispatch together)

input-packet: |
  You are `configsmith` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  Read `DECISIONS.md` in full first — it has the complete CQ-07 history and the human's approved
  plan revision.

  ## Context: CQ-07 fix (human-approved plan revision)
  AEM's runtime Content Policy resolution does not differentiate `realmac/components/teaser` at 2
  different author-droppable nesting depths (hero vs. card-grid) under the `landing-page` template's
  mapping tree — live-confirmed: all 4 cards resolve the hero's policy instead of their own. The
  human approved giving the cards a distinct resourceType,
  `realmac/components/innovation-card` (a thin proxy Blockwright is creating in parallel with this
  dispatch — `sling:resourceSuperType="realmac/components/teaser"`, no forked rendering). Your job:
  re-key the policy allowlist and mapping tree so that new resourceType resolves its own, correct
  policy — eliminating the depth-ambiguity entirely because hero (`teaser`) and card
  (`innovation-card`) are now distinct resourceTypes, not the same one at two depths.

  ## Fix 1 — `policy_landing_card_grid`'s allowlist
  File: `ui.content/src/main/content/jcr_root/conf/realmac/settings/wcm/policies/.content.xml`
  (the node `policy_landing_card_grid`, currently `components="[realmac/components/teaser]"`)

  Change the allowlist from `[realmac/components/teaser]` to `[realmac/components/innovation-card]`
  — the card-grid container should now only accept the new proxy, not the plain teaser (this also
  prevents an author from ever again dropping a plain teaser into the card-grid and re-triggering the
  exact depth-ambiguity this fix eliminates).

  ## Fix 2 — `landing-page`'s policy mapping tree, card-grid segment
  File: `ui.content/src/main/content/jcr_root/conf/realmac/settings/wcm/templates/landing-page/policies/.content.xml`

  Currently, inside the card-grid container's own `<realmac><components>` fallback segment, there is
  a `<teaser cq:policy="realmac/components/teaser/policy_landing_card_teaser" .../>` entry. Change
  this entry's ELEMENT NAME (the resourceType-fallback key) from `teaser` to `innovation-card`, and
  update its `cq:policy` value to point at a policy path under the NEW resourceType's own namespace:
  `realmac/components/innovation-card/policy_landing_card_teaser` (i.e., the policy definition itself
  moves from being keyed under `teaser/` to being keyed under `innovation-card/` in the consolidated
  policies file — see Fix 3 below). Do NOT change the outer parsys-level `<teaser
  cq:policy="realmac/components/teaser/policy_landing_hero_teaser".../>` entry (the hero's own
  mapping, one level up, at the editable-parsys's own `<realmac><components>` segment) — that one
  must remain exactly as-is; the hero still uses plain `realmac/components/teaser`.

  ## Fix 3 — move `policy_landing_card_teaser`'s definition to the new resourceType's node
  File: `ui.content/src/main/content/jcr_root/conf/realmac/settings/wcm/policies/.content.xml`

  The policy definition itself (`policy_landing_card_teaser` — `titleType=h3`, `allowedTypes=[h3]`,
  `imageDelegate=realmac/components/image`, `cq:styleGroups` with `cmp-teaser--innovation-card` /
  `styleId=20260828104`) currently lives nested under the `teaser` component node in this file. Move
  (not duplicate) that entire policy node so it lives under an `innovation-card` component node
  instead, mirroring the same nesting convention the file already uses for `teaser`/`text`/
  `container`/`button` (i.e., add a sibling `<innovation-card><policy_landing_card_teaser
  .../></innovation-card>` node at the same level as the existing `<teaser>` node, and remove the
  old `policy_landing_card_teaser` definition from under `<teaser>` once moved — do not leave two
  copies). Leave `policy_landing_hero_teaser` exactly where it is, under `<teaser>` — only
  `policy_landing_card_teaser` moves.

  ## Verify — hero mapping untouched
  After your changes, confirm by re-reading both files yourself: the editable parsys's own
  `<realmac><components><teaser cq:policy=".../policy_landing_hero_teaser"/>` entry (one level up
  from the card-grid) is completely unchanged, still keys on plain `teaser`, and still resolves to
  the hero policy. State this confirmation explicitly in your handoff.

  ## Do NOT touch
    - Any `ui.apps`/`core`/`ui.frontend` file (Blockwright's parallel task).
    - The sample page content (Composer's task, sequenced after both of you — it needs your policy
      changes in place first, or its resourceType change wouldn't discharge CQ-07 even though the
      content would still deploy).
    - `pom.xml`, `ui.apps/pom.xml`, `ui.frontend/pom.xml` (CQ-03, still out of scope for everyone).
    - `filter.xml` — no packaging change is needed for this fix (same file, same package coverage).

  ## After your fixes
    - Do NOT run `mvn` yourself.
    - Update `handoffs/configsmith.yaml` with a `remediation` block covering: the exact diff for all
      3 fixes above, the hero-mapping-untouched confirmation, and explicit confirmation you touched
      nothing outside this scope.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\configsmith.yaml

gate-criteria: |
  - policy_landing_card_grid's allowlist is exactly [realmac/components/innovation-card] (teaser
    removed from this specific allowlist).
  - The card-grid's nested policy-mapping segment keys on innovation-card (not teaser), pointing at
    realmac/components/innovation-card/policy_landing_card_teaser.
  - policy_landing_card_teaser's definition now lives under an <innovation-card> node in the
    consolidated policies file, not duplicated under both <teaser> and <innovation-card>.
  - The hero's own mapping (outer parsys level, plain teaser -> policy_landing_hero_teaser) is
    verified unchanged.
  - No ui.apps/core/ui.frontend/pom.xml/filter.xml files touched.
  - No mvn invocation.
  - handoffs/configsmith.yaml updated.
