agent: configsmith
stage: Implement (config/hardening branch)
run-id: 2026-08-28T1200Z-tata-innovation-page
parallel-with: 03-blockwright.md (no dependency between the two — dispatch together)

input-packet: |
  You are `configsmith` for the AEM Program Agent's ADLC run `2026-08-28T1200Z-tata-innovation-page`
  in the `realmac` AEMaaCS project (repo root `C:\AEM\Repos\realmac`, branch
  `feature/realmac-landing-page`). Human Checkpoint 2 (dialog spec confirmation) has been APPROVED
  AS-IS, including explicit approval for the XF Root policy amendment described below.

  ## Required reading before you start
    - `C:\AEM\Repos\realmac\.aem-skills-config.yaml`
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/policy-mapping.md` (your primary spec —
      contains the full policy mapping XML tree, all new policy definitions with exact XML, and the
      existing-policy amendment)
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/template-design.md` §§ Policy mapping,
      Style System hooks, Heading-level budget, and the "Note: pre-existing XF Root policy
      amendment required" section
    - `runs/2026-08-28T1200Z-tata-innovation-page/design/component-specifications.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/designforge.yaml`

  ## Task — 2 tracks

  ### Track 1: `landing-page` policy mapping + new policy definitions
  Author under `ui.content/src/main/content/jcr_root/conf/realmac/settings/wcm/`:
    - `templates/landing-page/policies/.content.xml` — the FULL mapping tree from
      `policy-mapping.md § 1` VERBATIM, including the nested `<realmac><components>`
      resourceType-fallback segments (required because hero/card teasers are dropped at runtime
      under auto-generated node names — do not simplify this away, it is what makes the hero teaser
      and card teaser resolve to two DIFFERENT policies despite both being
      `realmac/components/teaser`).
    - New policy nodes under `settings/wcm/policies/jcr:content/realmac/components/...`:
      - `container/policy_landing_content` (editable parsys — explicit allowlist: teaser, text,
        title, image, button, container — no wildcard)
      - `teaser/policy_landing_hero_teaser` (`titleType=h1`, `allowedTypes=[h1]`,
        `actionsDisabled=true`, `cq:styleGroups` → `cmp-teaser--hero` / `cq:styleId=20260828101`)
      - `text/policy_landing_intro_text` (clone RTE config from the existing
        `policy_641562756958017` verbatim per policy-mapping.md §2.3, add `cq:styleGroups` →
        `cmp-text--intro-lead` / `cq:styleId=20260828102`) — this is a NEW policy, do not edit the
        shared one.
      - `container/policy_landing_card_grid` (explicit allowlist: teaser only;
        `cq:styleGroups` → `cmp-container--card-grid` / `cq:styleId=20260828103`)
      - `teaser/policy_landing_card_teaser` (`titleType=h3`, `allowedTypes=[h3]`, actions NOT
        disabled — cards keep their arrow CTA; `cq:styleGroups` → `cmp-teaser--innovation-card` /
        `cq:styleId=20260828104`)
      - `button/policy_landing_button` (minimal, default options — satisfies G1 only)
    - Copy every XML block from `policy-mapping.md §2` EXACTLY as written — field names, style IDs,
      and the `imageDelegate`/`allowedTypes`/`actionsDisabled` attributes are all load-bearing and
      already verified against project convention (do not invent alternate values).
    - Confirm the reused (unmodified) policies listed in `policy-mapping.md §3` already exist on disk
      at the paths given and resolve correctly for this template's mapping tree — flag in your
      handoff if any are missing or shaped differently than documented (do not silently fix a
      structural mismatch in a SHARED policy; escalate instead, since editing a shared policy has
      blast radius beyond this run).

  ### Track 2: Existing XF Root policy amendment (Configsmith-owned, human-approved)
  `realmac/components/container/policy_1575040440977` ("XF Root", used by the shared
  `xf-web-variation` template for every Experience Fragment in the project):
    - **Current:** `components="[group:Realmac - Content,/apps/realmac/components/form/container]"`
    - **Required change:** `components="[group:Realmac - Content,/apps/realmac/components/form/container,group:Realmac - Structure]"`
    - This is a MINIMAL, additive change to an existing shared policy — add exactly the one group,
      do not remove or reorder the existing entries, do not touch any other property on this policy
      node. Rationale + least-privilege justification is in `policy-mapping.md §4` (already reviewed
      and approved at Human Checkpoint 2 — the human explicitly approved this amendment path).
    - **Do NOT attempt to fix** the separately-observed pre-existing `<mysite>` vs `<realmac>`
      naming oddity in this policy's resourceType-fallback segment (`policy-mapping.md §4` /
      `template-design.md` note) — that is out of this run's fix scope; record it as an observed
      pre-existing item in your handoff for Auditron's awareness, do not remediate it.
    - This amendment is a genuine PRECONDITION for Composer's later work (Composer cannot author
      `site-header`/`site-footer` into the header/footer master XFs until this lands) — treat it as
      a MUST, not a nice-to-have, in this dispatch.

  ### Security review
    - Run the standard pre-deploy security review over the 2 new components (`site-header`,
      `site-footer`) and the policy changes above.
    - No service user / ACL / repoinit change is expected this run (no external integration, no new
      backend service) — verify that assumption holds; if you find a genuine need (e.g. a dedicated
      service user for DAM asset seeding), flag it explicitly rather than silently adding one, since
      the run's scope guardrail assumed none is needed.
    - No dispatcher/CDN config change is required (standard cacheable published Sites page under
      `/content/realmac`) — confirm, do not add speculative rules.

  ## Gate reminders (do not violate)
    - Zero high-severity security findings, or each accepted in DECISIONS.md (route any finding to
      the Program Agent via your handoff rather than silently downgrading severity).
    - Every policy in this run's scope is an EXPLICIT allowlist — no `*` wildcard anywhere
      (self-check table already in `policy-mapping.md §5`; your job is to make the actual JCR nodes
      match that table, not to redesign it).
    - No `loginAdministrative` anywhere in your diff.
    - Secrets externalized (not applicable this run — no secrets introduced).
    - You do NOT author `ui.apps` components, Sling Models, SCSS, or the template `structure/` node
      (Blockwright's parallel track) — only `policies/` + the one XF Root policy amendment.
    - You do NOT invoke `mvn`.

  ## Outputs required from you
    - `landing-page` policy mapping + 6 new policy nodes under `ui.content/.../settings/wcm/...`
    - Amended `policy_1575040440977` (XF Root) — one additive attribute change
    - `runs/2026-08-28T1200Z-tata-innovation-page/implement/configsmith/security-review.md`
    - `runs/2026-08-28T1200Z-tata-innovation-page/handoffs/configsmith.yaml`

  If you hit a Write-permission denial on any `runs/.../` path, use the parent-materialization
  fallback (stage at repo root with a clear filename prefix, print
  `PARENT_MATERIALIZATION_REQUIRED: source=... target=...`).

expected-handoff: C:\AEM\Repos\realmac\.claude\agents\runs\2026-08-28T1200Z-tata-innovation-page\handoffs\configsmith.yaml

gate-criteria: |
  - Every new policy node matches policy-mapping.md §2 verbatim (style IDs, allowedTypes, titleType,
    actionsDisabled, imageDelegate).
  - Policy mapping tree (policies/.content.xml) matches policy-mapping.md §1 verbatim including the
    nested resourceType-fallback segments.
  - XF Root policy (policy_1575040440977) amended additively — exactly one group added, nothing
    else changed.
  - Zero `*` wildcard allowlists introduced.
  - Zero high-severity security findings, or each explicitly flagged for human/Program Agent
    acceptance.
  - No ui.apps/core/ui.frontend files touched (blockwright's track).
  - No mvn invocation by configsmith.
  - handoffs/configsmith.yaml present.
