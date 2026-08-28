# Policy Mapping — `landing-page`

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Least-privilege gate:** every parsys/container area below lists **explicit** components and/or
  component groups. No policy in this document uses `*`.
- Policies live under `/conf/realmac/settings/wcm/policies/jcr:content/realmac/components/...`
  (existing project convention — see `ui.content/.../settings/wcm/policies/.content.xml`).
  The mapping file lives at `ui.content/.../settings/wcm/templates/landing-page/policies/.content.xml`.

---

## 1. `templates/landing-page/policies/.content.xml` — full mapping tree

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        cq:policy="realmac/components/page/policy"
        jcr:primaryType="nt:unstructured"
        sling:resourceType="wcm/core/components/policies/mappings">
        <root
            cq:policy="realmac/components/container/policy_1574694950110"
            jcr:primaryType="nt:unstructured"
            sling:resourceType="wcm/core/components/policies/mapping">
            <experiencefragment-header
                cq:policy="realmac/components/experiencefragment/policy_header"
                jcr:primaryType="nt:unstructured"
                sling:resourceType="wcm/core/components/policies/mapping"/>
            <experiencefragment-footer
                cq:policy="realmac/components/experiencefragment/policy_footer"
                jcr:primaryType="nt:unstructured"
                sling:resourceType="wcm/core/components/policies/mapping"/>
            <container
                cq:policy="realmac/components/container/policy_649128221558427"
                jcr:primaryType="nt:unstructured"
                sling:resourceType="wcm/core/components/policies/mapping">
                <container
                    cq:policy="realmac/components/container/policy_landing_content"
                    jcr:primaryType="nt:unstructured"
                    sling:resourceType="wcm/core/components/policies/mapping">
                    <realmac jcr:primaryType="nt:unstructured">
                        <components jcr:primaryType="nt:unstructured">
                            <teaser
                                cq:policy="realmac/components/teaser/policy_landing_hero_teaser"
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="wcm/core/components/policies/mapping"/>
                            <text
                                cq:policy="realmac/components/text/policy_landing_intro_text"
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="wcm/core/components/policies/mapping"/>
                            <title
                                cq:policy="realmac/components/title/policy_641528232375303"
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="wcm/core/components/policies/mapping"/>
                            <image
                                cq:policy="realmac/components/image/policy_651483963895698"
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="wcm/core/components/policies/mapping"/>
                            <button
                                cq:policy="realmac/components/button/policy_landing_button"
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="wcm/core/components/policies/mapping"/>
                            <container
                                cq:policy="realmac/components/container/policy_landing_card_grid"
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="wcm/core/components/policies/mapping">
                                <realmac jcr:primaryType="nt:unstructured">
                                    <components jcr:primaryType="nt:unstructured">
                                        <teaser
                                            cq:policy="realmac/components/teaser/policy_landing_card_teaser"
                                            jcr:primaryType="nt:unstructured"
                                            sling:resourceType="wcm/core/components/policies/mapping"/>
                                    </components>
                                </realmac>
                            </container>
                        </components>
                    </realmac>
                </container>
            </container>
        </root>
    </jcr:content>
</jcr:root>
```

**Why the nested `<realmac><components>` segments are required:** the hero teaser and the card
teasers are dropped by authors at runtime under auto-generated node names (e.g. `teaser`,
`teaser_1234567`), not at fixed structural paths. AEM's policy resolution falls back from an exact
node-name match to a resourceType-keyed segment named after the **project namespace** (`realmac`,
matching the existing convention verified in `page-content/policies/.content.xml` and
`xf-web-variation/policies/.content.xml`) mirroring `/apps/realmac/components/<type>`. Each container
level in this tree gets its own `<realmac><components>` sub-tree scoped to only the resourceTypes it
allows as direct children — this is what makes the card-grid teaser resolve to a **different** policy
than the hero teaser even though both are `realmac/components/teaser` (see
`template-design.md § Heading-level budget`).

---

## 2. New policy definitions (`settings/wcm/policies/jcr:content/realmac/components/...`)

### 2.1 `container/policy_landing_content` — the editable parsys

```xml
<policy_landing_content
    jcr:primaryType="nt:unstructured"
    jcr:title="Landing Page — Content"
    jcr:description="Allowed components for the single editable content parsys on landing-page (hero, intro, card-grid)."
    sling:resourceType="wcm/core/components/policy/policy"
    components="[realmac/components/teaser,realmac/components/text,realmac/components/title,realmac/components/image,realmac/components/button,realmac/components/container]">
    <jcr:content jcr:primaryType="nt:unstructured"/>
