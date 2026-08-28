# Technical Specifications — Tata Innovation Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Project:** realmac (AEMaaCS, Java 21) · repo `C:\AEM\Repos\realmac` · branch `feature/realmac-landing-page`
- **Companion artifacts:** `plan/requirements.yaml`, `plan/reference-deconstruction.md`
- **Best-practices skill:** run across this proposal — no deprecated API, scheduler, listener, replication, or HTL-lint anti-pattern is introduced (all-reuse of Core Component v2/v3 proxies + Sling Models with standard adaptables).

---

## 1. Chosen architectural pattern + rationale

**Pattern: Full server-rendered AEMaaCS Sites page (HTL + editable template + content policies).**

Rationale:
- The deliverable is a public, SEO-relevant, editor-authored marketing page whose primary content is text + images — the canonical server-rendered Sites case.
- No structured-content reuse across surfaces (app/SPA/commerce) is required, so **Headless CF + GraphQL is rejected** — it would add a CF-Model + persisted-query + GraphQL-endpoint layer with zero reuse payoff and a heavier authoring model for what is dialog-authored content.
- No Git-backed content source / UE roadmap requirement stated → **Universal Editor not selected** for this run; the existing WCM editable-template + policy model is used.
- Hybrid rejected for the same reason as Headless (no second consuming surface).

This matches the run's scope guardrail ("Full server-rendered Sites page … default assumption is direct dialog-authored content").

_No deviation from project architecture defaults S1–S10 is required; see §Deviations (none)._

---

## 2. Module / package impact

| Module | Mutable/Immutable | Impact |
|---|---|---|
| `core/` | immutable | New Sling Models `SiteHeaderModel`, `SiteFooterModel` under `com.realmac.aem.core.models` + wcm.io AEM Mocks unit tests. |
| `ui.apps/` | immutable | New chrome components `site-header`, `site-footer` (`.content.xml`, `_cq_dialog`, HTL, clientlib refs). Component group "Realmac - Structure" for header/footer chrome. NO new content components (hero/intro/cards all reuse existing proxies). |
| `ui.frontend/` | immutable (compiled → ui.apps) | New SCSS partials: `_site-header.scss`, `_site-footer.scss`, hero + card-grid + card + intro variant partials (or extensions of existing `_teaser.scss`/`_container.scss`), imported into `main.scss`. Playwright authoring lives in `ui.tests`, not here. |
| `ui.content/` | mutable | New editable template `landing-page` (structure + policies + initial), policy mappings, Style System variant policies, `cq:allowedTemplates` registration, header/footer EF content, sample page + intermediate `cq:Page` nodes, seeded DAM assets. |
| `ui.config/` | mutable | No new OSGi config required (no external integration, no service user). |
| `dispatcher/` | mutable | No rule change required — standard cacheable published Sites page under `/content/realmac`. |
| `it.tests/` | — | No new integration tests required this run (optional smoke could be added; not sized). |
| `ui.tests/` | — | New Playwright specs authored (not executed) for the innovation page. |

---

## 3. Component strategy (reuse-vs-new triage — S5/S6/S8)

Existing `apps/realmac/components/` proxies verified on disk (all extend Core Components):
`teaser`→teaser/v2, `container`→container/v1, `image`→image/v3, `text`→text, `title`→title, `list`→list/v3 (teaserDelegate=teaser), `navigation`→navigation/v2, `carousel`→carousel/v1, `breadcrumb`, `accordion`, `button`, `download`, `languagenavigation`, `experiencefragment`.

