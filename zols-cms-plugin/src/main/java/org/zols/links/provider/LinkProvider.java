/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.links.provider;

import java.util.List;
import org.zols.links.domain.Link;

/**
 *
 * @author sathish_ku
 */
public interface LinkProvider {
    public List<Link> getLinks();
}
