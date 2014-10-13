package com.brew.brewshop.storage;

import java.util.ArrayList;
import java.util.List;

public class StyleInfoList extends ArrayList<StyleInfo> {

    public int findIndexById(int id) {
        for(StyleInfo info : this) {
            if (info.getId() == id) {
                return this.indexOf(info);
            }
        }
        return -1;
    }
}
