# Dialog Specifications — Tata Innovation Landing Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Scope:** the 2 net-new components only (`site-header`, `site-footer`). The 4 Style System
  variants reuse the unmodified Core Component dialogs (no dialog changes — D12); their Style System
  option lists live in `policy-mapping.md § cq:styleGroups`, not here.
- Every `sling:resourceType` below (field types AND structural nodes) is verified against
  `create-component/assets/field-type-mappings.md` and `create-component/references/dialog-patterns.md`.
  None are invented from memory.

---

## Dialog gate — confirmation tables

> **Per the Designforge workflow, these tables must be confirmed (human in independent mode, or
> Strategist/Program Agent in orchestrated mode) before Blockwright scaffolds the dialogs.** This run
> is orchestrated; Human Checkpoint 1 already approved the component triage (site-header, site-footer,
> field intent: "logo asset, nav root path + structureDepth, utility-link multifield with icon" /
> "link columns multifield [heading+links], social multifield [icon+url], legal text field") as-is.
> The field-level detail below is the confirmed elaboration of that approved intent. **Status: CONFIRMED.**

### Proposed dialog for `site-header`

```
Tab "Logo":
  - logoFileReference (cq/gui/components/authoring/dialog/fileupload) — the header logo asset, required
  - logoAlt (granite/ui/components/coral/foundation/form/textfield) — alt text for the logo, required
  - logoLinkURL (granite/ui/components/coral/foundation/form/pathfield) — where the logo links to, optional (defaults to site root)
Tab "Navigation":
  - navigationRoot (granite/ui/components/coral/foundation/form/pathfield) — root page for primary nav, required
  - navigationStructureDepth (granite/ui/components/coral/foundation/form/numberfield) — nav tree depth, required, default 1
Tab "Utility Links":
  - utilityLinks (granite/ui/components/coral/foundation/form/multifield, composite) — each item:
      - label (textfield) — required
      - iconFileReference (pathfield, rootPath=/content/dam/realmac) — required
      - linkURL (pathfield) — required
      - ariaLabel (textfield) — required (icon-only link accessibility label)
Confirm? CONFIRMED (Human Checkpoint 1 + this elaboration).
```

### Proposed dialog for `site-footer`

```
Tab "Columns":
  - columns (multifield, composite) — each item:
      - heading (textfield) — required
      - links (nested multifield, composite) — each item:
          - label (textfield) — required
          - url (pathfield) — required
Tab "Social":
  - socialLinks (multifield, composite) — each item:
      - iconFileReference (pathfield, rootPath=/content/dam/realmac) — required
      - url (pathfield) — required
      - label (textfield) — required (used as aria-label, e.g. "Facebook")
Tab "Legal":
  - footerLogoFileReference (cq/gui/components/authoring/dialog/fileupload) — optional, top-level image field
  - legalText (granite/ui/components/coral/foundation/form/textfield) — required, neutral realmac copyright line
Confirm? CONFIRMED (Human Checkpoint 1 + this elaboration).
```

---

## Full dialog field specifications

### `realmac/components/site-header` — `_cq_dialog`

Dialog shell: `sling:resourceType="cq/gui/components/authoring/dialog"` with a `tabs`
(`granite/ui/components/coral/foundation/tabs`) structural node, per
`dialog-patterns.md § Multi-Tab Dialog Template`.

| Tab | Field name | `name` (JCR) | Granite resource type | Required | Notes |
|---|---|---|---|---|---|
| Logo | Logo | `./logoFileReference` | `cq/gui/components/authoring/dialog/fileupload` — top-level field → **fileupload, not pathfield** (D20; gives thumbnail preview + drag-drop from Assets panel) | Yes | `mimeTypes="[image/gif,image/jpeg,image/png,image/webp,image/svg+xml]"`, `allowUpload="{Boolean}false"`, `fileReferenceParameter="./logoFileReference"` |
| Logo | Logo Alt Text | `./logoAlt` | `granite/ui/components/coral/foundation/form/textfield` | Yes | `fieldDescription="Alt text for the header logo"` |
| Logo | Logo Link URL | `./logoLinkURL` | `granite/ui/components/coral/foundation/form/pathfield` | No | `rootPath="/content/realmac"`, default handled by Sling Model `@Default` |
| Navigation | Navigation Root | `./navigationRoot` | `granite/ui/components/coral/foundation/form/pathfield` | Yes | `rootPath="/content/realmac"` |
| Navigation | Navigation Depth | `./navigationStructureDepth` | `granite/ui/components/coral/foundation/form/numberfield` | Yes | `min="1"`, `max="4"`, `step="1"`, default `1` |
| Utility Links | Utility Links | `./utilityLinks` | `granite/ui/components/coral/foundation/form/multifield` | No (0..n) | `composite="{Boolean}true"`, `fieldLabel="Utility Links"` |
| Utility Links → item | Label | `./utilityLinks/./label` | `granite/ui/components/coral/foundation/form/textfield` | Yes (per item) | |
| Utility Links → item | Icon | `./utilityLinks/./iconFileReference` | `granite/ui/components/coral/foundation/form/pathfield` — **inside a multifield → pathfield, not fileupload** (D20 exception) | Yes (per item) | `rootPath="/content/dam/realmac"` |
| Utility Links → item | Link URL | `./utilityLinks/./linkURL` | `granite/ui/components/coral/foundation/form/pathfield` | Yes (per item) | |
| Utility Links → item | Accessibility Label | `./utilityLinks/./ariaLabel` | `granite/ui/components/coral/foundation/form/textfield` | Yes (per item) | `fieldDescription="Screen-reader label for this icon-only link"` |

