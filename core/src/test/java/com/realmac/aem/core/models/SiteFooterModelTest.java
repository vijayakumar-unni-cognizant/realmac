/*
 *  Copyright 2026 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.realmac.aem.core.models;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;
import com.realmac.aem.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SiteFooterModel} (discharges TC-038..TC-045 / US-009 for the
 * {@code site-footer} component).
 */
@ExtendWith(AemContextExtension.class)
class SiteFooterModelTest {

    private static final String COMPONENT_RESOURCE_TYPE = "realmac/components/site-footer";

    private final AemContext context = AppAemContext.newAemContext();

    private Page page;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(SiteFooterModel.class, FooterColumn.class, FooterLink.class, SocialLink.class);
        page = context.create().page("/content/realmac/us/en");
    }

    @Test
    void testWithCompleteData_populatesAllFields() {
        Resource resource = context.create().resource(page, "site-footer",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "footerLogoFileReference", "/content/dam/realmac/tata-logo-white.svg",
            "legalText", "© 2026 Realmac. All Rights Reserved.");

        Resource column0 = context.create().resource(resource, "columns/item0",
            "heading", "Business Verticals");
        context.create().resource(column0, "links/item0",
            "label", "Tata Chemicals",
            "url", "/content/realmac/us/en/innovation.html");
        context.create().resource(column0, "links/item1",
            "label", "Tata Steel Europe",
            "url", "/content/realmac/us/en/innovation.html");

        context.create().resource(resource, "columns/item1",
            "heading", "About");

        context.create().resource(resource, "socialLinks/item0",
            "iconFileReference", "/content/dam/realmac/fb.svg",
            "url", "https://facebook.com/realmac",
            "label", "Facebook");

        SiteFooterModel model = resource.adaptTo(SiteFooterModel.class);

        assertNotNull(model);
        assertEquals("/content/dam/realmac/tata-logo-white.svg", model.getFooterLogoFileReference());
        assertEquals("© 2026 Realmac. All Rights Reserved.", model.getLegalText());
        assertTrue(model.hasContent());

        assertEquals(2, model.getColumns().size());
        FooterColumn firstColumn = model.getColumns().get(0);
        assertEquals("Business Verticals", firstColumn.getHeading());
        assertTrue(firstColumn.hasContent());
        assertEquals(2, firstColumn.getLinks().size());
        assertEquals("Tata Chemicals", firstColumn.getLinks().get(0).getLabel());
        assertEquals("/content/realmac/us/en/innovation.html", firstColumn.getLinks().get(0).getUrl());
        assertTrue(firstColumn.getLinks().get(0).hasContent());

        FooterColumn secondColumn = model.getColumns().get(1);
        assertNotNull(secondColumn.getLinks());
        assertTrue(secondColumn.getLinks().isEmpty());

        assertEquals(1, model.getSocialLinks().size());
        SocialLink socialLink = model.getSocialLinks().get(0);
        assertEquals("/content/dam/realmac/fb.svg", socialLink.getIconFileReference());
        assertEquals("https://facebook.com/realmac", socialLink.getUrl());
        assertEquals("Facebook", socialLink.getLabel());
        assertTrue(socialLink.hasContent());
    }

    @Test
    void testWhenEmpty_hasContentFalseAndCollectionsEmpty() {
        Resource resource = context.create().resource(page, "site-footer",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE);

        SiteFooterModel model = resource.adaptTo(SiteFooterModel.class);

        assertNotNull(model);
        assertFalse(model.hasContent());
        assertNull(model.getFooterLogoFileReference());
        assertNotNull(model.getColumns());
        assertTrue(model.getColumns().isEmpty());
        assertNotNull(model.getSocialLinks());
        assertTrue(model.getSocialLinks().isEmpty());
    }

    @Test
    void testNewlyAuthored_multifieldNodesAbsent_returnsEmptyLists() {
        // Simulates a freshly-dropped component where the author has not yet opened
        // the Columns/Social tabs — no "columns"/"socialLinks" child nodes exist.
        Resource resource = context.create().resource(page, "site-footer",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "legalText", "© 2026 Realmac. All Rights Reserved.");

        SiteFooterModel model = resource.adaptTo(SiteFooterModel.class);

        assertNotNull(model);
        assertTrue(model.hasContent());
        assertNotNull(model.getColumns());
        assertTrue(model.getColumns().isEmpty());
        assertNotNull(model.getSocialLinks());
        assertTrue(model.getSocialLinks().isEmpty());
    }

    @Test
    void testFooterLogoAbsent_omittedNotBroken() {
        Resource resource = context.create().resource(page, "site-footer",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "legalText", "© 2026 Realmac. All Rights Reserved.");

        SiteFooterModel model = resource.adaptTo(SiteFooterModel.class);

        assertNull(model.getFooterLogoFileReference());
        assertTrue(model.hasContent());
    }

    @Test
    void testAdaptsFromBothRequestAndResource() {
        // TC-038 (US-009): validates the D1 array-form `adaptables = {SlingHttpServletRequest.class,
        // Resource.class}` — both adaptation paths must succeed and return a non-null, populated model.
        Resource resource = context.create().resource(page, "site-footer",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "legalText", "© 2026 Realmac. All Rights Reserved.");

        SiteFooterModel fromResource = resource.adaptTo(SiteFooterModel.class);
        assertNotNull(fromResource);
        assertTrue(fromResource.hasContent());

        context.currentResource(resource);
        SiteFooterModel fromRequest = context.request().adaptTo(SiteFooterModel.class);
        assertNotNull(fromRequest);
        assertTrue(fromRequest.hasContent());
        assertEquals("© 2026 Realmac. All Rights Reserved.", fromRequest.getLegalText());
    }

    @Test
    void testColumn_withoutLinksMultifield_returnsEmptyLinksList() {
        Resource resource = context.create().resource(page, "site-footer",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "legalText", "© 2026 Realmac. All Rights Reserved.");

        context.create().resource(resource, "columns/item0", "heading", "Newsroom");

        SiteFooterModel model = resource.adaptTo(SiteFooterModel.class);

        assertEquals(1, model.getColumns().size());
        assertNotNull(model.getColumns().get(0).getLinks());
        assertTrue(model.getColumns().get(0).getLinks().isEmpty());
    }

    @Test
    void testSocialLink_missingUrl_hasContentFalse() {
        Resource resource = context.create().resource(page, "site-footer",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "legalText", "© 2026 Realmac. All Rights Reserved.");

        context.create().resource(resource, "socialLinks/item0",
            "iconFileReference", "/content/dam/realmac/instagram.svg");

        SiteFooterModel model = resource.adaptTo(SiteFooterModel.class);

        assertEquals(1, model.getSocialLinks().size());
        assertFalse(model.getSocialLinks().get(0).hasContent());
    }

}
