/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.cms.web;

import org.zols.cms.domain.NewsEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author sathish_ku
 */
public @RestController
class NewsController {

    Map<Long, NewsEntry> entries = new ConcurrentHashMap<Long, NewsEntry>();

    @RequestMapping("/news")
    @Secured("USER")
    public Collection<NewsEntry> entries() {
        return this.entries.values();
    }

    @RequestMapping(value = "/news/{id}", method = RequestMethod.DELETE)
    public NewsEntry remove(@PathVariable Long id) {
        return this.entries.remove(id);
    }

    @RequestMapping(value = "/news/{id}", method = RequestMethod.GET)
    public NewsEntry entry(@PathVariable Long id) {
        return this.entries.get(id);
    }

    @RequestMapping(value = "/news/{id}", method = RequestMethod.POST)
    public NewsEntry update(@RequestBody NewsEntry news) {
        this.entries.put(news.getId(), news);
        return news;
    }

    @RequestMapping(value = "/news", method = RequestMethod.POST)
    public NewsEntry add(@RequestBody NewsEntry news) {
        long id = 10 + new Random().nextInt(99);
        news.setId(id);
        this.entries.put(id, news);
        return news;
    }

    public NewsController() {
        for (long i = 0; i < 5; i++) {
            this.entries.put(i, new NewsEntry(i, "Title #" + i));
        }
    }

}
