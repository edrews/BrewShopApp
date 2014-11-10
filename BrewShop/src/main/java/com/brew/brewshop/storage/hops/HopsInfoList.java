package com.brew.brewshop.storage.hops;

import com.brew.brewshop.storage.NameableList;

public class HopsInfoList extends NameableList<HopsInfo> {
    public HopsInfo findByName(String name) {
        for (HopsInfo info : this) {
            if (info.getName().equals(name)) {
                return info;
            }
        }
        return null;
    }
}
