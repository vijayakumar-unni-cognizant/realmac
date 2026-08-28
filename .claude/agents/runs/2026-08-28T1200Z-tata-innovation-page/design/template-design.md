# Template Design — `landing-page`

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Template type (reused):** `/conf/realmac/settings/wcm/template-types/page` (existing, unchanged).
- **Decision basis:** `technical-specifications.md §4` / `handoffs/strategist.yaml § template_decision`
  — new template because the existing `page-content` template's structural `<title>` would render an
  unwanted second `<h1>` above the hero (S10 mismatch on a shared, blast-radius template).

---

## Structure (`structure/jcr:content`)

Mirrors `page-content`'s S1-compliant chrome pattern verbatim, **minus the structural `<title>`
node** (D22 decision — see below), plus the "Page Main" `<main>` landmark wrapper retained from
`page-content` for the a11y NFR (`requirements.yaml § nfr.seo`: "Semantic landmarks
(header/main/footer)").

```xml
<jcr:content
    cq:deviceGroups="[mobile/groups/responsive]"
    cq:template="/conf/realmac/settings/wcm/templates/landing-page"
    jcr:primaryType="cq:PageContent"
    sling:resourceType="realmac/components/page">
    <root
        jcr:primaryType="nt:unstructured"
        sling:resourceType="realmac/components/container"
        layout="responsiveGrid">
        <experiencefragment-header
            jcr:primaryType="nt:unstructured"
            sling:resourceType="realmac/components/experiencefragment"
            fragmentVariationPath="/content/experience-fragments/realmac/us/en/site/header/master"/>
        <container
            jcr:primaryType="nt:unstructured"
            sling:resourceType="realmac/components/container"
            layout="responsiveGrid">
            <container
                jcr:primaryType="nt:unstructured"
                sling:resourceType="realmac/components/container"
                editable="{Boolean}true"
                layout="responsiveGrid"/>
        </container>
        <experiencefragment-footer
            jcr:primaryType="nt:unstructured"
            sling:resourceType="realmac/components/experiencefragment"
            fragmentVariationPath="/content/experience-fragments/realmac/us/en/site/footer/master"/>
    </root>
    <cq:responsive jcr:primaryType="nt:unstructured">
        <breakpoints jcr:primaryType="nt:unstructured">
            <phone jcr:primaryType="nt:unstructured" title="Smaller Screen" width="{Long}768"/>
            <tablet jcr:primaryType="nt:unstructured" title="Tablet" width="{Long}1200"/>
        </breakpoints>
    </cq:responsive>
</jcr:content>
```

**Forbidden attributes checklist (D8) — verified absent above:**
- No `editable="{Boolean}true"` on `<root>`.
- No `editable="{Boolean}false"` on the EF references (structural nodes are locked by default).
- No `decoration="{Boolean}false"` on the EF references.
- Only the innermost `<container>` carries `editable="{Boolean}true"`.

**Structural page heading decision (D22): `absent`.** The reference layout leads with the hero
banner, whose own overlaid "Innovation" title is the page's H1 (see `component-specifications.md § B.1`
and `§ Heading-level budget` below). A structural `<title>` node would render a second, unwanted `<h1>`
above the hero (this is exactly the S10 mismatch that forced a new template instead of reusing
`page-content`). The `<title>` node is therefore **omitted** from `structure/`. An **optional**
secondary heading remains available to authors as a plain `realmac/components/title` instance dropped
inside the editable parsys (h2, not h1) — see `component-specifications.md § B.2`.

**Authoring depth (D11):** authored components (hero teaser, intro text, card-grid container, and the
4 card teasers nested inside it) are placed at
`jcr:content/root/container/container/*` — i.e. **two levels deep** inside `root`, matching the
innermost `editable="{Boolean}true"` container above. Composer must not author directly under
`root` or under the middle (`main`-landmark) container.

---

## Page-level policy (D8 point 1)

Reuse the existing shared page policy — no new policy needed:

```
cq:policy (on jcr:content) = realmac/components/page/policy
  → clientlibs="[realmac.dependencies,realmac.site]"
  → clientlibsJsHead="realmac.dependencies"
```

Resolves to `/conf/realmac/settings/wcm/policies/jcr:content/realmac/components/page/policy`
(existing node — unchanged).

---

## Policy mapping (`policies/jcr:content`) — structure

Full detail (including the resourceType-fallback nesting for runtime-authored children) is in
`policy-mapping.md`. Summary of what maps where:

| Structural path | `cq:policy` | New or reused |
|---|---|---|
| `root` | `realmac/components/container/policy_1574694950110` ("Page Root") | Reused |
| `root/experiencefragment-header` | `realmac/components/experiencefragment/policy_header` | Reused |
| `root/experiencefragment-footer` | `realmac/components/experiencefragment/policy_footer` | Reused |
| `root/container` (main landmark) | `realmac/components/container/policy_649128221558427` ("Page Main") | Reused |
| `root/container/container` (editable parsys) | `realmac/components/container/policy_landing_content` | **New** |
| ↳ any `teaser` dropped directly in the parsys | `realmac/components/teaser/policy_landing_hero_teaser` | **New** |
| ↳ any `text` dropped directly in the parsys | `realmac/components/text/policy_landing_intro_text` | **New** |
| ↳ any `title` dropped directly in the parsys | `realmac/components/title/policy_641528232375303` ("Content Title", h2) | Reused |
| ↳ any `image` dropped directly in the parsys | `realmac/components/image/policy_651483963895698` ("Content Image") | Reused |
| ↳ any `button` dropped directly in the parsys | `realmac/components/button/policy_landing_button` | **New** (minimal — no special properties) |
| ↳ any `container` dropped directly in the parsys (the card-grid) | `realmac/components/container/policy_landing_card_grid` | **New** |
| ↳↳ any `teaser` dropped inside the card-grid container | `realmac/components/teaser/policy_landing_card_teaser` | **New** |

