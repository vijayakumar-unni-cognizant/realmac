agent: strategist
stage: Plan
run-id: 2026-08-28T1200Z-tata-innovation-page

input-packet: |
  You are `strategist` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`
  in the `realmac` AEMaaCS project (repo root `C:\AEM\Repos\realmac`, branch
  `feature/realmac-landing-page`).

  ## Task
  Structure requirements + solution architecture + work breakdown for a NEW AEM page that visually
  matches the reference page:

      https://www.tata.com/about-us/innovation

  This reference URL is a VISUAL / DESIGN REFERENCE ONLY. Do NOT recommend transplanting its
  HTML/DOM/CSS. Every component must be planned as a fresh AEMaaCS artifact (HTL + dialog + Sling
  Model + ui.frontend SCSS), preferring extension of existing project components / AEM Core
  Components before proposing new ones.

  Fetch the reference URL (WebFetch) and produce a per-region visual breakdown — this run has an
  active reference-fidelity gate (ADLC-SPEC §P6): you MUST emit `plan/reference-deconstruction.md`
  containing a per-region breakdown of every visible section of the reference page (header/nav,
  hero banner, intro/overview text block, innovation showcase card grid, footer/social chrome, and
  any others you observe) — typography scale, spacing rhythm, grid structure, and color usage per
  region, to the extent the reference page's public markup lets you observe them. This is
  Designforge's authoritative input for componentspecs later, so be concrete (e.g. approximate
  heading sizes, card grid column count at desktop/tablet/mobile, image aspect ratios).

  ## Supplied local assets (already on disk — reference these by relative DAM-target path, do not
  treat them as remote-fetchable; Composer will seed them into the DAM in a later stage)
  Source folder: `C:\Users\2489691\Downloads\tata-innovation-assets`
    - about_innovation_banner_desktop_1920x1080.jpg — hero banner image
    - TataChemicals_Desk.jpg, TataSteelEurope_Desk.jpg, TMETC_Desk.jpg,
      tcsinnovation_information_desktop_360x260.jpg — 4 innovation showcase cards (business units)
    - tata-logo.svg — brand logo (for demo purposes only; do NOT use Tata's real logo/brand as final
      production branding — flag this as a licensing/branding consideration in your NFR risks, but
      proceed with it for this demo build since the human supplied it explicitly as a local asset)
    - search.svg, video.svg, close.svg, ContactUs.svg, FB.svg, Instagram.svg, Linkedin.svg — icons
      for header/footer chrome

  ## Project facts (verified — use verbatim, do not re-scan)
    - Maven groupId: com.realmac, artifactId: realmac
    - Java package base: com.realmac.aem.core ; Java version 21 (`.cloudmanager/java-version`)
    - App root: /apps/realmac ; conf root: /conf/realmac ; content root: /content/realmac ;
      DAM root: /content/dam/realmac
    - Component group convention (verified from existing components):
      `componentGroup="Realmac - Content"`
    - Existing Core-Component-based components under
      `ui.apps/src/main/content/jcr_root/apps/realmac/components/` include: teaser, carousel,
      container, image, text, title, list, navigation, breadcrumb, accordion, button, download,
      contentfragment, and others — inventory the actual directory before proposing new components,
      and prefer extending/configuring these (e.g. teaser for the hero banner, a card-list/carousel
      pattern for the showcase grid) over authoring net-new components where a reasonable fit exists.
    - `.aem-skills-config.yaml` now exists at repo root (created this session) with these exact
      values — read it to confirm.

  ## Scope guardrails
    - Full server-rendered Sites page. NO headless/GraphQL/CF-Model architecture unless you find a
      concrete reason the showcase cards or hero content genuinely need CF-backed reuse elsewhere —
      default assumption is direct dialog-authored content (simpler, matches "sample page
      demonstrating full layout" deliverable).
    - NO external system integration — Bridgesmith is not in this run's specialist roster. Do not
      propose any external API/webhook/IDP touchpoint.
    - Sentinel (post-deploy NFR + Playwright execution) will NOT run this session — flag this in
      your NFR risk section as "deferred pending environment URLs," but still specify the NFR
      targets (performance/SEO/a11y/visual-fidelity) that Sentinel would eventually enforce, and
      note that Blockwright must still author (not execute) Playwright specs pre-deploy so Cloud
      Manager's Custom UI Testing runs Playwright, not Cypress.
    - Deploy is gated as normal: Auditron → Pilot raises PR → pause. Do not plan around a live
      deploy this session.

  ## Deliverable expectations to size into your work breakdown
    - Editable template (reuse existing template type if one fits; else propose new template) +
      content policies for the page.
    - Components for: hero banner, intro/overview text section, innovation showcase card grid (4
      cards: image + title + link, matching Tata's card grid layout), any header/footer chrome
      needed to match layout fidelity (logo, nav, social icons, contact link) — assess whether
      existing navigation/breadcrumb/teaser/carousel components can be configured/extended for
      these before proposing new ones.
    - Sling Models + unit tests (wcm.io AEM Mocks) for any new Java-backed components.
    - SCSS partials in ui.frontend for typography/spacing/layout/responsive breakpoints matching the
      reference deconstruction.
    - A sample authored page at /content/realmac using the seeded DAM assets.
    - Green `mvn clean install` (Auditron's Build Validation Gate).

  ## Outputs required from you
    - `runs/2026-08-28T1200Z-tata-innovation-page/plan/requirements.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/plan/technical-specifications.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/plan/reference-deconstruction.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/strategist.yaml`

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback described in `.claude/agents/aem-program-agent.md` — stage the file at repo root with a
  clear prefix and print `PARENT_MATERIALIZATION_REQUIRED: source=... target=...` in your final
  response; the Program Agent will move it into place on resume.

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\strategist.yaml

gate-criteria: |
  - Every requirement in requirements.yaml has >=1 acceptance criterion.
  - Every requirement traces to >=1 work-breakdown item.
  - No deprecated AEM API recommended.
  - Every NFR has a mitigation owner (Sentinel's deferred status is an acceptable mitigation note).
  - reference-deconstruction.md covers every visible region of the reference page.
  - Architectural pattern chosen and justified (server-rendered Sites vs headless) — expect
    server-rendered Sites per scope guardrails unless strongly justified otherwise.
  - Component list explicitly marks each as reuse-existing vs new, with rationale.
