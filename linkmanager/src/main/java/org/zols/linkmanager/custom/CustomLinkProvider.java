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

package org.zols.linkmanager.custom;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.zols.linkmanager.domain.Link;
import org.zols.linkmanager.provider.LinkProvider;

@Component(value = "custom")
public class CustomLinkProvider implements LinkProvider{

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<Link>(1);
        Link l = new Link();
        l.setName("Custom");
        l.setLabel("Custom");        
        links.add(l);
        return links;
    }
    
}
