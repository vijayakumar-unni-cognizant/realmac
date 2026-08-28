// site-header — minimal mobile menu-toggle behavior (no heavy hydration; NFR INP mitigation).
// See design/component-specifications.md § A.1 accessibility expectations.

(function() {
    "use strict";

    var selectors = {
        self:   '[data-cmp-is="site-header"]',
        toggle: '[data-cmp-hook-site-header="menuToggle"]',
        nav:    '[data-cmp-hook-site-header="nav"]'
    };

    function SiteHeader(config) {

        function toggleNav(toggle, nav) {
            var isOpen = nav.getAttribute("data-cmp-is-open") === "true";
            var nextState = !isOpen;

            nav.setAttribute("data-cmp-is-open", String(nextState));
            toggle.setAttribute("aria-expanded", String(nextState));
            toggle.setAttribute("aria-label", nextState ? "Close menu" : "Open menu");
        }

        function init(config) {
            config.element.removeAttribute("data-cmp-is");

            var toggle = config.element.querySelector(selectors.toggle);
            var nav = config.element.querySelector(selectors.nav);

            if (!toggle || !nav) {
                return;
            }

            toggle.addEventListener("click", function() {
                toggleNav(toggle, nav);
            });
        }

        if (config && config.element) {
            init(config);
        }
    }

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new SiteHeader({ element: elements[i] });
        }

        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body = document.querySelector("body");
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                new SiteHeader({ element: element });
                            });
                        }
                    });
                }
            });
        });

        observer.observe(body, {
            subtree: true,
            childList: true,
            characterData: true
        });
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

}());
