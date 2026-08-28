# Authoring Guidelines — Tata Innovation Landing Page

- **Run:** `2026-08-28T1200Z-tata-innovation-page`
- **Audience:** Composer (this run's seeding agent) and any future content author using
  `landing-page` for a similar page.

---

## 1. Creating the page

1. In the Sites console, create a new page under `/content/realmac/us/en` using the **Innovation
   Landing Page** template (`landing-page`). Verify it appears in the template picker per
   `template-design.md § cq:allowedTemplates registration` — it should already be covered by the
   existing permissive regex at `/content/realmac`.
2. Set the page title and, separately, author SEO meta (description) in Page Properties — the page
   has **no structural page-title component** (see §2), so the browser `<title>`/meta description is
   the only place page-level SEO text is authored.
3. Every intermediate path segment above the sample page must be a real `cq:Page` (not `nt:folder`) —
   for this run, `/content/realmac/us/en/innovation` — `us` and `en` already exist as `cq:Page`
   (verified on disk); only `innovation` is new.

## 2. Authoring depth — where components go

The template has exactly **one** editable content area. In the page editor, this is the empty area
between the header and footer chrome. Under the hood it is
`jcr:content/root/container/container` (two levels inside `root`) — do not expect a separate
"drop zone" for a page title; there isn't one (see `template-design.md § D22`).

Drag components into this one area in this order, top to bottom:

1. **Hero Teaser** (`Teaser` in the component browser, "Hero" Style System variant)
2. **Intro Text** (`Text`, "Intro Lead" Style System variant)
3. *(optional)* **Title** (h2, plain — for the "strategy" sub-heading, if the copy calls for one)
4. **Card Grid Container** (`Container`, "Card Grid" Style System variant) — then drop **4 Teaser
   instances** (no variant chosen at this level — the "Innovation Card" variant is auto-scoped, see
   below) inside it.

## 3. Hero (US-001)

- Drop a **Teaser** component into the top of the content area.
- Open its Style System (the paintbrush icon) and select **Hero**.
- In the Teaser dialog: set Title = `Innovation`; upload/select the hero image
  `about_innovation_banner_desktop_1920x1080.jpg` from the DAM. Do **not** add a CTA link/action — the
  Hero policy disables actions entirely (they will not render even if authored).
- No pretitle/description field is used for the hero (US-001: "Title only").

## 4. Intro/overview text (US-002)

- Drop a **Text** component below the hero.
- Open its Style System and select **Intro Lead**.
- Author the copy as a single rich-text block: the **first paragraph** is the lead — it renders larger
  automatically (no manual formatting needed). Subsequent paragraphs render as body copy.
- If a "strategy" sub-heading is wanted (reference Region 3, optional), drop a plain **Title**
  component either before or after the Text block and set it to `h2` (the default for this policy) —
  do not try to make it `h1`; that option is not offered by its policy.

## 5. Card grid + 4 showcase cards (US-003)

- Drop a **Container** below the intro. Open its Style System and select **Card Grid**.
- Inside that container, drop exactly 4 **Teaser** components (one per innovation centre). Each one's
  Style System offers only **Innovation Card** (this container's policy scopes teasers to that single
  variant) — select it.
- Per card: Title = centre name (e.g. "Tata Chemicals Innovation Centre"), Description = a short
  descriptor (optional), Image = the matching supplied card image, Action = one link (label + URL) —
  this renders as the arrow-style link; there is no filled-button option.
- The grid automatically arranges 2 columns at tablet/desktop and collapses to 1 column below 768px —
  no author action needed for responsive behavior.

## 6. Header (US-004) — authored once, in the Experience Fragment, not on the page

- The header is **not** authored on the landing page itself. It lives in the Experience Fragment at
  `/content/experience-fragments/realmac/us/en/site/header/master`, referenced by every page that
  uses `landing-page` (and `page-content`).
- Open that XF and replace its existing content with **one** `site-header` component instance:
  - Logo tab: upload/select `tata-logo.svg`; set alt text (e.g. "Realmac").
  - Navigation tab: set Navigation Root to the site's nav root (e.g. `/content/realmac/us/en`) and
    Depth to `1`.
  - Utility Links tab: add entries for Search (`search.svg`) and Contact (`ContactUs.svg`), each with
    a label, an icon, a link URL, and an accessibility label (required — these render as icon-only
    links).
- **Do not** re-add the master XF's previous Navigation/LanguageNavigation/Search components alongside
  `site-header` — per S8, the header is ONE cohesive authoring surface, not several stacked components.

## 7. Footer (US-005) — same pattern, in the footer master XF

- Open `/content/experience-fragments/realmac/us/en/site/footer/master` and replace its existing
  content (a Separator + a Text block) with **one** `site-footer` component instance:
  - Columns tab: add up to 4–5 link columns, each with a heading and a nested list of label+URL links.
  - Social tab: add entries for Facebook (`FB.svg`), LinkedIn (`Linkedin.svg`), Instagram
    (`Instagram.svg`), and Contact (`ContactUs.svg`) — each needs an icon, a URL, and a label (used as
    the accessibility label, e.g. "Facebook").
  - Legal tab: author a **neutral realmac copyright line** (e.g. "© 2026 Realmac. All rights
    reserved.") — do **not** copy Tata's copyright text (per S9.a — the reference is visual-only).
  - The footer logo field is optional; leave it empty unless a footer-specific (often inverted) logo
    asset is supplied.

## 8. What each component does NOT support (author-facing caveats)

| Component | Does not support |
|---|---|
| Hero Teaser | CTA/action links (disabled by policy); pretitle/description (not used in this layout) |
| Intro Text | Per-paragraph font-size override beyond "first paragraph = lead, rest = body" — the split is automatic and based on paragraph position, not manual styling |
| Card Grid Container | Any component other than Teaser (policy-restricted) |
| Card Teaser | More than one action link (only the first is styled as the arrow CTA); a "featured/filled button" look (none exists in this design) |
| `site-header` | More than one logo image; nested/multi-level navigation beyond the configured structure depth |
| `site-footer` | Tata's original copyright text (must be neutral realmac copy) |

## 9. Accessibility reminders for authors

- Always fill in **alt text** for the hero image and every card image (US-001/US-003, WCAG 2.1 AA).
- Always fill in the **accessibility label** field on every utility link and social link — these are
  icon-only links and are unusable to screen-reader users without it.
- Keep the hero title short — it must remain legible against the dark scrim at every breakpoint.
