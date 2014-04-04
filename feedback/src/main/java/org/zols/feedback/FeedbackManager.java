/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.feedback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.feedback.domain.FeedBack;
import org.zols.setting.SettingManager;

@Service
public class FeedbackManager {

    @Autowired
    private SettingManager settingManager;

    public FeedBack add(FeedBack feedBack) throws IOException {
        if (feedBack != null) {
            GitHub github = GitHub.connectUsingPassword(settingManager.get("GITHUB_USER").getValue(), settingManager.get("GITHUB_PASSWORD").getValue());
            GHRepository repository = github.getRepository(settingManager.get("GITHUB_REPO").getValue());
            GHIssue gHIssue = repository.createIssue(feedBack.getTitle()).body(feedBack.getDescription()).create();
            feedBack.setId(String.valueOf(gHIssue.getNumber()));
        }
        return feedBack;
    }

    public List<FeedBack> getFeedBacks() throws IOException {
        List<FeedBack> feedBacks = null;
        FeedBack feedBack;
        GitHub github = GitHub.connectUsingPassword(settingManager.get("GITHUB_USER").getValue(), settingManager.get("GITHUB_PASSWORD").getValue());
        GHRepository repository = github.getRepository(settingManager.get("GITHUB_REPO").getValue());
        List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN);
        if (issues != null) {
            feedBacks = new ArrayList<FeedBack>(issues.size());
            for (GHIssue gHIssue : issues) {
                feedBack = new FeedBack();
                feedBack.setId(String.valueOf(gHIssue.getNumber()));
                feedBack.setTitle(gHIssue.getTitle());
                feedBack.setDescription(gHIssue.getBody());
                feedBacks.add(feedBack);
            }
        }
        return feedBacks;
    }

}
