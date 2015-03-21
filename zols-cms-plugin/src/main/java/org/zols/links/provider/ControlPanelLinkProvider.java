/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.provider;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.zols.links.domain.Link;

@Component(value = "controlPanel")
public class ControlPanelLinkProvider implements LinkProvider {

    private final List<Link> links;

    public ControlPanelLinkProvider() {
        this.links = new ArrayList<>();
        addDefaultLinks();
    }

    private void addDefaultLinks() {
        Link link;

        link = new Link();
        link.setName("schema");
        link.setLabel("Schema");
        link.setTargetUrl("/schema");
        links.add(link);

        link = new Link();
        link.setName("master");
        link.setLabel("Master");
        link.setTargetUrl("/master");
        links.add(link);

        link = new Link();
        link.setName("templates");
        link.setLabel("Templates");
        link.setTargetUrl("/templates");
        links.add(link);

        link = new Link();
        link.setName("links");
        link.setLabel("Links");
        link.setTargetUrl("/links");
        links.add(link);

        link = new Link();
        link.setName("documents");
        link.setLabel("Documents");
        link.setTargetUrl("/documents");
        links.add(link);

        link = new Link();
        link.setName("users");
        link.setLabel("Users");
        link.setTargetUrl("/users");
        links.add(link);

        link = new Link();
        link.setName("setting");
        link.setLabel("Settings");
        link.setTargetUrl("/setting");
        links.add(link);

    }

    public void addLink(Link link) {
        links.add(link);
    }

    @Override
    public List<Link> getLinks() {
        return links;
    }

}
