#!/usr/bin/env bash

# Copyright 2026 Adobe Systems Incorporated
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Migrated from Cypress to Playwright — run 2026-08-28T1200Z-tata-innovation-page.
# Playwright runs headless in the mcr.microsoft.com/playwright base image — no Xvfb needed.

# setup proxy environment variables (Cloud Manager EaaS convention — preserved verbatim)
if [ -n "${PROXY_HOST:-}" ]; then
  if [ -n "${PROXY_HTTPS_PORT:-}" ]; then
    export HTTP_PROXY="https://${PROXY_HOST}:${PROXY_HTTPS_PORT}"
  elif [ -n "${PROXY_HTTP_PORT:-}" ]; then
    export HTTP_PROXY="http://${PROXY_HOST}:${PROXY_HTTP_PORT}"
  fi
  if [ -n "${PROXY_CA_PATH:-}" ]; then
    export NODE_EXTRA_CA_CERTS=${PROXY_CA_PATH}
  fi
  if [ -n "${PROXY_OBSERVABILITY_PORT:-}" ] && [ -n "${HTTP_PROXY:-}" ]; then
    echo "Waiting for proxy"
    curl --silent --retry "${PROXY_RETRY_ATTEMPTS:-3}" --retry-connrefused --retry-delay "${PROXY_RETRY_DELAY:-10}" \
      --proxy "${HTTP_PROXY}" --proxy-cacert "${PROXY_CA_PATH:-}" \
      "${PROXY_HOST}:${PROXY_OBSERVABILITY_PORT}"
    if [ $? -ne 0 ]; then
      echo "Proxy is not ready"
      exit 1
    fi
  fi
fi

# JUnit XML is written to $REPORTS_PATH per the Cloud Manager UI-testing contract.
npx playwright test