| Reference section | Classification | Target resourceType | Rationale |
|---|---|---|---|
| **Hero banner** (US-001) | **(A) Style System variant** | `realmac/components/teaser` (→ core teaser/v2) styled `cmp-teaser--hero` | S6: hero is a teaser-pattern block → Core Teaser default. Field-set fit: image (fileReference) + title. Visual-fit check (S6-ext): Core Teaser v2 emits `.cmp-teaser__image` + `.cmp-teaser__content > .cmp-teaser__title`; full-bleed image + absolutely-positioned title + dark scrim (`::before`) is achievable with CSS on that DOM — no HTL override, no DOM restructuring. Confirmed (A), NOT a new component. |
| **Intro/overview text** (US-002) | **(A) reuse + Style System variant** | `realmac/components/text` (→ core text) styled `cmp-text--intro-lead` | Body copy in a single column. Core Text RTE covers lead + body; lead-vs-body typography is CSS (Style System variant). Optional section heading via `realmac/components/title`. No new component. |
| **Card grid container** (US-003) | **(A) Style System variant** | `realmac/components/container` (→ core container/v1) styled `cmp-container--card-grid` | S2: project container proxy is the parsys/grid type. CSS Grid (2-col desktop/tablet → 1-col <768px) via Style System variant. Legitimate composition (S8 rule 3): 4 distinct card authoring surfaces. NOT foundation responsivegrid. |
| **Showcase card** ×4 (US-003) | **(A) Style System variant** | `realmac/components/teaser` styled `cmp-teaser--innovation-card` | S6: card is a teaser-pattern block → Core Teaser default. Field-set: image + title + link (title link / single action). Visual-fit: `.cmp-teaser` (image top, content below, arrow via `.cmp-teaser__action` restyled) matches card layout with CSS only. Confirmed (A). |
| **Header chrome** (US-004) | **(C) new component** | `realmac/components/site-header` (new, group "Realmac - Structure") | S8 header rule: logo + primary nav + utility icons (search/contact/toggle) is ONE atomic authoring surface. Faking it from Image + Navigation + Navigation is forbidden fragmentation. Build ONE custom component (dialog: logo asset, nav root + structureDepth, utility-link multifield with icon). Authored into the header EF (S1). |
| **Footer chrome** (US-005) | **(C) new component** | `realmac/components/site-footer` (new, group "Realmac - Structure") | S8 footer rule: link columns + social-icon row + legal bar is ONE atomic surface; 5×Text fragmentation (recorded Lunar failure) is forbidden. Build ONE custom component (dialog: columns multifield[heading+links], social multifield[icon+url], legal text). Authored into the footer EF (S1). |

**Net-new components: exactly 2 (`site-header`, `site-footer`) — both chrome.** All primary page content reuses existing Core Component proxies via the Style System. This is the intended reuse posture (S5/S6/S8).

### Component strategy → deviations
None. No teaser-pattern block was classified (C); the two (C) classifications are chrome sections with domain-specific data models no single Core Component exposes (S8 rule 2).

---

## 4. Template strategy

**Decision: NEW editable template `landing-page` (reusing existing template-type `/conf/realmac/settings/wcm/template-types/page`).**

Per S10 reuse-vs-new assessment of the existing `page-content` template:
1. **Default-structure fidelity:** `page-content`'s `structure/` renders a **structural Title** (self-populates page `jcr:title` as an `<h1>` at the top of the body). The innovation layout has NO plain page-title heading at body top — its title lives inside the hero image overlay. This is a default-structure conflict.
2. **Blast radius:** `page-content` is the shared base site template (referenced by the existing `/content/realmac` page + `initial`). Modifying its `structure/` to remove the Title would change every current consumer.
3. **Decision:** **new template / variant** (the default resolution for a mismatch on a shared template). `landing-page` clones `page-content`'s S1-compliant chrome pattern (EF header + EF footer references) and its `cq:responsive` breakpoints, but its `structure/` is: root **container proxy** (S2) → EF header → single editable **container proxy** parsys (hero + intro + card-grid authored here) → EF footer. **No structural Title.**

- **Template type:** reuse `page` template-type (exists).
- **Allowed components** (via content policy on the editable parsys): `realmac/components/{teaser, text, title, container, image, button}` + the two chrome components restricted to EF authoring.
- **Registration (S4):** extend `cq:allowedTemplates` at `/content/realmac`, `/content/realmac/us`, AND `/content/realmac/us/en` so the template is assignable at the locale level where pages live.

Carried forward as an acceptance criterion on the Designforge template item (designforge §D22) and the Blockwright/Configsmith items.

---

## 5. Integration map

