## Summary

Tata Innovation landing page with header/footer chrome and comprehensive Playwright testing.

- **Landing page template** (editable, responsive, section-based structure)
- **Site header component** with utility links and social links (Sling Model + HTL)
- **Site footer component** with footer columns and social integration (Sling Model + HTL)
- **Innovation card component** (4-card grid showcase, using Core Teaser with custom policy)
- **Hero teaser, intro text, and section container layout**
- **18 Playwright UI test specs** (desktop, tablet, mobile; A11y, landmarks, keyboard focus)
- **DAM assets**: 10 SVG icons + 4 featured imagery for the showcase
- **Updated filter.xml and WCM policies** for new template and components

## Build Status

**Status**: BUILD_SUCCESS (Iteration 5 / Sanity-only dispatch)

- **Build command**: `mvn clean install -PautoInstallSinglePackage`
- **Exit code**: 0 ✓
- **all.zip present**: 1,249,225 bytes ✓
- **Unit tests**: 20/20 pass, 0 failures ✓
- **Build hash**: `e260c64ec27823a644bd7c02fc157c09453b45fc`

### Functional Test Cases (TC Ledger)

- **Total**: 46 test cases
- **Pass**: 38
- **Accepted known gap**: 2 (TC-012, TC-015 — both due to CQ-09 only)
- **Blocked** (no dedicated fixture): 6 (TC-003, TC-005, TC-009, TC-014, TC-020, TC-025)

All three signal gates passed: exit code 0, all.zip produced, surefire 20/20 green.

## Known Gap — CQ-09 (Accepted, Non-Blocking)

**Innovation card heading level and style class mismatch** (human decision: DECISIONS.md 2026-08-28T20:50Z)

- **Symptom**: The 4 showcase cards render with Core Teaser's default `<h2>` heading tag instead of `<h3>`, and lack a distinct `.cmp-teaser--innovation-card` style class.
- **Root cause**: AEM's nested-container Content Policy resolution limitation; the card policy inherits Core Teaser's default heading level. A workaround would require a custom card component wrapper or post-render DOM manipulation.
- **Acceptance**: This is a cosmetic, SEO-friendly gap (the page still has 1 primary `<h1>` hero and 4 secondary headings). It does NOT block the landing page from publishing.
- **Validation in Sentinel**: If CQ-09 is observed during Playwright visual or accessibility checks, it is an accepted-gap finding, NOT a new defect. Citation: DECISIONS.md 2026-08-28T20:50Z.

## UI / Performance / A11y / Observability — Deferred to Real Environment

**These validations are NOT yet run** and will be performed by Sentinel AFTER this PR is merged and deployed to the real Author/Publish environment:

- Playwright responsive design tests (UI-001 through UI-016)
- Performance metrics (time-to-interactive, cumulative layout shift, etc.)
- Full accessibility scan (WCAG 2.1 AA via axe/Playwright)
- SEO meta tags and Open Graph validation
- GraphQL parity (if applicable)
- Production observability (logs, metrics, alerts)

**This PR review is** a code / architecture review only. Real-world validation happens post-deployment on the live environment.

## Local Build Notes

The working tree contains 3 intentional, local-only Maven POM edits (CQ-03 resolution) to work around a Zscaler network restriction that blocks the `frontend-maven-plugin` node/npm download:

- `pom.xml` (root): `frontend-maven-plugin` skip node/npm install
- `ui.apps/pom.xml`: commented-out realmac.ui.frontend zip dependency
- `ui.frontend/pom.xml`: `skipAssembly=true`

**These 3 files are intentionally EXCLUDED from this PR commit** so that Cloud Manager's production pipeline runs the normal, unmodified frontend build. If you see these edits in your local checkout, they are pre-existing and should remain local-only.

## Post-Merge Lead Checklist

Once approved, the Lead must:

1. **Merge this PR** to `master`
2. **Sync to Adobe Git** (the real deployment source)
3. **Deploy to the real environment** via Cloud Manager (Dev → Stage → Prod as applicable)
4. **Supply real environment URLs + auth mode** to the ADLC run:
   - Author URL (e.g., https://author-p123456-e789.adobeaemcloud.com)
   - Publish URL (e.g., https://publish-p123456-e789.adobeaemcloud.com)
   - Auth mode (`none` for public, `bearer-token`, or other credentials)
5. **Record the deployment in DECISIONS.md** and resume the ADLC run with `status: lead_approved`

Once you supply the real environment URLs, the Sentinel stage will validate the landing page against Playwright specs, accessibility rules, performance baselines, and other non-functional requirements on the live environment.

---

Generated with [Claude Code](https://claude.com/claude-code)