</policy_landing_content>
```

**Allowed components (explicit list — no `*`, no group wildcard):** `realmac/components/teaser`,
`realmac/components/text`, `realmac/components/title`, `realmac/components/image`,
`realmac/components/button`, `realmac/components/container`.

### 2.2 `teaser/policy_landing_hero_teaser`

```xml
<policy_landing_hero_teaser
    jcr:primaryType="nt:unstructured"
    jcr:title="Landing Page — Hero Teaser"
    jcr:description="Full-bleed hero with overlaid H1 title. No CTA."
    sling:resourceType="wcm/core/components/policy/policy"
    allowedTypes="[h1]"
    titleType="h1"
    imageDelegate="realmac/components/image"
    actionsDisabled="{Boolean}true"
    cq:allowSingleSelection="{Boolean}true">
    <jcr:content jcr:primaryType="nt:unstructured"/>
    <cq:styleGroups jcr:primaryType="nt:unstructured">
        <item0 jcr:primaryType="nt:unstructured" cq:styleGroupLabel="Hero Variant">
            <cq:styles jcr:primaryType="nt:unstructured">
                <item0
                    jcr:primaryType="nt:unstructured"
                    cq:styleClasses="cmp-teaser--hero"
                    cq:styleId="20260828101"
                    cq:styleLabel="Hero"/>
            </cq:styles>
        </item0>
    </cq:styleGroups>
</policy_landing_hero_teaser>
```

> Per D6: does **not** add `titleHidden`/`descriptionHidden`/`pretitleHidden` (off by default already —
> adding them explicitly risks suppressing rendering, per the recorded Motorcycle Landing Page failure).
> `actionsDisabled=true` is a genuine variant-specific need (US-001: "No CTA button in the hero").

### 2.3 `text/policy_landing_intro_text`

```xml
<policy_landing_intro_text
    jcr:primaryType="nt:unstructured"
    jcr:title="Landing Page — Intro Text"
    jcr:description="Lead + body copy for the intro/overview block. Clones the project default RTE config and adds the intro-lead style variant."
    sling:resourceType="wcm/core/components/policy/policy"
    cq:allowSingleSelection="{Boolean}true">
    <jcr:content jcr:primaryType="nt:unstructured"/>
    <rtePlugins jcr:primaryType="nt:unstructured">
        <!-- Cloned verbatim from realmac/components/text/policy_641562756958017 ("Content Text") -->
        <paraformat jcr:primaryType="nt:unstructured" features="*">
            <formats jcr:primaryType="nt:unstructured" override="true">
                <item0 jcr:primaryType="nt:unstructured" description="Paragraph" tag="p"/>
                <item1 jcr:primaryType="nt:unstructured" description="Quote" tag="blockquote"/>
            </formats>
        </paraformat>
        <format jcr:primaryType="nt:unstructured" features="bold,italic"/>
        <links jcr:primaryType="nt:unstructured" features="modifylink,unlink"/>
        <lists jcr:primaryType="nt:unstructured" features="*"/>
    </rtePlugins>
    <cq:styleGroups jcr:primaryType="nt:unstructured">
        <item0 jcr:primaryType="nt:unstructured" cq:styleGroupLabel="Text Variant">
            <cq:styles jcr:primaryType="nt:unstructured">
                <item0
                    jcr:primaryType="nt:unstructured"
                    cq:styleClasses="cmp-text--intro-lead"
                    cq:styleId="20260828102"
                    cq:styleLabel="Intro Lead"/>
            </cq:styles>
        </item0>
    </cq:styleGroups>
</policy_landing_intro_text>
```

> New policy (not an edit to the shared `policy_641562756958017`) — isolates the intro-lead style
> option to `landing-page` only, so `page-content` authors do not see an irrelevant style choice.

### 2.4 `container/policy_landing_card_grid`

```xml
<policy_landing_card_grid
    jcr:primaryType="nt:unstructured"
    jcr:title="Landing Page — Card Grid"
    jcr:description="Container restricted to innovation-centre card teasers only."
    sling:resourceType="wcm/core/components/policy/policy"
    components="[realmac/components/teaser]"
    cq:allowSingleSelection="{Boolean}true">
    <jcr:content jcr:primaryType="nt:unstructured"/>
    <cq:styleGroups jcr:primaryType="nt:unstructured">
        <item0 jcr:primaryType="nt:unstructured" cq:styleGroupLabel="Layout">
            <cq:styles jcr:primaryType="nt:unstructured">
                <item0
                    jcr:primaryType="nt:unstructured"
                    cq:styleClasses="cmp-container--card-grid"
                    cq:styleId="20260828103"
                    cq:styleLabel="Card Grid"/>
            </cq:styles>
        </item0>
    </cq:styleGroups>
