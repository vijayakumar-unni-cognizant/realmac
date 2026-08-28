# Sentinel — Test (UI + NFR) — 2026-08-28T1200Z-tata-innovation-page

- **status:** FAIL
- **run:** 2026-08-28T1200Z-tata-innovation-page
- **url:** https://publish-p185256-e1945105.adobeaemcloud.com/content/realmac/us/en/innovation.html

> 3 NEW, unaccepted, correctness-class findings (Playwright harness auth defect blocking 0/18 spec execution; dangling internal link targets suppressing anchor+aria-label across header/footer/card CTAs; duplicate header/footer landmarks) plus 1 serious a11y threshold miss (mobile hero contrast 1.18:1) and an LCP threshold miss (3015ms). CQ-09 (cards render `h2` not `h3`, no `.cmp-teaser--innovation-card` class) is correctly cited as the pre-existing, human-**accepted** gap per `DECISIONS.md 2026-08-28T20:50Z` and does **not** contribute to this FAIL. The Style System clientlib CSS is confirmed **present and correctly compiled** on Publish (Cloud Manager's frontend build ran as expected) — this is explicitly **not** a new defect.

## Scores

- UI Tests: **0** (0/18 · blocked_harness_defect)
- Performance: **93** (LCP 3.0s (target ≤2.5s))
- Accessibility: **95** (Lighthouse baseline)
- Best Practices: **96** (1 console error (favicon))
- SEO: **92** (canonical + robots.txt fail)
- A11y (axe, authoritative): **70** (1 serious @ mobile, 0 critical)
- Visual (Tier A fidelity): **67** (4/6 regions match layout intent)

## Track matrix

| Track | Verdict | Detail |
|---|---|---|
| UI Tests (Playwright) | INCOMPLETE | 0/18 executed — harness `global-setup.js` auth-mode mismatch aborts the entire binary before any spec runs (F-HARNESS-01) |
| NFR Baseline (Lighthouse — Publish) | FAIL | Perf 93 / A11y 95 / BP 96 / SEO 92 — but LCP 3015ms misses the 2500ms target; canonical + robots.txt SEO audits fail |
| A11y-deep (axe, full ruleset) | FAIL | 0 critical, 1 serious (mobile hero contrast), 10 moderate (duplicate landmarks) across both viewports |
| SEO-deep | FAIL | canonical is relative not absolute; robots.txt/sitemap.xml 404; no OpenGraph tags (pre-existing/out-of-scope, informational) |
| Observability | PASS | ACDL (Adobe Client Data Layer) baseline confirmed wired (18 `data-cmp-data-layer` attrs + commons.datalayer clientlibs loaded); no new Launch/Analytics requirement this run |
| Authoring Provisions (Author) | PASS | 20/20 AUTH cases executed: 17 pass, 3 na (disclosed fixture gap, matches Auditron's own TC-003/005/009/014/020/025 disclosure) |
| GraphQL Content Parity | N/A | server-rendered Sites page, no CF Models / persisted queries in scope this run (`handoffs/composer.yaml → headless: null`) |
| SPA Integration | N/A | no headless/SPA front-end consumer in this repo |
| Visual Verification (Tier A) | FAIL | 4/6 regions match reference layout intent; mobile hero contrast diverges (major) and card CTA arrow never renders (high, correctness) |

## Findings

### [HIGH] F-HARNESS-01 — [Correctness-class, NEW] Playwright global-setup implements credentials-mode login only; aborts ALL 18 specs against this run's bearer-token Author tier
- **Issue:** `playwright.config.js`'s top-level `globalSetup` (`global-setup.js`) performs a Granite `j_security_check` form login using `AEM_AUTHOR_USERNAME`/`AEM_AUTHOR_PASSWORD` (default `admin`/`admin`) unconditionally for the entire test binary, regardless of `--project` filtering. This run's real Author tier auth mode is bearer-token only (per the resume block) — there is no local admin account and no username/password was ever supplied (correctly so). The login 403s, and because Playwright's `globalSetup` is not project-scoped, this single failure blocks all 18 specs — including the 16 anonymous Publish-tier specs that need no author auth at all.
- **Evidence:** `npm test` run this dispatch: `Error: AEM author login failed (HTTP 403) at https://author-p185256-e1945105.adobeaemcloud.com. at global-setup.js:22`. Full log at `/tmp/aem-playwright.log`. Confirmed separately via `curl` that the same bearer token IS valid and IS accepted by the real Author tier when sent as an `Authorization: Bearer` header (`/libs/granite/security/currentuser.json` → 200, `authorizableId: vijayakumar.unni@cognizant.com`) — the credential is fine, the harness's auth *mechanism* is the wrong one for this environment.
- **Cause:** The harness was authored against a local-SDK/credentials-mode assumption (admin/admin) and never gained a bearer-token code path, despite bearer-token being a documented, standard AEMaaCS Author auth mode in Sentinel's own environment-targets contract.
- **Recommended fix:** Add a bearer-token branch to `global-setup.js`: when `process.env.AEM_AUTHOR_BEARER_TOKEN` is set, build the `storageState`/context via `extraHTTPHeaders: { Authorization: 'Bearer ' + token }` (or an equivalent IMS-cookie construction) instead of attempting `j_security_check`, and skip the form-login path entirely in that mode. Additionally, consider scoping author-tier auth setup to only the `author-*` projects (e.g. via Playwright's project-dependency / per-project `use.storageState` pattern) so a future author-auth failure cannot collaterally block the anonymous publish-tier specs.
- **Route:** blockwright
- **Status:** open

### [MEDIUM] F-HARNESS-02 — [Threshold-class, NEW] Missing Firefox and mobile-emulation Playwright projects
- **Issue:** `playwright.config.js` defines only 3 projects: `publish-chromium`, `publish-webkit`, `author-chromium`. There is no Firefox project and no device-emulated mobile project, despite Sentinel's own cross-browser contract ("Chromium, Firefox, WebKit + mobile emulation is the default") and `requirements.yaml`'s explicit browser matrix ("Last 2 versions of Chrome, Edge, Firefox, Safari... iOS Safari and Android Chrome").
- **Evidence:** Read `playwright.config.js` directly: `projects: [publish-chromium, publish-webkit, author-chromium]` — no `firefox` entry, no `devices['iPhone ...']`/`devices['Pixel ...']` entry.
- **Cause:** Harness scaffolded with 2 desktop engines + 1 author engine only; mobile coverage was left to in-spec `page.setViewportSize()` calls (present in UI-003/008/013/015), which emulates viewport size but not a genuine mobile browser engine/UA.
- **Recommended fix:** Add a `firefox` project (`devices['Desktop Firefox']`, testDir `./tests/publish`) and a mobile-emulation project (e.g. `devices['Pixel 7']` or `devices['iPhone 14']`, testDir `./tests/publish`) to close the cross-browser/device gap.
- **Route:** blockwright
- **Status:** open

### [HIGH] F-LINK-01 — [Correctness-class, NEW] Dangling internal link targets silently suppress anchor + aria-label rendering across header, footer, and all 4 card CTAs
- **Issue:** The vast majority of this page's navigational chrome renders with **no functional href and no accessible name at all**: both header utility links (Search, Contact), 7 of the footer's 9 internal links (Company: About Us/Careers/Newsroom; Resources: Media Library/Publications; Legal: Privacy Policy/Terms of Use), the footer's Contact social link, and all 4 innovation-card "Learn More" CTAs render as bare `<img>`/plain text with zero `<a>` wrapper. Only links whose target is an existing page (the footer's 2 "Innovation" column links, which point at the sample page itself, and the 3 external social URLs) render as real anchors.
- **Evidence:** Publish DOM: card action-container is `<div class="cmp-teaser__action-container">Learn More</div>` with no nested `<a>` (4/4 cards). Header: `<li class="cmp-site-header__utility-item"><img .../></li>` with no `<a>` (2/2). Footer: `<li>About Us</li>` etc. with no `<a>` (7/9 links + 1/4 social). Author-tier render of the SAME node in edit mode surfaces Core Components' own diagnostic: `alt="invalid link: /content/realmac/us/en/search.html"` and `alt="invalid link: /content/realmac/us/en/contact.html"` — direct confirmation from AEM's own LinkHandler. All named target paths 404 on Publish (`search.html`, `contact.html`, `tata-chemicals.html`, `tata-steel-europe.html`, `tcs-innovation-labs.html`, `tata-motors-etc.html`, `about.html`, `careers.html`, `newsroom.html`, `media.html`, `publications.html`, `privacy.html`, `terms.html` — all 404). This breaks Playwright spec **UI-009** (`.cmp-teaser__action-link` must be visible with a truthy href — 0/4 cards satisfy this) and **UI-016** (keyboard tab order cannot reach non-existent anchors), and violates `requirements.yaml`'s explicit a11y requirement ("aria-labels on icon-only links (search, social, contact)") for 3 of 4 icon-only header/social contacts.
- **Cause:** Core Components' `LinkHandler` resolves an authored internal path against the actual repository; when the target resource does not exist, it returns an *invalid* `Link` and the shared HTL idiom (used uniformly by Core Teaser's action rendering and by the header/footer's own link rendering) correctly, by design, omits the `<a>`+`aria-label` markup rather than emit a broken link. The root cause is therefore a **content-authoring gap** (Composer authored realistic-looking internal paths for pages that were never created in this single-page-demo run's scope), not a component code defect.
- **Recommended fix:** Either author the referenced sub-pages (out of this run's original scope) or repoint the CTAs/utility links/footer links at a target that genuinely exists — the sample page itself, an external URL, or (for icon-only utility controls with no real destination this run) omit the `linkURL` and adjust the component to render a non-interactive icon in that case rather than an intentionally-broken internal path. Do not add a workaround inside the shared LinkHandler-consuming HTL — its current suppression behavior is correct and should be preserved.
- **Route:** composer
- **Status:** open

### [HIGH] F-LANDMARK-01 — [Correctness-class, NEW] Duplicate header/footer landmarks — the XF wrapper and the component both render a semantic `<header>`/`<footer>` tag
- **Issue:** Core Component's Experience Fragment component renders its own outer `<header class="experiencefragment">` / `<footer class="experiencefragment">` tag (per the XF variation's own template convention), and `site-header.html`/`site-footer.html` render a second, nested `<header class="cmp-site-header">` / `<footer class="cmp-site-footer">` inside it — producing 2 `<header>` and 2 `<footer>` elements on every page.
- **Evidence:** axe full-ruleset sweep (both viewports): `landmark-banner-is-top-level`, `landmark-contentinfo-is-top-level`, `landmark-no-duplicate-banner`, `landmark-no-duplicate-contentinfo`, `landmark-unique` — 5 moderate violations, reproduced at both 1440×900 and 390×844. Raw DOM grep: `grep -c '<header' innovation.html` → 2; same for `<footer>` → 2. This directly and deterministically fails Playwright specs **UI-010** and **UI-012**, both of which assert `page.locator('header').toHaveCount(1)` / `page.locator('footer').toHaveCount(1)`.
- **Cause:** The XF's own component template (driven by the XF variation type used for `header`/`footer` master fragments) renders a semantic landmark tag around whatever content is authored inside it; authoring a component that ALSO renders the same semantic tag doubles the landmark.
- **Recommended fix:** Either change `site-header.html`/`site-footer.html` to render a non-landmark wrapper (`<div>`) since the outer XF already supplies the `<header>`/`<footer>` landmark, or configure the XF template/policy so its own wrapper renders a plain `<div>` and let `site-header`/`site-footer` own the single semantic tag. Pick one owner of the landmark, not both.
- **Route:** blockwright
- **Status:** open

### [HIGH] F-A11Y-01 — [Threshold-class, NEW] Mobile hero title fails color contrast (1.18:1, requires ≥4.5:1) — directly violates the run's own pinned a11y NFR
- **Issue:** At the 390×844 mobile viewport, the hero H1 ("Innovation") renders in white (#ffffff) text on a plain light-gray (#ececec) background — 1.18:1 contrast, far below the 4.5:1 WCAG AA threshold `requirements.yaml`'s own accessibility_notes explicitly names ("Scrim contrast >=4.5:1 for hero title"). At the 1440×900 desktop viewport the SAME title correctly overlays the dark scrim gradient over the hero image and passes (no violation reported at desktop).
- **Evidence:** Sentinel's standalone axe sweep, mobile viewport: `serious color-contrast — Element has insufficient color contrast of 1.18 (foreground color: #ffffff, background color: #ececec, font size: 21.0pt (28px), font weight: bold)`, target `h1`. Visual confirmation via `screenshots/innovation-mobile.png` vs `screenshots/innovation-desktop.png`: on mobile the title sits in a separate plain gray block BELOW the hero image, not overlaid on it; on desktop it correctly overlays the image with the dark scrim visible behind it. Lighthouse's own mobile-form-factor run also independently flagged `color-contrast` on the same element.
- **Cause:** The mobile breakpoint of `.cmp-teaser--hero` appears to stack the title outside the image+scrim bounds (or the scrim's dark background does not extend to cover the title's position at this breakpoint), unlike the desktop layout where content is layered (z-index 3) over the scrim (z-index 2) over the image (z-index 1).
- **Recommended fix:** Confirm the mobile-breakpoint CSS keeps `.cmp-teaser__content` positioned over `.cmp-teaser__image`/its `::after` scrim (as at desktop) rather than allowing it to render in normal flow below the image at narrow viewports — likely a `position:relative`/`flex`/`height` rule that only applies above a breakpoint boundary.
- **Route:** blockwright
- **Status:** open

### [MEDIUM] F-PERF-01 — [Threshold-class] LCP 3015ms exceeds the 2500ms target
- **Issue:** Lighthouse (mobile form-factor, simulated throttling) measures LCP at 3.0s against this run's ≤2500ms NFR target. CLS (0.0001) and TBT (0ms) both comfortably pass.
- **Evidence:** `lighthouse-innovation.json`: `largest-contentful-paint` = 3015.168ms displayValue "3.0 s"; Performance category score 93/100.
- **Cause:** The LCP element is the hero image, delivered via Adobe Dynamic Media (`/adobe/dynamicmedia/deliver/...`); no `fetchpriority="high"` or preload hint was observed on the hero `<img>`.
- **Recommended fix:** Add `fetchpriority="high"` (and/or a `<link rel="preload" as="image">` for the correctly-sized DM rendition) to the hero image, and confirm the DM `width={width}` responsive srcset is delivering an appropriately small rendition for the mobile viewport rather than a larger one being downscaled client-side.
- **Route:** blockwright
- **Status:** open

### [MEDIUM] F-SEO-01 — [Threshold-class] Canonical tag is a relative path, not an absolute URL
- **Issue:** `<link rel="canonical" href="/content/realmac/us/en/innovation.html"/>` — Lighthouse's `canonical` SEO audit fails explicitly for this reason ("Is not an absolute URL").
- **Evidence:** `lighthouse-innovation.json` audit `canonical`: score 0, explanation "Is not an absolute URL (/content/realmac/us/en/innovation.html)". Confirmed via raw HTML fetch.
- **Cause:** The canonical tag is generated relative to the current host rather than through an Externalizer-style absolute-URL resolution.
- **Recommended fix:** Configure the project's Externalizer OSGi config with the publish domain, and generate the canonical tag via `Externalizer.publishLink(...)` (or equivalent) so it resolves to an absolute `https://` URL.
- **Route:** configsmith
- **Status:** open

### [LOW] F-SEO-02 — [Threshold-class, pre-existing/out-of-scope] robots.txt and sitemap.xml both 404 on Publish
- **Issue:** `GET /robots.txt` → 404, `GET /sitemap.xml` → 404 on the real Publish tier. Lighthouse's `robots-txt` audit fails as a result.
- **Evidence:** `curl -I https://publish-.../robots.txt` → 404; `curl -I .../sitemap.xml` → 404.
- **Cause:** Project-wide dispatcher/sitemap configuration, not something this run's feature work touches — `requirements.yaml § seo` for this run does not name robots.txt/sitemap as in scope.
- **Recommended fix:** Track as a project-level backlog item (not blocking for this run): add a `robots.txt` and a sitemap generator/config at the dispatcher/project level.
- **Route:** configsmith
- **Status:** informational-out-of-scope

### [LOW] F-BP-01 — [Threshold-class] favicon.ico 404 logs a console error (Best Practices audit)
- **Issue:** Lighthouse's `errors-in-console` Best Practices audit fails: "Failed to load resource: the server responded with a status of 404 (Not Found)" for `/favicon.ico`.
- **Evidence:** `lighthouse-innovation.json` audit `errors-in-console`, single network item, `favicon.ico`.
- **Cause:** No favicon asset registered for the site.
- **Recommended fix:** Add a `favicon.ico` (or a `<link rel="icon">` reference to a seeded DAM/clientlib asset) at the project level.
- **Route:** configsmith
- **Status:** informational-out-of-scope

### [LOW] F-SEO-03 — [Threshold-class, informational, out-of-scope] No OpenGraph tags present
- **Issue:** No `og:title`/`og:description`/`og:image`/`og:url`/`og:type` meta tags found in `<head>`. Not required by this run's `requirements.yaml § seo` scope.
- **Evidence:** Full `<head>` fetched and grepped for `og:`/`twitter:` — zero matches.
- **Cause:** Never in scope for this run's requirements.
- **Recommended fix:** No action required this run; revisit if social-share preview fidelity becomes a requirement.
- **Route:** composer
- **Status:** informational-out-of-scope

### [LOW] CQ-09 — [Threshold-class, ACCEPTED KNOWN GAP] 4 showcase cards render Core Teaser's h2 default and lack the .cmp-teaser--innovation-card style class
- **Issue:** The 4 innovation-card instances render with Core Teaser's hardcoded `h2` heading level (not the intended `h3`) and never receive the `.cmp-teaser--innovation-card` style class, because AEM's nested Content Policy resolution does not reliably apply across 2 stacked levels of author-dropped (non-structural) container nesting.
- **Evidence:** Independently reconfirmed live on Publish this dispatch: `grep -c cmp-teaser--innovation-card` on the rendered page → 0; 4 `<h2 class="cmp-teaser__title">` where `<h3>` is intended (confirmed the clientlib CSS DOES contain the `.cmp-teaser--innovation-card` rule — 9 occurrences — so this is a class-application gap, not a missing-CSS gap).
- **Cause:** AEM platform characteristic: Content Policy resolution is reliable for one level of resourceType-based disambiguation from a structural ancestor, but becomes unreliable across two stacked levels of author-dropped ancestors (root-caused by the Program Agent at DECISIONS.md Stage 12).
- **Recommended fix:** Human-accepted; no action required this run. If revisited: a thin, deterministic HTL-level h3+class override on innovation-card (Option 1, previously recommended) would resolve this without depending on Content Policy resolution succeeding.
- **Route:** n/a — human-accepted
- **Status:** accepted_known_gap (DECISIONS.md 2026-08-28T20:50Z)

### [INFO] F-INFO-01 — [Confirmed GOOD — explicitly checked per this run's critical instruction] Style System clientlib CSS is present and correctly compiled on Publish
- **Issue:** N/A — this is a confirmation, not a defect. Explicitly checked to distinguish from CQ-03's LOCAL-ONLY Zscaler build-skip (never in scope for the real Cloud Manager pipeline).
- **Evidence:** `clientlib-site.lc-305b3542667708578649e9eb3c6cb5ac-lc.min.css` resolves 200 on Publish (14,192 bytes) and contains all expected Style System selectors: `.cmp-teaser--hero` (13 occ), `.cmp-text--intro-lead` (4), `.cmp-container--card-grid` (5), `.cmp-site-header` (22), `.cmp-site-footer` (16), and even `.cmp-teaser--innovation-card` (9 occ — the rule exists; CQ-09 above is about it never being *applied* to any element, not about the CSS being missing).
- **Cause:** Cloud Manager's frontend build ran `ui.frontend` normally with real network/npm access, as expected — CQ-03's local-only Zscaler build-skip was correctly excluded from the PR per DECISIONS.md and never reached the real pipeline.
- **Recommended fix:** None — confirms no new blocking finding here.
- **Route:** n/a
- **Status:** confirmed-good
