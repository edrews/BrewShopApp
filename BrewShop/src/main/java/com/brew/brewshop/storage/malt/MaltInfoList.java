package com.brew.brewshop.storage.malt;

import com.brew.brewshop.storage.NameableList;

public class MaltInfoList extends NameableList<MaltInfo> {
    public MaltInfo findByName(String name) {
        for (MaltInfo info : this) {
            if (info.getName().equals(name)) {
                return info;
            }
        }
        return null;
    }
}