**None.** No external system integration in this run (Bridgesmith not in roster; scope guardrail). No sync servlet, async job, replication event, OSGi event, or scheduled poll is introduced. This row is intentionally empty.

---

## 6. Content strategy (routed to Composer)

- **CF Models / persisted queries:** none (server-rendered, dialog-authored).
- **DAM seeding:** copy the 8 supplied binaries from `C:\Users\2489691\Downloads\tata-innovation-assets` into `/content/dam/realmac/tata-innovation/` with real binaries + original renditions (C11 — no `dam:Asset` without a binary):
  - `about_innovation_banner_desktop_1920x1080.jpg` (hero)
  - `TataChemicals_Desk.jpg`, `TataSteelEurope_Desk.jpg`, `TMETC_Desk.jpg`, `tcsinnovation_information_desktop_360x260.jpg` (4 cards)
  - `tata-logo.svg` (logo), `search.svg`, `video.svg`, `close.svg`, `ContactUs.svg`, `FB.svg`, `Instagram.svg`, `Linkedin.svg` (icons)
- **Sample page (S3):** author `/content/realmac/us/en/innovation` as `cq:Page`; every intermediate segment (`us`, `en`) already exists as `cq:Page` — verify and author the new `innovation` page node (do not let it default to `nt:folder`). Content depth must reach the innermost editable parsys (C8).
- **Chrome EFs (S1):** author header EF at `/content/experience-fragments/realmac/us/en/site/header/master` (logo + nav + utility icons via `site-header`) and footer EF at `.../footer/master` (columns + social + legal via `site-footer`). These EF paths already exist and are the ones the template structure references.
- **Copy:** reference is `visual-reference-only` (S9.a) — Composer authors neutral demo copy mirroring the reference structure (lead paragraph + strategy body + 4 named innovation-centre cards). Neutral realmac copyright in footer, not Tata's.
- **Standard field names (C10):** use Core Component v2 canonical fields (`jcr:title`, `description`, `fileReference`, `imageAltText`, `actions[*]`).

---

## 7. NFR strategy (owner per row)

| NFR | Target | Mitigation | Owner |
|---|---|---|---|
| LCP | <=2500ms | Core Image v3 responsive srcset on hero + explicit width/height; hero is above-the-fold priority image; card images lazy. | Sentinel (execution **deferred** — no env URL); Blockwright authors the responsive markup. |
| INP | <=200ms | Minimal JS (header menu toggle only); no heavy client hydration. | Sentinel (deferred); Blockwright. |
| CLS | <=0.1 | Explicit image dimensions on hero + cards; reserved grid tracks. | Sentinel (deferred); Blockwright. |
| TTFB | <=600ms | Standard dispatcher/CDN caching of published page; no per-request servlet. | Sentinel (deferred); Configsmith (cache/policy). |
| Accessibility | WCAG 2.1 AA | Semantic heading order (H1 hero/H2 section/H3 cards), alt text, icon aria-labels, scrim + footer contrast >=4.5:1, focus states. | Sentinel a11y scan (deferred); Blockwright authors semantics; Designforge specs it. |
| SEO | server-rendered, single H1, meta | HTL semantic landmarks; authored title/meta on sample page. | Sentinel (deferred); Composer authors meta. |
| Visual fidelity | matches reference-deconstruction | SCSS authored per `reference-deconstruction.md` regions; Tier-A visual diff. | Sentinel Tier-A visual diff (deferred); Blockwright SCSS; Designforge component specs. |
| Cross-browser | last-2 evergreen + mobile | Standard autoprefixed SCSS build; no bleeding-edge CSS. | Sentinel (deferred); Blockwright. |

**Sentinel is deferred this session** (no real environment URL). This is an accepted mitigation note: NFR targets are specified now and enforced by Sentinel at resume against the real env URL. Blockwright MUST still **author (not execute)** Playwright specs in `ui.tests` so Cloud Manager's Custom UI Testing runs Playwright (not Cypress).

---

## 8. Risks + mitigations

