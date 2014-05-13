package org.zols.linkmanager.provider;

import java.util.List;
import org.zols.linkmanager.domain.Link;

public interface LinkProvider {
    public List<Link> getLinks();
}