agent: composer
stage: Integrate (content orchestration)
run-id: 2026-08-28T1200Z-tata-innovation-page
sequencing: DISPATCH ONLY AFTER handoffs/blockwright.yaml AND handoffs/configsmith.yaml both exist
  and pass their stage-03 gates. Do not dispatch in parallel with those two — see DECISIONS.md
  "Sequencing decision" entry for the dependency rationale (components must exist; XF Root policy
  must be amended; landing-page policies must resolve — all three are real preconditions for
  Composer's authoring tasks below, not a convenience ordering).

input-packet: |
  You are `composer` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`
  in the `realmac` AEMaaCS project (repo root `C:\AEM\Repos\realmac`, branch
  `feature/realmac-landing-page`). Blockwright and Configsmith have both completed — the
  `site-header`/`site-footer` components, the `landing-page` template + structure, and all
  landing-page + XF Root policies now exist and resolve.

  ## Required reading before you start
    - `C:\AEM\Repos\realmac\.aem-skills-config.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/authoring-guidelines.md` (your primary
      how-to spec — follow it step by step)
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/reference-assets.md` (the 13-asset manifest
      — authoritative count, corrects an "8" typo elsewhere in the run's docs)
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/template-design.md` §§ cq:allowedTemplates
      registration, Authoring depth
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/authoring-test-cases.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/blockwright.yaml` +
      `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/configsmith.yaml` (confirm both status:
      pass before you start; if either is not pass, stop and report back rather than proceeding)

  ## Task — 4 tracks

  ### Track 1: DAM asset seeding
  Seed ALL 13 files from `C:\Users\2489691\Downloads\tata-innovation-assets` into
  `/content/dam/realmac/tata-innovation/` as real `dam:Asset` nodes with actual binaries + original
  renditions (never a `dam:Asset` node without a rendition):
    - `about_innovation_banner_desktop_1920x1080.jpg`
    - `TataChemicals_Desk.jpg`, `TataSteelEurope_Desk.jpg`, `TMETC_Desk.jpg`,
      `tcsinnovation_information_desktop_360x260.jpg`
    - `tata-logo.svg`, `search.svg`, `video.svg`, `close.svg`, `ContactUs.svg`, `FB.svg`,
      `Instagram.svg`, `Linkedin.svg`
  Per `reference-assets.md`: 11 of these are consumed by components this run; `video.svg` and
  `close.svg` are explicitly "supplied, not-used-this-run" — seed them anyway (for DAM completeness /
  no unexplained gap), but do not force them into any component's dialog. Author sensible
  `dc:title`/alt-text metadata for each image asset where the project convention expects it.

  ### Track 2: Verify (not assume) template registration
  Confirm `landing-page` actually appears in the Create Page wizard at `/content/realmac/us/en`.
  `template-design.md § cq:allowedTemplates registration` states the existing permissive regex
  `[/conf/realmac/settings/wcm/templates/(?!xf-).*]` at `/content/realmac` already covers it — but
  you must VERIFY this on the actual repo/instance state, not take the design doc's prediction on
  faith. If verification fails for any reason, add an explicit override
  `cq:allowedTemplates="[/conf/realmac/settings/wcm/templates/(?!xf-).*]"` on
  `/content/realmac/us/en`'s `jcr:content` as the documented fallback. Record which path you took
  in your handoff.

  ### Track 3: Replace legacy header/footer master XF content (human-approved content mutation)
  Human Checkpoint 2 explicitly approved this as a content mutation on existing nodes:
    - Open `/content/experience-fragments/realmac/us/en/site/header/master`. **Remove** its existing
      Navigation + LanguageNavigation + Search component instances and **replace** with exactly ONE
      `site-header` instance, authored per `authoring-guidelines.md §6`:
      - Logo: `tata-logo.svg`, alt text "Realmac" (or similar neutral text — not Tata's brand
        language).
      - Navigation: root `/content/realmac/us/en`, depth `1`.
      - Utility Links: Search (`search.svg`) and Contact (`ContactUs.svg`), each with label, icon,
        link URL, and a real accessibility label (these are icon-only links — required per a11y
        NFR).
    - Open `/content/experience-fragments/realmac/us/en/site/footer/master`. **Remove** its existing
      Separator + Text content and **replace** with exactly ONE `site-footer` instance, authored per
      `authoring-guidelines.md §7`:
      - Columns: 4–5 link columns with heading + nested label/URL links (author reasonable neutral
        demo link targets/labels mirroring the reference's structure — do not invent Tata-specific
        URLs).
      - Social: Facebook (`FB.svg`), LinkedIn (`Linkedin.svg`), Instagram (`Instagram.svg`), Contact
        (`ContactUs.svg`) — each with icon, URL, and label.
      - Legal: a NEUTRAL realmac copyright line, e.g. "© 2026 Realmac. All rights reserved." — do
        NOT copy Tata's copyright text ("© 2019-2027 Tata Sons Private Limited...") verbatim; this
        is a hard requirement (S9.a, reference is visual-reference-only).
    - Per S8 / the run's design: do not leave the old fragmented components alongside the new one —
      this is a genuine replace, not an addition.

  ### Track 4: Sample page authoring
  Author `/content/realmac/us/en/innovation` as a real `cq:Page` (verify `us` and `en` already exist
  as `cq:Page`, not `nt:folder` — they should, per Strategist's verification, but confirm) using the
  `landing-page` template, per `authoring-guidelines.md §§1-5`:
    1. Set page title + SEO meta description in Page Properties (there is no structural page-title
       component on this template — this is the only place page-level SEO text lives).
    2. In the one editable content area (`jcr:content/root/container/container`), author in order:
       - **Hero Teaser** — Style System "Hero"; Title = "Innovation"; image =
         `about_innovation_banner_desktop_1920x1080.jpg` from the DAM; no CTA (disabled by policy).
       - **Intro Text** — Style System "Intro Lead"; author neutral demo copy mirroring the
         reference's *structure* (lead paragraph + strategy body, per
         `reference-deconstruction.md § Region 3`) — do NOT copy Tata's actual sentences verbatim;
         write original text that covers the same narrative shape (per S9.a / Q-002).
       - *(Optional)* secondary `h2` Title component for a "strategy" sub-heading, if your copy
         calls for one.
       - **Card Grid Container** — Style System "Card Grid"; inside it, 4 **Teaser** instances
         (Style System auto-scopes to "Innovation Card"), one per innovation centre, using the 4
         supplied card images + neutral demo titles/descriptors mirroring the reference's 4 named
         business-unit cards (Tata Chemicals Innovation Centre · Tata Steel Europe RD&T · TCS
         Innovation Labs · Tata Motors European Technical Centre — you may keep these names as
         structural placeholders since they are factual business-unit names, not proprietary
         marketing copy, but flag this choice in your handoff for the human's awareness) — each with
         an arrow-link action (label + URL).
    3. Ensure the page renders end-to-end conceptually (hero + intro + 4 cards + header/footer EF
       chrome) with no "Please configure" / empty-component placeholders once seeded.

  ## Gate reminders (do not violate)
    - CF Models / persisted queries: NONE this run (server-rendered, dialog-authored only) — do not
      create a headless track.
    - Every seeded DAM asset has a real binary + rendition (C11).
    - Every seeded page/EF references a real, existing template/component (no dangling
      `sling:resourceType`).
    - Content lands ONLY in `ui.content/` (mutable) — never in `ui.apps/` (immutable).
    - Do not invent Tata verbatim copy anywhere (headings, body text, legal line) — neutral demo
      copy only, mirroring structure not content.
    - You do NOT invoke `mvn`.

  ## Outputs required from you
    - DAM assets under `ui.content/.../content/dam/realmac/tata-innovation/` (all 13 files)
    - Header/footer master XF content replaced under
      `ui.content/.../content/experience-fragments/realmac/us/en/site/{header,footer}/master/`
    - Sample page under `ui.content/.../content/realmac/us/en/innovation/`
    - `runs/2026-08-28T1200Z-tata-innovation-page/integrate/composer/dam-fixture-manifest.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/integrate/composer/content-seeding-report.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/composer.yaml`

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\composer.yaml

gate-criteria: |
  - All 13 supplied assets seeded to /content/dam/realmac/tata-innovation/ with real binaries +
    renditions.
  - Template-registration verification performed and result recorded (not assumed).
  - Header + footer master XFs contain exactly one site-header / site-footer instance each, with the
    prior legacy components fully removed (not left alongside).
  - Sample page /content/realmac/us/en/innovation authored with hero + intro + 4-card grid, using
    the seeded DAM assets, no placeholder/empty-component state.
  - No Tata verbatim copy anywhere in authored content (especially the footer legal line).
  - No ui.apps/core/ui.frontend/ui.content-policy files touched (blockwright's / configsmith's
    tracks).
  - No mvn invocation by composer.
  - handoffs/composer.yaml present.
