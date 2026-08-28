# Authoring Test Cases — Tata Innovation Landing Page

```ids: prefix=AUTH count=20 AUTH-001..AUTH-020 (no gaps)
```

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Why this artifact is required:** this run creates new authoring surfaces — the `site-header` and
  `site-footer` dialogs, the `landing-page` editable template + its policies, and seeded DAM/EF/page
  content. Per Designforge's contract this artifact is mandatory (not an N/A stub) for this run.
- **GraphQL/CF-Model list-schema note:** the guardrail "verify a list field introspects as
  `kind: LIST` in the delivered GraphQL schema" **does not apply** to this run — there are no Content
  Fragment Models in scope (`requirements.yaml § out_of_scope`: "Headless/GraphQL/Content Fragment
  Model architecture"). The applicable analog used below is: verify each multifield is a **genuine
  composite multifield** (values stored as child-resource nodes, addable/removable/reorderable in the
  dialog), not a `multiple`-flagged single-value widget.

---

## Model → editor parity

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-001 | Every field in `SiteHeaderModel` (`logoFileReference`, `logoAlt`, `logoLinkURL`, `navigationRoot`, `navigationStructureDepth`, `utilityLinks[].{label,iconFileReference,linkURL,ariaLabel}`) appears in the `site-header` dialog with the widget specified in `dialog-specifications.md`; nothing silently dropped | Open the `site-header` dialog in the local SDK author instance; compare visible fields against `dialog-specifications.md` field table | Composer (author verification) / Auditron (structural `_cq_dialog` diff against the spec) |
| AUTH-002 | Every field in `SiteFooterModel` (`footerLogoFileReference`, `columns[].{heading,links[].{label,url}}`, `socialLinks[].{iconFileReference,url,label}`, `legalText`) appears in the `site-footer` dialog with the specified widget | Same method as AUTH-001, for `site-footer` | Composer / Auditron |

## Multi-value authorability

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-003 | `utilityLinks` is a genuine composite multifield: add/remove/reorder works in the dialog UI, and each entry persists as a child-resource node (`utilityLinks/item0`, `item1`, …), not a comma-joined string | Author 2 utility links, save, inspect JCR — two child nodes under `utilityLinks`, each with its own `label`/`iconFileReference`/`linkURL`/`ariaLabel` properties | Composer |
| AUTH-004 | `columns` (footer, outer multifield) supports add/remove/reorder; each column persists as a child-resource node | Author 2 columns, save, inspect JCR — two child nodes under `columns` | Composer |
| AUTH-005 | `columns/*/links` (nested multifield, per column) supports add/remove/reorder independently per column | Author 2 links in column 1 and 1 link in column 2, save, inspect JCR — `columns/item0/links` has 2 children, `columns/item1/links` has 1 | Composer |
| AUTH-006 | `socialLinks` supports add/remove/reorder; persists as child-resource nodes | Author 3 social links, save, inspect JCR — three child nodes under `socialLinks` | Composer |

## Required-field enforcement

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-007 | `site-header`'s required fields (`logoFileReference`, `logoAlt`, `navigationRoot`, `navigationStructureDepth`) block dialog save when empty | Clear each required field one at a time in the dialog; attempt save | Composer |
| AUTH-008 | `site-footer`'s `legalText` is required; every authored column requires a non-empty `heading`; every authored link requires `label`+`url` | Clear `legalText`; attempt save. Add a column with an empty heading; attempt save | Composer |
| AUTH-009 | Every authored `utilityLinks` item requires `label`+`iconFileReference`+`linkURL`+`ariaLabel` together (an icon-only link with a missing `ariaLabel` is an authoring error, not a valid partial entry) | Author a utility link with `ariaLabel` blank; attempt save | Composer |

## Data-setup integrity

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-010 | All 13 supplied assets seeded under `/content/dam/realmac/tata-innovation/` have a **stored** binary + original rendition equal to the intended source file (not merely a `dam:Asset` node with no rendition) | For each of the 13 assets, read back the `original` rendition and diff its byte size/checksum against the source file in the supplied asset folder | Composer (seed) / Auditron (C11 check) |
| AUTH-011 | Values containing the serialization array-separator character (`,`) are correctly escaped where they appear inside a bracket-notation multi-value property, so one intended element does not silently split into several | Register `cq:allowedTemplates` (bracket multi-value `String[]`) at `/content/realmac/us/en` if an explicit override is ever added (see `template-design.md § cq:allowedTemplates registration`); if any authored regex/path segment were to contain a literal comma, verify it is written as `\,` inside the bracket list and that the property still resolves to the correct single element count on read-back. **This run's authored values (template paths, DAM paths, URLs) contain no literal commas**, so no escaping is exercised in practice — this case exists to make the hazard checkable if a future column heading, link label, or path ever does contain one (none of `site-header`/`site-footer`'s own list fields are bracket-notation `String[]` — they are composite multifields stored as child nodes, so this hazard is structurally absent from AUTH-003–006; it is only reachable via the `cq:allowedTemplates`-style bracket properties Composer/Configsmith author) | Composer / Configsmith |
| AUTH-012 | `site-header`'s `logoFileReference` and every `utilityLinks[].iconFileReference` resolve to a real, seeded DAM asset — no dangling path | Inspect authored header EF content; resolve each path against the DAM tree seeded per AUTH-010 | Composer |
| AUTH-013 | `site-footer`'s `socialLinks[].iconFileReference` and (if authored) `footerLogoFileReference` resolve to real, seeded DAM assets | Inspect authored footer EF content; resolve each path | Composer |
| AUTH-014 | Hero teaser's and every card teaser's `fileReference` resolve to their intended seeded DAM asset (hero → `about_innovation_banner_desktop_1920x1080.jpg`; cards → `TataChemicals_Desk.jpg`, `TataSteelEurope_Desk.jpg`, `TMETC_Desk.jpg`, `tcsinnovation_information_desktop_360x260.jpg`) | Inspect authored sample-page content; resolve each `fileReference` | Composer |

