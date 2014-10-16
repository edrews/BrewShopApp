package com.brew.brewshop.storage.malt;

import com.brew.brewshop.storage.StyleInfo;

import java.util.ArrayList;

public class MaltInfoList extends ArrayList<MaltInfo> {

    public MaltInfo findById(int id) {
        for(MaltInfo info : this) {
            if (info.getId() == id) {
                return info;
            }
        }
        return null;
    }
}
