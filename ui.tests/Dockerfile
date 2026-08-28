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
# Version pin rule: this tag MUST match the @playwright/test version in package.json.
FROM mcr.microsoft.com/playwright:v1.49.1-jammy

ENV APP_PATH=/usr/src/app
WORKDIR ${APP_PATH}
COPY ./test-module ./
RUN npm install
# Browsers are preinstalled in the base image; headless — no Xvfb/X11 needed.
ENTRYPOINT ["bash","run.sh"]
