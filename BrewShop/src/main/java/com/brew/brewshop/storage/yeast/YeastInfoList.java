package com.brew.brewshop.storage.yeast;

import com.brew.brewshop.storage.NameableList;

public class YeastInfoList extends NameableList<YeastInfo> {
    public YeastInfo findByName(String name) {
        for (YeastInfo info : this) {
            if (info.getName().equals(name)) {
                return info;
            }
        }
        return null;
    }
}