</policy_landing_card_grid>
```

**Allowed components:** `realmac/components/teaser` only — this is the card-grid's own
least-privilege allowlist (US-003: "4 cards render… No new component").

### 2.5 `teaser/policy_landing_card_teaser`

```xml
<policy_landing_card_teaser
    jcr:primaryType="nt:unstructured"
    jcr:title="Landing Page — Card Teaser"
    jcr:description="Innovation-centre showcase card: image + title + short descriptor + arrow link."
    sling:resourceType="wcm/core/components/policy/policy"
    allowedTypes="[h3]"
    titleType="h3"
    imageDelegate="realmac/components/image"
    cq:allowSingleSelection="{Boolean}true">
    <jcr:content jcr:primaryType="nt:unstructured"/>
    <cq:styleGroups jcr:primaryType="nt:unstructured">
        <item0 jcr:primaryType="nt:unstructured" cq:styleGroupLabel="Card Variant">
            <cq:styles jcr:primaryType="nt:unstructured">
                <item0
                    jcr:primaryType="nt:unstructured"
                    cq:styleClasses="cmp-teaser--innovation-card"
                    cq:styleId="20260828104"
                    cq:styleLabel="Innovation Card"/>
            </cq:styles>
        </item0>
    </cq:styleGroups>
</policy_landing_card_teaser>
```

**Note:** `actionsDisabled` is **not** set (default = actions enabled) — cards DO carry the
arrow-style CTA link (US-003), unlike the hero.

### 2.6 `button/policy_landing_button`

```xml
<policy_landing_button
    jcr:primaryType="nt:unstructured"
    jcr:title="Landing Page — Button"
    jcr:description="Default Core Button options; no landing-page-specific constraint required this run."
    sling:resourceType="wcm/core/components/policy/policy">
    <jcr:content jcr:primaryType="nt:unstructured"/>
</policy_landing_button>
```

Minimal policy created solely to satisfy G1 ("every editable container/component has a resolving
content policy") for the optional Button entry in the parsys allowlist — Button is not exercised by
any US-### acceptance criterion this run but is included in the allowlist per
`technical-specifications.md §4`.

---

## 3. Reused (unmodified) policies referenced by this template

| Policy path | Used for |
|---|---|
| `realmac/components/page/policy` | Page-level clientlibs |
| `realmac/components/container/policy_1574694950110` | `root` — Page Root (`components=[group:Realmac - Content,/apps/realmac/components/form/container,group:Realmac - Structure]`) |
| `realmac/components/experiencefragment/policy_header` | Header EF — `cq:styleDefaultElement=header` |
| `realmac/components/experiencefragment/policy_footer` | Footer EF — `cq:styleDefaultElement=footer` |
| `realmac/components/container/policy_649128221558427` | Main-landmark container — `cq:styleDefaultElement=main` |
| `realmac/components/title/policy_641528232375303` | Optional secondary heading — `type=h2`, `allowedTypes=[h2,h3,h4,h5,h6]` |
| `realmac/components/image/policy_651483963895698` | Standalone image (if authored) — existing "Content Image" config (lazy load, crop presets, etc.) |

---

## 4. Existing policy amendment required (Configsmith-owned, flagged by Designforge)

`realmac/components/container/policy_1575040440977` ("XF Root", used by the shared
`xf-web-variation` template for **every** Experience Fragment in the project, including the header/
footer master XFs that will host `site-header`/`site-footer`):

**Current:**
```
components="[group:Realmac - Content,/apps/realmac/components/form/container]"
```

**Required change:**
```
components="[group:Realmac - Content,/apps/realmac/components/form/container,group:Realmac - Structure]"
```

**Rationale (least privilege honored):** `Realmac - Structure` is the chrome-only component group
(already used by `navigation`, `languagenavigation`, `search`, and now `site-header`/`site-footer`).
Experience Fragments ARE the chrome-authoring surface in this project's S1 pattern — allowing the
Structure group broadly inside XFs (but not inside ordinary content pages, where `policy_landing_content`
and `policy_landing_card_grid` above deliberately exclude it) is consistent with the project's existing
convention and does not violate least privilege for content parsys areas.

Without this change, `site-header`/`site-footer` cannot be dropped into the header/footer master XFs
at all — this blocks US-004/US-005. Composer's work-breakdown item depends on this amendment landing
alongside (or before) EF content authoring.

---

## 5. Least-privilege gate — self-check

| Parsys / container area | Explicit allowlist | `*` used? |
|---|---|---|
| `root` (Page Root) | `group:Realmac - Content`, `/apps/realmac/components/form/container`, `group:Realmac - Structure` | No |
| Editable parsys (`policy_landing_content`) | `realmac/components/{teaser,text,title,image,button,container}` | No |
| Card-grid container (`policy_landing_card_grid`) | `realmac/components/teaser` | No |
| XF Root (amended) | `group:Realmac - Content`, `/apps/realmac/components/form/container`, `group:Realmac - Structure` | No |

No policy in this document or its amendments uses `*` (all components).
