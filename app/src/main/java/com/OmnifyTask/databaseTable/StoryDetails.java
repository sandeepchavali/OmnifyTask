package com.OmnifyTask.databaseTable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by chavali on 2018-04-08.
 */

public class StoryDetails extends RealmObject {

    private String by;
    private Long descendants;
    private Long id;
    private RealmList<Long> comments = null;
    private Long score;
    private Long time;
    private String title;
    private String type;
    private String url;

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Long getDescendants() {
        return descendants;
    }

    public void setDescendants(Long descendants) {
        this.descendants = descendants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RealmList<Long> getComments() {
        return comments;
    }

    public void setComments(RealmList<Long> comments) {
        this.comments = comments;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
