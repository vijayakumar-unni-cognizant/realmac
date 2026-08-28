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
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Sling Model for the {@code realmac/components/site-footer} chrome component.
 *
 * <p>Renders an optional footer logo, N link columns, a social-icon row, and a
 * required legal/copyright line.</p>
 */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SiteFooterModel {

    private static final String COLUMNS_MULTIFIELD_NAME = "columns";
    private static final String SOCIAL_LINKS_MULTIFIELD_NAME = "socialLinks";

    @ValueMapValue
    private String footerLogoFileReference;

    @ValueMapValue
    private String legalText;

    @SlingObject
    private Resource resource;

    private List<FooterColumn> columns;
    private List<SocialLink> socialLinks;

    @PostConstruct
    protected void init() {
        columns = readColumns();
        socialLinks = readSocialLinks();
    }

    private List<FooterColumn> readColumns() {
        Resource parent = resource.getChild(COLUMNS_MULTIFIELD_NAME);
        if (parent == null) {
            return Collections.emptyList();
        }
        List<FooterColumn> items = new ArrayList<>();
        for (Resource item : parent.getChildren()) {
            FooterColumn column = item.adaptTo(FooterColumn.class);
            if (column != null) {
                items.add(column);
            }
        }
        return items;
    }

    private List<SocialLink> readSocialLinks() {
        Resource parent = resource.getChild(SOCIAL_LINKS_MULTIFIELD_NAME);
        if (parent == null) {
            return Collections.emptyList();
        }
        List<SocialLink> items = new ArrayList<>();
        for (Resource item : parent.getChildren()) {
            SocialLink link = item.adaptTo(SocialLink.class);
            if (link != null) {
                items.add(link);
            }
        }
        return items;
    }

    public String getFooterLogoFileReference() {
        return footerLogoFileReference;
    }

    public List<FooterColumn> getColumns() {
        return columns;
    }

    public List<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    public String getLegalText() {
        return legalText;
    }

    public boolean hasContent() {
        return legalText != null;
    }

}