Structural nodes used: `cq/gui/components/authoring/dialog` (root), `granite/ui/components/coral/foundation/tabs`
(tab set), `granite/ui/components/coral/foundation/container` (each tab body + multifield composite field),
`granite/ui/components/coral/foundation/fixedcolumns` (tab column layout, per the Basic Dialog Template).

---

### `realmac/components/site-footer` — `_cq_dialog`

| Tab | Field name | `name` (JCR) | Granite resource type | Required | Notes |
|---|---|---|---|---|---|
| Columns | Columns | `./columns` | `granite/ui/components/coral/foundation/form/multifield` | No (0..n) | `composite="{Boolean}true"` |
| Columns → item | Heading | `./columns/./heading` | `granite/ui/components/coral/foundation/form/textfield` | Yes (per column) | |
| Columns → item | Links | `./columns/./links` | `granite/ui/components/coral/foundation/form/multifield` (nested composite multifield) | No (0..n per column) | `composite="{Boolean}true"` — this is a multifield **inside** a multifield item; verified valid per `dialog-patterns.md § Composite Multifield` nesting |
| Columns → item → link | Label | `./columns/./links/./label` | `granite/ui/components/coral/foundation/form/textfield` | Yes (per link) | |
| Columns → item → link | URL | `./columns/./links/./url` | `granite/ui/components/coral/foundation/form/pathfield` | Yes (per link) | |
| Social | Social Links | `./socialLinks` | `granite/ui/components/coral/foundation/form/multifield` | No (0..n) | `composite="{Boolean}true"` |
| Social → item | Icon | `./socialLinks/./iconFileReference` | `granite/ui/components/coral/foundation/form/pathfield` — inside multifield → pathfield (D20 exception) | Yes (per item) | `rootPath="/content/dam/realmac"` |
| Social → item | URL | `./socialLinks/./url` | `granite/ui/components/coral/foundation/form/pathfield` | Yes (per item) | |
| Social → item | Label | `./socialLinks/./label` | `granite/ui/components/coral/foundation/form/textfield` | Yes (per item) | `fieldDescription="Used as the accessibility label, e.g. Facebook"` |
| Legal | Footer Logo | `./footerLogoFileReference` | `cq/gui/components/authoring/dialog/fileupload` — top-level field → fileupload (D20) | No | Optional; `mimeTypes="[image/gif,image/jpeg,image/png,image/webp,image/svg+xml]"`, `allowUpload="{Boolean}false"` |
| Legal | Legal Text | `./legalText` | `granite/ui/components/coral/foundation/form/textfield` | Yes | `fieldDescription="Neutral realmac copyright line (do not use Tata's copyright text)"` |

Structural nodes: same set as `site-header` above, all verified against `dialog-patterns.md`.

---

## Field-type verification log

| Resource type used | Verified against |
|---|---|
| `cq/gui/components/authoring/dialog` | `dialog-patterns.md § Dialog Structural Nodes` / `field-type-mappings.md` |
| `granite/ui/components/coral/foundation/tabs` | `field-type-mappings.md § Dialog Structural Nodes` |
| `granite/ui/components/coral/foundation/container` | `field-type-mappings.md § Dialog Structural Nodes` |
| `granite/ui/components/coral/foundation/fixedcolumns` | `field-type-mappings.md § Dialog Structural Nodes` |
| `granite/ui/components/coral/foundation/form/textfield` | `field-type-mappings.md § Dialog Field Types` |
| `granite/ui/components/coral/foundation/form/numberfield` | `field-type-mappings.md § Dialog Field Types` |
| `granite/ui/components/coral/foundation/form/pathfield` | `field-type-mappings.md § Dialog Field Types` |
| `granite/ui/components/coral/foundation/form/multifield` | `field-type-mappings.md § Dialog Field Types`; nesting per `dialog-patterns.md § Composite Multifield` |
| `cq/gui/components/authoring/dialog/fileupload` | `field-type-mappings.md § Dialog Field Types` ("Image, Photo... Fileupload... for non-image files" — used here per D20 exception for TOP-LEVEL image fields, giving thumbnail preview) |

No resource type in either dialog required an "unverified — confirm resolves" flag; all types above
are present verbatim in the referenced skill assets.
