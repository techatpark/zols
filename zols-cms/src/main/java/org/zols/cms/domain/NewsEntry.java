/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.cms.domain;

/**
 *
 * @author sathish_ku
 */
public class NewsEntry {

    private long id;
    private String content;

    public NewsEntry() {
    }

    public NewsEntry(long id, String b) {
        this.id = id;
        this.content = b;
    }

    public long getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
