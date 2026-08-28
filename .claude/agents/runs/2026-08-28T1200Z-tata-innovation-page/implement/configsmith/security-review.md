# Security Review — `2026-08-28T1200Z-tata-innovation-page`

- **Agent:** configsmith
- **Scope reviewed:** (1) the 2 net-new chrome components `site-header` / `site-footer` as specified
  in `design/component-specifications.md` §A and `design/dialog-specifications.md` (Blockwright's
  parallel track had not yet landed `ui.apps`/`core` code at review time — see Caveat below), and
  (2) the policy changes authored by this agent (`policies/.content.xml`,
  `templates/landing-page/policies/.content.xml`).
- **Method:** `security-review` skill checklist — `loginAdministrative` usage, plaintext secrets in
  OSGi configs, overly broad ACLs, service users without a `ServiceUserMapper` entry, Sling Models
  adapting to `Session`/calling `.save()` from a request thread, dialog field XSS surface, wildcard
  policy allowlists.

## Caveat — component code not yet on disk

`ui.apps/src/main/content/jcr_root/apps/realmac/components/site-header` and `site-footer` do not
exist on disk as of this review (glob confirmed empty). This run dispatches Blockwright and
Configsmith in parallel with no ordering dependency (`dispatch/03b-configsmith.md` line 4). The
review below is therefore performed against the **approved design artifacts**
(`component-specifications.md` §A.1/A.2, `dialog-specifications.md`), which are the load-bearing
contract Blockwright must implement against. **Recommendation:** re-run this review (or have
Auditron re-verify) once Blockwright's actual `_cq_dialog`, HTL, and Sling Model files land, to
confirm the delivered code matches the reviewed design — do not treat this document as a substitute
for a code-level review of the final diff.

## Findings

### High severity
None.

### Medium severity
None.

### Low severity

| # | Finding | Location | Assessment |
|---|---|---|---|
| L1 | `logoLinkURL`, `navigationRoot`, and all `linkURL`/`url` fields across both dialogs use `granite/ui/components/coral/foundation/form/pathfield`, most scoped with `rootPath="/content/realmac"` or `rootPath="/content/dam/realmac"`. This is correct practice (author picks from a repository tree rather than free-typing an arbitrary URL) and constrains the authorable value space — no open-redirect or arbitrary-URL-injection surface. Recorded as an observation, not a defect. | `dialog-specifications.md` lines 74-101 | Pass — no action needed |
| L2 | `legalText` (site-footer) is a plain `textfield`, not an RTE. Good — it removes the largest stored-XSS surface for that component (no `richtext`/`@context=unsafe` HTL rendering implied). Confirmed no RTE authoring surface anywhere in either component's spec. | `dialog-specifications.md` line 104 | Pass — no action needed |
| L3 | Both components' `fileupload` fields set `allowUpload="{Boolean}false"`, forcing asset selection from the DAM rather than ad-hoc browser upload — consistent with the project's existing convention on `policy_651483963895698` ("Content Image"). | `dialog-specifications.md` lines 73, 103 | Pass — no action needed |
| L4 | Pre-existing, out-of-scope naming inconsistency: the `xf-web-variation` template's policy-mapping resourceType-fallback segment is named `<mysite>` rather than `<realmac>` (`templates/xf-web-variation/policies/.content.xml` line 12), unlike the equivalent segments in `page-content` and the new `landing-page` mapping (both correctly named `<realmac>`). This does not currently break anything (the header/footer XFs' only nested resource — the embedded Navigation inside `site-header` — is a synthetic `data-sly-resource` call, not an authorable child node, so it never needs policy resolution through that fallback segment). Per explicit instruction this run, **not remediated**. Flagging for Auditron/future-run awareness only. | `templates/xf-web-variation/policies/.content.xml:12` | Accepted, no fix this run (human-approved scope boundary) |

No `loginAdministrative` calls, no plaintext secrets, no `Session.save()` from a request-handler
Sling Model, and no service user without a mapper entry were found — because none of those patterns
appear anywhere in this run's scope (confirmed by design: both components are pure read-side Sling
Models over `@ValueMapValue`/`@ChildResource`, no write-back, no external integration).

