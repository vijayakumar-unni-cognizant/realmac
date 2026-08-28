agent: composer
stage: Integrate (remediation re-dispatch — CQ-05 content fix + CQ-04 filter.xml fix, consolidated)
run-id: 2026-08-28T1200Z-tata-innovation-page

input-packet: |
  You are `composer` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`.
  Auditron's iteration-2 Build Gate re-run found 2 HIGH findings in your prior output. Both are
  yours to fix. The human has approved the filter.xml approach below — read this whole packet before
  making any change; it also carries a completeness check you must perform, not skip.

  ## Fix 1 — CQ-05 (HIGH): duplicate sibling node names collapse 3 of 4 showcase cards
  File: `ui.content/src/main/content/jcr_root/content/realmac/us/en/innovation/.content.xml`

  The card-grid container currently authors 4 sibling elements ALL literally named `<teaser>`:
  ```
  <container ... cq:styleIds="[20260828103]">
      <teaser jcr:title="Tata Chemicals Innovation Centre" .../>
      <teaser jcr:title="Tata Steel Europe RD&amp;T" .../>
      <teaser jcr:title="TCS Innovation Labs" .../>
      <teaser jcr:title="Tata Motors European Technical Centre" .../>
  </container>
  ```
  This is invalid same-name-sibling usage for DocView import into AEMaaCS's Oak-based repository —
  on import, later same-named siblings silently overwrite earlier ones; only the last one (Tata
  Motors ETC) survives.

  **Fix:** rename the 4 card elements to unique node names, e.g. `teaser`, `teaser_1`, `teaser_2`,
  `teaser_3` (matching how AEM's own authoring UI names successive same-resourceType siblings).
  Preserve EVERY property and child EXACTLY as authored — `jcr:title`, `jcr:description`,
  `fileReference`, `imageAlt`, `actionsEnabled`, `cq:styleIds="[20260828104]"`, and each one's nested
  `<actions>` child with its `item0` (`text`, `link`) — and preserve the AUTHORED ORDER (Tata
  Chemicals, Tata Steel Europe, TCS Innovation Labs, Tata Motors ETC). Do NOT touch the hero teaser
  (a different node, under a different parent, not affected by this bug) or any other node in this
  file.

  ## Fix 2 — CQ-04 (HIGH): mode="merge" filter roots block deployment of mutations to pre-existing
  content
  File: `ui.content/src/main/content/META-INF/vault/filter.xml`

  Root cause (human-approved Approach A): the 3 existing broad `mode="merge"` filter roots
  (`/conf/realmac`, `/content/realmac`, `/content/experience-fragments/realmac`) do not reliably (a)
  apply property updates to already-existing nodes, or (b) install new sibling nodes serialized in
  the SAME single docview file as an already-existing parent. This blocks: the 6 new content
  policies + the XF-Root policy amendment (both live in the shared, pre-existing
  `settings/wcm/policies` file), and the two XF master `jcr:content` replacements (which need old
  legacy children actually REMOVED, not merge-preserved).

  **Human-approved fix:** add narrowly-scoped `mode="replace"` filter entries at EXACTLY the specific
  paths this run changes — leaving the 3 broad existing merge roots, and your own Stage-04 DAM filter
  entry (`/content/dam/realmac/tata-innovation`), completely UNTOUCHED.

  **You must perform a completeness check, not just add the 3 originally-proposed entries blindly.**
  Go through EACH of the following 5 areas and decide, with your own reasoning (cross-reference the
  live evidence below), whether it needs an explicit new filter entry or already deploys correctly
  under the existing broad merge roots:

  1. **The 6 new content policies + XF-Root amendment** (`settings/wcm/policies` subtree, specifically
     the `jcr:content/realmac` mapping node inside it). This is CONFIRMED BROKEN by Auditron's live
     evidence (5/6 policies 404, XF-Root amendment not live) — NEEDS an explicit entry. Use
     `mode="replace"` scoped as narrowly as you can (ideally at
     `/conf/realmac/settings/wcm/policies/jcr:content/realmac` or narrower, NOT the whole
     `/conf/realmac` tree) — narrow enough that it does not touch any policy content outside what
     this run added/amended, but broad enough to cover the property update on the pre-existing
     XF-Root node.

  2. **The `landing-page` template subtree** (`settings/wcm/templates/landing-page`, including its
     own nested `templates/landing-page/policies/.content.xml` mapping file). Auditron's iteration-2
     TC-027 live evidence: `status=enabled`, `allowedPaths` matches — the template's own status and
     path restriction ARE live and correct. Investigate whether this is because the ENTIRE
     `landing-page` path is a brand-new node (unlike the shared `policies` file, which mixes new
     content into an ALREADY-existing node) — if so, it likely deploys fine under the existing broad
     `/conf/realmac` merge root with NO new filter entry needed. Confirm this by also checking
     whether the template's card-grid/hero/intro/card-teaser POLICY REFERENCES actually resolve live
     (they may still 404 if they point at the shared `policies` file's new nodes from area 1 above —
     that would be area 1's bug surfacing here, not a separate template-subtree bug). Do NOT add a
     redundant filter entry for `landing-page` itself unless your own investigation shows it is
     genuinely needed.

  3. **The two XF master `jcr:content` subtrees** (header + footer). CONFIRMED BROKEN — legacy
     Navigation/LanguageNavigation/Search and Separator/Text still serve live instead of
     site-header/site-footer. NEEDS an explicit entry, `mode="replace"`, scoped to exactly
     `/content/experience-fragments/realmac/us/en/site/header/master/jcr:content` and
     `.../footer/master/jcr:content` — this is the one case that genuinely requires delete-old-then-
     install-new semantics (removing the legacy children), which is why `replace` (not `merge` or
     `update`) is the correct mode here specifically.

  4. **The new sample page** (`/content/realmac/us/en/innovation`). Auditron's iteration-2 TC-001
     evidence: the page DOES render (image `src`/`srcset` correct), just missing style classes
     because of area-1's policy 404s — meaning the page NODE ITSELF deployed fine as a brand-new
     child under the existing `/content/realmac` merge root. Confirm this with your own check (e.g.
     verify the page's `jcr:primaryType`/`cq:template` resolved live in Auditron's report) — if
     confirmed, NO new filter entry is needed for this path; CQ-05's fix (this dispatch) plus area
     1's fix are what will make it render fully, not a filter change.

  5. **The 13 DAM assets** (`/content/dam/realmac/tata-innovation`). Already fixed in Stage 04 with
     your own filter entry; Auditron confirmed "13/13 DAM assets deploy and render correctly" in
     iteration 2. CONFIRM this entry is still present and unchanged — do not re-add or duplicate it.

  **Bottom line:** add ONLY the explicit filter entries your own investigation shows are genuinely
  needed (expected: 1 for the shared policies subtree, 2 for the two XF masters — but verify, do not
  assume this is exactly right without checking areas 2 and 4 yourself). Use `mode="replace"` only
  where removal of existing nodes or an update to an existing node's properties is required — not
  as a default for every new entry.

  ## Do NOT touch
    - The 3 existing broad `mode="merge"` filter roots — leave them exactly as they are.
    - Your own existing DAM filter entry from Stage 04 — leave it exactly as it is.
    - Any `ui.apps`/`core`/`ui.frontend` file, or the `pom.xml` files (CQ-03, human-confirmed
      local-only, still out of scope for everyone).
    - Configsmith's actual policy XML content (the policy node definitions themselves) — you are
      only changing what filter.xml PACKAGES, not what the policy nodes say.

  ## After both fixes
    - Do NOT run `mvn` yourself — Auditron re-verifies in the next stage-05/07 dispatch (iteration 3,
      fresh 2-mvn budget already granted for that dispatch, not yours to spend).
    - Update `handoffs/composer.yaml` with a `remediation` block: the CQ-05 diff summary (old names
      -> new names, confirmation all properties/children/order preserved), and the CQ-04 completeness
      check results for all 5 areas above (which needed a new entry and why; which didn't and why
      not), plus the exact final filter.xml diff.

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\composer.yaml

gate-criteria: |
  - All 4 card teaser siblings have unique node names; hero teaser and all other nodes untouched;
    every property/child/order preserved exactly.
  - filter.xml: exactly the minimally-necessary set of new mode="replace" entries added, each
    justified by an explicit per-area investigation (not a blind copy of the 3 originally-proposed
    paths); the 3 broad existing merge roots and the Stage-04 DAM entry unchanged.
  - No ui.apps/core/ui.frontend/pom.xml files touched.
  - No Configsmith policy XML content changed (filter.xml packaging only).
  - No mvn invocation by composer.
  - handoffs/composer.yaml updated with a remediation block covering both fixes + the 5-area
    completeness check.
