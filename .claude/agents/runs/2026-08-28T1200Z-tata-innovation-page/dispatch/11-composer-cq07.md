agent: composer
stage: Integrate (remediation — CQ-07 content re-typing, sequenced after blockwright + configsmith)
run-id: 2026-08-28T1200Z-tata-innovation-page
status: WRITTEN BUT NOT YET DISPATCHED — held pending BOTH dispatch/10-blockwright-cq06-cq07.md and
  dispatch/10b-configsmith-cq07.md passing their gates. Do not emit the DISPATCH-REQUEST for this
  packet until the Program Agent has validated both on disk.

input-packet: |
  You are `composer` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  Blockwright has created `realmac/components/innovation-card` (a thin proxy of
  `realmac/components/teaser`) and fixed CQ-06. Configsmith has re-keyed the card-grid's policy
  allowlist and mapping tree to use `innovation-card` instead of `teaser`. Your job: update the
  sample page's 4 card nodes to use the new resourceType.

  ## Fix — CQ-07 content re-typing
  File: `ui.content/src/main/content/jcr_root/content/realmac/us/en/innovation/.content.xml`

  The 4 card-grid siblings (currently named `teaser`, `teaser_1`, `teaser_2`, `teaser_3` after the
  earlier CQ-05 fix) each have `sling:resourceType="realmac/components/teaser"`. Change ONLY that
  one attribute on all 4 to `sling:resourceType="realmac/components/innovation-card"`. Preserve
  EVERYTHING else exactly: the node names (or rename to `card_0`/`card_1`/`card_2`/`card_3` if you
  judge that's a clearer name now that they're no longer literally teasers — your call, either is
  fine as long as the 4 names stay unique and the authored order is preserved), every property
  (`jcr:title`, `jcr:description`, `fileReference`, `imageAlt`, `actionsEnabled`,
  `cq:styleIds="[20260828104]"`), and each one's nested `<actions>` child with its `item0`
  (`text`, `link`). Do NOT touch the hero teaser (different node, different parent, still
  `realmac/components/teaser` — unaffected by this change) or any other node in this file.

  ## Verify before you finish
    - Confirm `realmac/components/innovation-card` actually exists on disk (Blockwright's output)
      before making this change — if it's missing, STOP and report back rather than authoring a
      dangling resourceType reference.
    - Confirm `policy_landing_card_grid`'s allowlist and the card-grid's policy-mapping segment
      (Configsmith's output) actually key on `innovation-card` now — if they still say `teaser`,
      STOP and report back; your content change would deploy but wouldn't discharge CQ-07.
    - Confirm `filter.xml` needs NO change for this fix (the new component lives under `ui.apps`,
      packaged by its own module's default coverage, not `ui.content`'s filter.xml; the sample page
      itself is already covered by the existing broad `/content/realmac` merge root, unaffected by
      this content-only edit). State this confirmation explicitly, don't just assume it.

  ## Do NOT touch
    - Any `ui.apps`/`core`/`ui.frontend` file.
    - Any policy XML (already done by Configsmith).
    - `pom.xml`, `ui.apps/pom.xml`, `ui.frontend/pom.xml`, `filter.xml` (out of scope for this fix).

  ## After your fix
    - Do NOT run `mvn` yourself.
    - Update `handoffs/composer.yaml` with a `remediation` block: the exact resourceType diff for
      all 4 cards, confirmation of the 2 pre-checks above, and confirmation you touched nothing else.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\composer.yaml

gate-criteria: |
  - All 4 card nodes have sling:resourceType="realmac/components/innovation-card"; hero teaser
    unchanged; all other properties/children/order preserved exactly.
  - Composer confirmed (not assumed) that innovation-card exists and the policy re-keying landed
    before making this change.
  - filter.xml confirmed unchanged (no edit needed) with explicit reasoning stated.
  - No ui.apps/core/ui.frontend/pom.xml files touched.
  - No mvn invocation.
  - handoffs/composer.yaml updated.
