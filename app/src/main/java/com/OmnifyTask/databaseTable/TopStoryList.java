package com.OmnifyTask.databaseTable;

import io.realm.RealmObject;

/**
 * Created by chavali on 2018-04-08.
 */

public class TopStoryList extends RealmObject {


    private long topstoryid;


    public long getTopstoryid() {
        return topstoryid;
    }

    public void setTopstoryid(long topstoryid) {
        this.topstoryid = topstoryid;
    }
}
