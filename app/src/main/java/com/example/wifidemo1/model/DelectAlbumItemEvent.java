package com.example.wifidemo1.model;

/**
 * Created by HLW on 2019/8/22.
 */

public class DelectAlbumItemEvent extends BaseModel {


    public AlbumItem getAlbumItem() {
        return albumItem;
    }

    private AlbumItem albumItem;
    public boolean deleteSucceed;

    public DelectAlbumItemEvent(AlbumItem albumItem, boolean deleteSucceed) {
        this.albumItem = albumItem;
        this.deleteSucceed = deleteSucceed;

    }

    @Override
    public String toString() {
        return " albumItem =" + albumItem;
    }
}
