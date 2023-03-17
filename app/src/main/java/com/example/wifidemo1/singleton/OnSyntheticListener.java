package com.example.wifidemo1.singleton;

import com.example.wifidemo1.model.AlbumItem;

public interface OnSyntheticListener {

    void onSyntheticStart(AlbumItem albumItem);
    void onSyntheticFailed(String what, AlbumItem albumItem);
    void onSyntheticCompleted(AlbumItem albumItem);

}
