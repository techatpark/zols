/*
 * Copyright 2014 sathish_ku.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zols.web.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.zols.linkmanager.domain.Link;
import org.zols.linkmanager.provider.LinkProvider;

@Component(value = "controlpanel")
public class ControlPanelLinkProvider implements LinkProvider {

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<Link>();
        Link link;

        link = new Link();
        link.setName("entities");
        link.setLabel("Entities");
        link.setTargetUrl("/entities");
        link.setIconUrl("/resources/css/images/icons/entities.svg");
        links.add(link);

        link = new Link();
        link.setName("templates");
        link.setLabel("Templates");
        link.setTargetUrl("/templates");
        link.setIconUrl("/resources/css/images/icons/template.svg");
        links.add(link);

        link = new Link();
        link.setName("templateStorages");
        link.setLabel("Template Storages");
        link.setTargetUrl("/templateStorages");
        link.setIconUrl("/resources/css/images/icons/template_storage.svg");
        links.add(link);

        link = new Link();
        link.setName("links");
        link.setLabel("Links");
        link.setTargetUrl("/links");
        link.setIconUrl("/resources/css/images/icons/links.svg");
        links.add(link);

        link = new Link();
        link.setName("languages");
        link.setLabel("Languages");
        link.setTargetUrl("/languages");
        link.setIconUrl("/resources/css/images/icons/language.svg");
        links.add(link);

        link = new Link();
        link.setName("documents");
        link.setLabel("Documents");
        link.setTargetUrl("/documents");
        link.setIconUrl("/resources/css/images/icons/documents.svg");
        links.add(link);

        link = new Link();
        link.setName("documentstorages");
        link.setLabel("Document Storages");
        link.setTargetUrl("/documentstorages");
        link.setIconUrl("/resources/css/images/icons/documentstroge.svg");
        links.add(link);

        link = new Link();
        link.setName("settings");
        link.setLabel("Settings");
        link.setTargetUrl("/settings");
        link.setIconUrl("/resources/css/images/icons/settings.svg");
        links.add(link);

        return links;
    }

}
