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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Child model for a single {@code site-header} utility link (icon-only link with an
 * accessibility label), authored via the {@code utilityLinks} composite multifield.
 */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class UtilityLink {

    @ValueMapValue
    private String label;

    @ValueMapValue
    private String iconFileReference;

    @ValueMapValue
    private String linkURL;

    @ValueMapValue
    private String ariaLabel;

    public String getLabel() {
        return label;
    }

    public String getIconFileReference() {
        return iconFileReference;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public String getAriaLabel() {
        return ariaLabel;
    }

    public boolean hasContent() {
        return label != null && linkURL != null;
    }

}