## Policy allowlist audit (G — least privilege)

| Policy | `components=` value | Wildcard (`*`) used? |
|---|---|---|
| `container/policy_landing_content` | `[realmac/components/teaser,realmac/components/text,realmac/components/title,realmac/components/image,realmac/components/button,realmac/components/container]` | No |
| `container/policy_landing_card_grid` | `[realmac/components/teaser]` | No |
| `container/policy_1574694950110` (Page Root, reused/verified) | `[group:Realmac - Content,/apps/realmac/components/form/container,group:Realmac - Structure]` | No |
| `container/policy_1575040440977` (XF Root, amended) | `[group:Realmac - Content,/apps/realmac/components/form/container,group:Realmac - Structure]` | No |
| `teaser/policy_landing_hero_teaser` | N/A (`allowedTypes=[h1]` heading restriction only, no `components=`) | No |
| `teaser/policy_landing_card_teaser` | N/A (`allowedTypes=[h3]`) | No |
| `text/policy_landing_intro_text` | N/A (RTE config only) | No |
| `button/policy_landing_button` | N/A (minimal, defaults) | No |

Zero `*` (all-components) wildcards introduced or present in any policy touched by this run. The
`rtePlugins` `features="*"` values on `paraformat`/`lists` (cloned verbatim from the shared
`policy_641562756958017`) are RTE **toolbar-feature** flags, not component allowlists — out of scope
for the least-privilege gate, which governs `components=`/`allowedTypes=` allowlists.

## Reused (unmodified) policy verification — `policy-mapping.md §3`

All 7 reused policies were confirmed on disk at the documented paths, with the documented shape —
no discrepancies found, nothing escalated:

| Policy path | Verified shape matches doc? |
|---|---|
| `realmac/components/page/policy` | Yes |
| `realmac/components/container/policy_1574694950110` | Yes (already carries `group:Realmac - Structure` — pre-existing, unrelated to this run's XF Root amendment) |
| `realmac/components/experiencefragment/policy_header` | Yes |
| `realmac/components/experiencefragment/policy_footer` | Yes |
| `realmac/components/container/policy_649128221558427` | Yes |
| `realmac/components/title/policy_641528232375303` | Yes |
| `realmac/components/image/policy_651483963895698` | Yes |

## Service user / ACL / repoinit assumption — verified

No service user, ACL, or repoinit change was made or is needed. Confirmed by:
- Both new Sling Models (`SiteHeaderModel`, `SiteFooterModel`) are pure read-side, adapting from
  `SlingHttpServletRequest`/`Resource` via `@ValueMapValue`/`@ChildResource` only — no
  `ResourceResolverFactory.getServiceResourceResolver()`, no write-back, no external system call.
- The existing repoinit script (`org.apache.sling.jcr.repoinit.RepositoryInitializer~realmac.cfg.json`)
  only provisions the `/content/dam/realmac` path scaffold — unrelated to and unaffected by this run.
- No DAM-seeding or other backend integration was identified in `component-specifications.md` or
  `technical-specifications.md` that would need a dedicated service user this run.

## Dispatcher / CDN assumption — verified

No `dispatcher/` directory exists in this repository (confirmed by glob) and this run introduces a
standard, cacheable, published Sites page under `/content/realmac` with no new dynamic endpoint,
selector, or extension. No dispatcher/CDN change made or required.

## Gate status

- Zero high-severity findings: **pass**.
- Zero medium-severity findings: **pass**.
- 4 low-severity findings, all either "pass — no action needed" (L1-L3, positive design observations)
  or explicitly accepted per human-approved scope boundary (L4) — **no blocking findings**.
- Every policy in scope is an explicit allowlist, no `*`: **pass**.
- No `loginAdministrative` in diff: **pass** (none exists — no Java touched this run by Configsmith).
- Secrets externalized: **not applicable** (none introduced).
- **Does not block promotion.**