| Risk | Severity | Mitigation | Owner |
|---|---|---|---|
| Tata logo/brand supplied for a `realmac` demo — trademark/licensing exposure if shipped as production branding. | High (legal) | Demo-only use as explicitly authorized by the human; flag in handoff; replace with realmac branding before any non-demo use (Q-004). | Human / Lead |
| Hero 1920×1080 as LCP element risks slow LCP. | Medium | Core Image v3 responsive srcset + priority + explicit dimensions. | Blockwright (author) / Sentinel (verify, deferred) |
| Reference ships obfuscated CSS → deconstruction sizes are approximations. | Medium | `reference-deconstruction.md` gives ranges; Designforge pins exact tokens in `design-token-audit.md`; visual diff catches drift at resume. | Designforge / Sentinel |
| Sentinel deferred (no env URL) → NFR/visual/Playwright execution not run this session. | Medium | Targets specified now; scheduled as LAST stage against real env URL at resume; Playwright authored pre-deploy. | Program Agent / Lead |
| Reference copy could be silently invented (S9.a). | Low | Classified `visual-reference-only`; Composer authors neutral demo copy, not Tata verbatim content. | Composer |
| Chrome authored as fragmented Text blocks (S8 recorded failure). | Low | Single `site-header` / `site-footer` components mandated in §3. | Designforge / Blockwright |

---

## 9. Out of ADLC scope

Recorded here, NOT added to the work breakdown (per Strategist contract). Direct the human to the external / Lead-driven process:
- **Cloud Manager Dev / Stage / Prod pipeline triggers, Stage soak, Stage/Prod human approvals** — the Lead runs these via Cloud Manager after merging the PR. Not an ADLC agent step.
- **Merging the PR, syncing to Adobe Git, deploying to the real environment** — Lead, manual.
- **Post-deploy operations** (real-env rollback, incident triage, postmortems, recurring-incident escalation) — out of ADLC scope.
- **Sentinel NFR + Playwright EXECUTION** — deferred this session pending real environment URL(s) + auth mode (Q-003); Sentinel is scheduled as the LAST stage and runs at resume against the real env, NOT localhost/RDE.
- **Production branding replacement** for the Tata demo assets (Q-004) — human/Lead decision.

---

## 10. Work breakdown

Ordered for the Program Agent. Stage order: strategist → designforge → {blockwright, configsmith, composer} (parallel) → auditron → **pilot (raise PR)** → **PAUSE + real-environment validation approval (human)** → **sentinel (LAST, real env — deferred this run)**. No `bridgesmith` (no integration). No live deploy step. Deploy target for any local install is Auditron's build-validation side effect, not a Pilot task.