## Redeploy / update semantics

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-015 | A corrected `legalText` (or any other authored field) value reaches the instance on redeploy — the covering package's `filter.xml` import mode updates existing nodes, not only adds missing ones | Change `legalText` in the source content package, redeploy (`mvn ... -PautoInstallPackage` on `ui.content`), re-read the node | Configsmith / Auditron |

## Edit round-trip

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-016 | An author can change a `site-header`/`site-footer` field value through the AEM authoring UI, it persists to the JCR node, and it re-delivers through the component's HTL render (JCR persistence check — complements the DOM-level checks in `ui-test-scenarios.md` UI-017/UI-018) | In the local SDK author instance, open the dialog, change `logoAlt`, save; inspect the JCR node for the new value; then curl the rendered page and confirm the `<img alt="">` reflects it | Composer |

## Publish / activation state

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-017 | The sample page, its 13 DAM assets, and both master XFs are content the feature depends on and are activation-ready (not author-only draft state) — actual activation to a Publish tier is a real-environment concern | Inspect each node's replication status properties (`cq:lastReplicationAction`, etc.) after Composer seeds content; full activation confirmation happens at Sentinel's real-environment stage (deferred this run) | Composer (provision) / Sentinel (real-env confirmation, deferred) |

## Authoring guardrails

| ID | Asserted behaviour | How to verify | Owner |
|---|---|---|---|
| AUTH-018 | A required field left empty (e.g. `legalText`, `logoFileReference`) results in `hasContent()==false` and an author-mode placeholder — no runtime exception | Author `site-footer` with no `legalText`; render in author mode and in a non-author (publish-simulated) mode | Composer / Auditron |
| AUTH-019 | An optional field omitted (`logoLinkURL`, `footerLogoFileReference`) falls back to its documented default/absent-safe behavior | Author `site-header` with no `logoLinkURL` → logo link resolves to the `@Default` site root. Author `site-footer` with no `footerLogoFileReference` → no `<img>` emitted (via `data-sly-test`) | Composer / Auditron |
| AUTH-020 | An empty list (`utilityLinks=[]`, `columns=[]`, `socialLinks=[]`) renders its container gracefully with no broken layout — the empty-state contract stated in `component-specifications.md § Edge cases` | Author each component with its multifields present but empty; render and inspect the corresponding empty `<ul>`/`<div>` (cross-checked against `functional-test-cases.md` TC-014/TC-020/TC-025) | Composer / Auditron |
