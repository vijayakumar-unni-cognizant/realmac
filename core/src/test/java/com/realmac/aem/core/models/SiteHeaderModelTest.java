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
import org.apache.sling.api.resource.ValueMap;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SiteHeaderModel} (discharges TC-038..TC-045 / US-009 for the
 * {@code site-header} component).
 */
@ExtendWith(AemContextExtension.class)
class SiteHeaderModelTest {

    private static final String COMPONENT_RESOURCE_TYPE = "realmac/components/site-header";

    private final AemContext context = AppAemContext.newAemContext();

    private Page page;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(SiteHeaderModel.class, UtilityLink.class);
        page = context.create().page("/content/realmac/us/en");
    }

    @Test
    void testWithCompleteData_populatesAllFields() {
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "logoFileReference", "/content/dam/realmac/tata-logo.svg",
            "logoAlt", "Realmac",
            "logoLinkURL", "/content/realmac/us/en.html",
            "navigationRoot", "/content/realmac/us/en",
            "navigationStructureDepth", 2);

        context.create().resource(resource, "utilityLinks/item0",
            "label", "Search",
            "iconFileReference", "/content/dam/realmac/search.svg",
            "linkURL", "/content/realmac/us/en/search.html",
            "ariaLabel", "Search");
        context.create().resource(resource, "utilityLinks/item1",
            "label", "Contact Us",
            "iconFileReference", "/content/dam/realmac/contact.svg",
            "linkURL", "/content/realmac/us/en/contact-us.html",
            "ariaLabel", "Contact Us");

        SiteHeaderModel model = resource.adaptTo(SiteHeaderModel.class);

        assertNotNull(model);
        assertEquals("/content/dam/realmac/tata-logo.svg", model.getLogoFileReference());
        assertEquals("Realmac", model.getLogoAlt());
        assertEquals("/content/realmac/us/en.html", model.getLogoLinkURL());
        assertEquals("/content/realmac/us/en", model.getNavigationRoot());
        assertEquals(2, model.getNavigationStructureDepth());
        assertTrue(model.hasContent());

        assertEquals(2, model.getUtilityLinks().size());
        UtilityLink first = model.getUtilityLinks().get(0);
        assertEquals("Search", first.getLabel());
        assertEquals("/content/dam/realmac/search.svg", first.getIconFileReference());
        assertEquals("/content/realmac/us/en/search.html", first.getLinkURL());
        assertEquals("Search", first.getAriaLabel());
        assertTrue(first.hasContent());
    }

    @Test
    void testWhenEmpty_hasContentFalseAndUtilityLinksEmpty() {
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE);

        SiteHeaderModel model = resource.adaptTo(SiteHeaderModel.class);

        assertNotNull(model);
        assertFalse(model.hasContent());
        assertNotNull(model.getUtilityLinks());
        assertTrue(model.getUtilityLinks().isEmpty());
    }

    @Test
    void testNewlyAuthored_multifieldNodeAbsent_returnsEmptyList() {
        // Simulates a freshly-dropped component where the author has not yet opened
        // the Utility Links tab of the dialog — no "utilityLinks" child node exists at all.
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "logoFileReference", "/content/dam/realmac/tata-logo.svg",
            "logoAlt", "Realmac");

        SiteHeaderModel model = resource.adaptTo(SiteHeaderModel.class);

        assertNotNull(model);
        assertNotNull(model.getUtilityLinks());
        assertTrue(model.getUtilityLinks().isEmpty());
        assertTrue(model.hasContent());
    }

    @Test
    void testDefaults_whenLinkURLAndNavigationPropertiesNotAuthored() {
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "logoFileReference", "/content/dam/realmac/tata-logo.svg",
            "logoAlt", "Realmac");

        SiteHeaderModel model = resource.adaptTo(SiteHeaderModel.class);

        assertNotNull(model);
        assertEquals("/content/realmac/us/en.html", model.getLogoLinkURL());
        assertEquals("/content/realmac/us/en", model.getNavigationRoot());
        assertEquals(1, model.getNavigationStructureDepth());
    }

    @Test
    void testWithPartialData_missingLogo_hasContentFalse() {
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "logoAlt", "Realmac");

        SiteHeaderModel model = resource.adaptTo(SiteHeaderModel.class);

        assertNotNull(model);
        assertFalse(model.hasContent());
        assertEquals("Realmac", model.getLogoAlt());
    }

    @Test
    void testAdaptsFromBothRequestAndResource() {
        // TC-038 (US-009): validates the D1 array-form `adaptables = {SlingHttpServletRequest.class,
        // Resource.class}` — both adaptation paths must succeed and return a non-null, populated model.
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "logoFileReference", "/content/dam/realmac/tata-logo.svg",
            "logoAlt", "Realmac");

        SiteHeaderModel fromResource = resource.adaptTo(SiteHeaderModel.class);
        assertNotNull(fromResource);
        assertTrue(fromResource.hasContent());

        context.currentResource(resource);
        SiteHeaderModel fromRequest = context.request().adaptTo(SiteHeaderModel.class);
        assertNotNull(fromRequest);
        assertTrue(fromRequest.hasContent());
        assertEquals("/content/dam/realmac/tata-logo.svg", fromRequest.getLogoFileReference());
    }

    @Test
    void testNavigationResource_carriesNavigationRootStructureDepthAndResourceType() {
        // CQ-02 remediation (Option B): the embedded navigation is exposed as a
        // ready-made Resource whose ValueMap already carries navigationRoot /
        // structureDepth plus the realmac/components/navigation resource type, so
        // the HTL needs no custom data-sly-resource expression options.
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "logoFileReference", "/content/dam/realmac/tata-logo.svg",
            "logoAlt", "Realmac",
            "navigationRoot", "/content/realmac/us/en",
            "navigationStructureDepth", 3);

        SiteHeaderModel model = resource.adaptTo(SiteHeaderModel.class);

        assertNotNull(model);
        Resource navigationResource = model.getNavigationResource();
        assertNotNull(navigationResource);
        assertEquals("realmac/components/navigation", navigationResource.getResourceType());

        ValueMap navigationValueMap = navigationResource.getValueMap();
        assertNotNull(navigationValueMap);
        assertEquals("/content/realmac/us/en", navigationValueMap.get("navigationRoot", String.class));
        assertEquals(3, navigationValueMap.get("structureDepth", Integer.class));
        assertEquals("realmac/components/navigation",
            navigationValueMap.get("sling:resourceType", String.class));

        ValueMap adaptedValueMap = navigationResource.adaptTo(ValueMap.class);
        assertNotNull(adaptedValueMap);
        assertEquals("/content/realmac/us/en", adaptedValueMap.get("navigationRoot", String.class));
    }

    @Test
    void testUtilityLink_withoutRequiredFields_hasContentFalse() {
        Resource resource = context.create().resource(page, "site-header",
            "sling:resourceType", COMPONENT_RESOURCE_TYPE,
            "logoFileReference", "/content/dam/realmac/tata-logo.svg",
            "logoAlt", "Realmac");

        context.create().resource(resource, "utilityLinks/item0",
            "iconFileReference", "/content/dam/realmac/search.svg");

        SiteHeaderModel model = resource.adaptTo(SiteHeaderModel.class);

        assertEquals(1, model.getUtilityLinks().size());
        assertFalse(model.getUtilityLinks().get(0).hasContent());
    }

}
