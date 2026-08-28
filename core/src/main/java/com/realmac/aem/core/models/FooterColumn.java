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
 * Child model for a single {@code site-footer} link column, authored via the
 * {@code columns} composite multifield. Each column carries a heading and a nested
 * {@code links} composite multifield (0..n links).
 */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FooterColumn {

    private static final String LINKS_MULTIFIELD_NAME = "links";

    @ValueMapValue
    private String heading;

    @SlingObject
    private Resource resource;

    private List<FooterLink> links;

    @PostConstruct
    protected void init() {
        links = readLinks();
    }

    private List<FooterLink> readLinks() {
        Resource parent = resource.getChild(LINKS_MULTIFIELD_NAME);
        if (parent == null) {
            return Collections.emptyList();
        }
        List<FooterLink> items = new ArrayList<>();
        for (Resource item : parent.getChildren()) {
            FooterLink link = item.adaptTo(FooterLink.class);
            if (link != null) {
                items.add(link);
            }
        }
        return items;
    }

    public String getHeading() {
        return heading;
    }

    public List<FooterLink> getLinks() {
        return links;
    }

    public boolean hasContent() {
        return heading != null;
    }

}