---

## Heading-level budget (D6 / D22 mandatory table)

| Instance | `sling:resourceType` | Policy | `titleType` / `type` |
|---|---|---|---|
| Hero teaser (direct child of the editable parsys) | `realmac/components/teaser` | `policy_landing_hero_teaser` | `titleType=h1` — **owns the page `<h1>`** |
| Card teaser ×4 (children of the card-grid container, a genuinely different mapping path — see `policy-mapping.md`) | `realmac/components/teaser` | `policy_landing_card_teaser` | `titleType=h3` |
| Optional secondary heading | `realmac/components/title` | `policy_641528232375303` (reused) | `type=h2`, `allowedTypes=[h2,h3,h4,h5,h6]` — never h1 |

Exactly one reachable teaser mapping path resolves to `titleType=h1` (the hero, at the outer parsys
level); the card teasers resolve via a structurally distinct mapping path (nested one level deeper,
inside the card-grid container) to a different policy with `titleType=h3`. This is the D6-endorsed
resolution ("normally the hero teaser… every other teaser → content-teaser policy") — the two teaser
policies are **not** competing for the same reachable mapping path, so this is not the G3
split-policy anti-pattern.

---

## Style System hooks

| Component type | Reachable path | Style variant(s) available | `cq:allowSingleSelection` |
|---|---|---|---|
| `teaser` (hero) | outer parsys | `cmp-teaser--hero` only | `{Boolean}true` |
| `teaser` (card) | inside card-grid container | `cmp-teaser--innovation-card` only | `{Boolean}true` |
| `text` | outer parsys | `cmp-text--intro-lead` only | `{Boolean}true` |
| `container` | outer parsys | `cmp-container--card-grid` only | `{Boolean}true` |

Each policy above has exactly one style variant in scope at its own reachable path — consistent with
D9's "one component type → one consolidated policy per reachable path" rule (see `policy-mapping.md`
for the full `cq:styleGroups` node definitions with numeric `cq:styleId` values).

---

## `cq:responsive` breakpoints

Reused verbatim from `page-content` (per `technical-specifications.md §4` — "clones… its
`cq:responsive` breakpoints"): `phone` width `768`, `tablet` width `1200`. These match
`design-token-audit.md § Breakpoints` and the project's existing responsive convention.

---

## `cq:allowedTemplates` registration (S4)

| Content branch | Current `cq:allowedTemplates` (verified on disk) | Action required |
|---|---|---|
| `/content/realmac` (`jcr:content`) | `[/conf/realmac/settings/wcm/templates/(?!xf-).*]` — a permissive **negative-lookahead** regex that already matches any non-`xf-` template, including `landing-page` | **No property change required** — `landing-page` is already covered. Composer must **verify** (not merely assume) by confirming `landing-page` appears in the "Create Page" wizard at `/content/realmac/us/en`. |
| `/content/realmac/us` | Not set (inherits from `/content/realmac`) | No change — inherits the permissive regex above. |
| `/content/realmac/us/en` | Not set (inherits) | No change — inherits. If verification fails for any reason, Composer must add an explicit override `cq:allowedTemplates="[/conf/realmac/settings/wcm/templates/(?!xf-).*]"` on this node's `jcr:content` as a fallback. |

**Template's own `allowedPaths` (on the `cq:Template` node itself):**

```
allowedPaths="[/content/realmac(/.*)?]"
```

This scopes `landing-page` to the `realmac` site tree only (matches project convention; the template
is not intended for other sites/roots).

---

## Note: pre-existing XF Root policy amendment required (not landing-page-specific)

`site-header`/`site-footer` are authored **inside** the header/footer master Experience Fragments
(`/content/experience-fragments/realmac/us/en/site/{header,footer}/master`), which use the existing,
shared `xf-web-variation` template — **not** `landing-page`. That template's root policy
(`realmac/components/container/policy_1575040440977`, "XF Root") currently allows
`components="[group:Realmac - Content,/apps/realmac/components/form/container]"` — it does **not**
include `group:Realmac - Structure`, so `site-header`/`site-footer` (group `Realmac - Structure`)
cannot be dropped into the master XFs as currently configured. See `policy-mapping.md § Existing
policy amendment` for the required change (Configsmith-owned; flagged here because it blocks US-004/
US-005 otherwise).

**Also observed (out of this run's fix scope, flagged for awareness):** the XF Root policy's
child-resourceType-fallback segment is named `<mysite>` (`.../policy_1575040440977/mysite/components/...`)
rather than `<realmac>`. This is pre-existing content unrelated to this run's added components — it
does not block `site-header`/`site-footer` (their only nested resource, the embedded Navigation, is a
synthetic HTL resource that does not go through policy resolution — see
`component-specifications.md § A.1`). Recorded for Auditron/Configsmith awareness only; not remediated
by this run.