| # | stage | agent | task | inputs | expected_artifact |
|---|---|---|---|---|---|
| 1 | plan | strategist | (this stage) requirements + architecture + work breakdown + reference deconstruction | intake, `.aem-skills-config.yaml`, reference URL, supplied assets | `plan/requirements.yaml`, `plan/technical-specifications.md`, `plan/reference-deconstruction.md`, `handoffs/strategist.yaml` |
| 2 | design | designforge | Convert work breakdown into implementation-ready specs: component contracts + dialog specs for `site-header`/`site-footer`; Style System variant specs for hero (`cmp-teaser--hero`), card (`cmp-teaser--innovation-card`), card-grid (`cmp-container--card-grid`), intro (`cmp-text--intro-lead`); `landing-page` template structure spec (D8–D12, D22 — no forbidden structural attributes, design-policy mapping block, no custom dialog/HTL on Core proxies); `design-token-audit.md`; functional + Playwright UI-test case specs. Cite `reference-deconstruction.md §<region>` per component. | `plan/*` | `design/component-specifications.md`, `design/design-token-audit.md`, `design/template-spec.md`, `design/test-cases.md`, `handoffs/designforge.yaml` |
| 3 | implement | blockwright | Author `site-header`/`site-footer` (HTL + dialog + Sling Model `SiteHeaderModel`/`SiteFooterModel` in `com.realmac.aem.core` + wcm.io AEM Mocks unit tests); `landing-page` editable template `structure/` (page-level `cq:policy`, EF chrome refs S1, project container proxy S2, Style System hooks — B3.a–B3.d, B4, B5, B6); SCSS partials in ui.frontend per `reference-deconstruction.md`; Core-Teaser variant hooks (no HTL override on proxies). Author (do NOT execute) Playwright specs in `ui.tests`. | `design/*`, `plan/reference-deconstruction.md` | components under `ui.apps`, models+tests under `core`, SCSS under `ui.frontend`, template under `ui.content`, Playwright specs under `ui.tests`, `handoffs/blockwright.yaml` |
| 4 | implement | configsmith | Content policies + policy mappings for `landing-page` (every `cq:policy` resolves G1; page policy has `clientlibs` G4/G2; single consolidated policy per component with Style System variants G3; allowed-components lists); Style System variant definitions for hero/card/card-grid/intro. No dispatcher/OSGi change required. | `design/*`, template from blockwright | policies + mappings under `ui.content`, `handoffs/configsmith.yaml` |
| 5 | implement | composer | Seed 8 DAM assets → `/content/dam/realmac/tata-innovation/` (binaries + renditions, C11); author header EF + footer EF content (S1, single-component chrome S8); author sample page `/content/realmac/us/en/innovation` as `cq:Page` (S3) with neutral demo copy + meta; register `cq:allowedTemplates` at `/content/realmac`, `/content/realmac/us`, `/content/realmac/us/en` (S4); Style System resolution + content-depth + standard-field-name checks (C7–C10). | `design/*`, template + policies, supplied assets | DAM + EF + page content under `ui.content`, `handoffs/composer.yaml` |
| 6 | test | auditron | Build Validation Gate: `mvn clean install` green (incl. new unit tests); best-practices checks (no deprecated API, no forbidden structural attrs, policy resolution Checks 20–21, Style System resolution); local install validation as a build side-effect. | all `handoffs/*.yaml`, full repo | `handoffs/auditron.yaml` (pass/fail + findings) |
| 7 | release | pilot | **Raise PR** (feature branch `feature/realmac-landing-page` → `master`). Auto after Auditron passes — no human approval precedes it. | `handoffs/auditron.yaml` | `deploy/pr-request.md`, `handoffs/pilot.yaml` |
| 8 | gate | human | **Lead: review/merge PR, deploy to real env, then record real-environment validation approval in DECISIONS.md.** Non-agent pause — Program Agent waits for real env URL + auth mode. | `handoffs/pilot.yaml` | `DECISIONS.md § real-environment validation approval block (real env URL + auth mode)` |
| 9 | test | sentinel | **NFR gate against the REAL environment URL (LAST stage)** — performance (LCP/INP/CLS/TTFB), a11y (WCAG 2.1 AA), SEO, Tier-A visual diff vs `reference-deconstruction.md`, Playwright execution. **Deferred this run** (no env URL); runs at resume against real env (NOT localhost/RDE). | `DECISIONS.md`, `handoffs/auditron.yaml`, `plan/reference-deconstruction.md` | `handoffs/sentinel.yaml` |

**Parallel group:** items 3, 4, 5 (blockwright, configsmith, composer) run in parallel after designforge, with a soft dependency (Composer's policy-dependent authoring + Configsmith's policies both consume Blockwright's template structure; Program Agent may serialize the template-structure handoff, then parallelize the rest).

**Requirements → work-breakdown traceability:**
- US-001 hero → 2,3,4,5,6 · US-002 intro → 2,3,4,5,6 · US-003 cards → 2,3,4,5,6
- US-004 header → 2,3,4,5,6 · US-005 footer → 2,3,4,5,6 · US-006 template → 2,3,4,5,6
- US-007 sample page → 5,6 · US-008 SCSS → 2,3,6 · US-009 Sling Models+tests → 3,6 · US-010 Playwright authored → 2,3,6 (executed at 9, deferred)
Every requirement traces to >=1 work-breakdown item.

---

## 11. Deviations from project defaults
None. Architecture defaults S1 (EF chrome), S2 (container proxy), S3 (cq:Page depth), S4 (allowed-templates at every level), S5/S6/S8 (reuse triage / Core Teaser default / 1:1 reuse), S9/S9.a (reference deconstruction + role classification), S10 (template reuse assessment) are all honored as specified above.
