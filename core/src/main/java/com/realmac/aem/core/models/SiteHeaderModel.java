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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Sling Model for the {@code realmac/components/site-header} chrome component.
 *
 * <p>Renders the site logo, an embedded primary navigation, and an authorable list
 * of icon-only utility links.</p>
 *
 * <p>The embedded navigation is exposed as a ready-made {@link Resource} (see
 * {@link #getNavigationResource()}) whose {@link ValueMap} already carries
 * {@code navigationRoot} / {@code structureDepth} as if they were authored directly
 * on that resource, plus the {@code realmac/components/navigation} resource type. The
 * HTL therefore only needs a plain {@code data-sly-resource="${model.navigationResource}"}
 * reference — no custom {@code data-sly-resource} expression options are required, so
 * the htl-maven-plugin's {@code allowedExpressionOptions} validation is never
 * triggered for this line.</p>
 */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SiteHeaderModel {

    private static final String UTILITY_LINKS_MULTIFIELD_NAME = "utilityLinks";
    private static final String NAVIGATION_RESOURCE_TYPE = "realmac/components/navigation";

    @ValueMapValue
    private String logoFileReference;

    @ValueMapValue
    private String logoAlt;

    @ValueMapValue
    @Default(values = "/content/realmac/us/en.html")
    private String logoLinkURL;

    @ValueMapValue
    @Default(values = "/content/realmac/us/en")
    private String navigationRoot;

    @ValueMapValue
    @Default(intValues = 1)
    private int navigationStructureDepth;

    @SlingObject
    private Resource resource;

    private List<UtilityLink> utilityLinks;
    private Resource navigationResource;

    @PostConstruct
    protected void init() {
        utilityLinks = readUtilityLinks();
        navigationResource = buildNavigationResource();
    }

    /**
     * Builds a synthetic {@link Resource} for the embedded primary navigation. Its
     * {@link ValueMap} pre-populates {@code navigationRoot} and {@code structureDepth}
     * so that {@code realmac/components/navigation} (a proxy over Core Navigation)
     * resolves them exactly as if they had been authored on the resource, without
     * requiring any custom {@code data-sly-resource} expression option in HTL.
     */
    private Resource buildNavigationResource() {
        Map<String, Object> props = new HashMap<>();
        props.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, NAVIGATION_RESOURCE_TYPE);
        props.put("navigationRoot", navigationRoot);
        props.put("structureDepth", navigationStructureDepth);
        final ValueMap navigationValueMap = new ValueMapDecorator(props);

        return new ResourceWrapper(resource) {
            @Override
            public ValueMap getValueMap() {
                return navigationValueMap;
            }

            @Override
            public String getResourceType() {
                return NAVIGATION_RESOURCE_TYPE;
            }

            @Override
            public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
                if (type == ValueMap.class) {
                    return type.cast(navigationValueMap);
                }
                return super.adaptTo(type);
            }
        };
    }

    private List<UtilityLink> readUtilityLinks() {
        Resource parent = resource.getChild(UTILITY_LINKS_MULTIFIELD_NAME);
        if (parent == null) {
            return Collections.emptyList();
        }
        List<UtilityLink> items = new ArrayList<>();
        for (Resource item : parent.getChildren()) {
            UtilityLink link = item.adaptTo(UtilityLink.class);
            if (link != null) {
                items.add(link);
            }
        }
        return items;
    }

    public String getLogoFileReference() {
        return logoFileReference;
    }

    public String getLogoAlt() {
        return logoAlt;
    }

    public String getLogoLinkURL() {
        return logoLinkURL;
    }

    public String getNavigationRoot() {
        return navigationRoot;
    }

    public int getNavigationStructureDepth() {
        return navigationStructureDepth;
    }

    public List<UtilityLink> getUtilityLinks() {
        return utilityLinks;
    }

    public Resource getNavigationResource() {
        return navigationResource;
    }

    public boolean hasContent() {
        return logoFileReference != null;
    }

}
