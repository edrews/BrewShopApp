package com.brew.brewshop.storage;

import java.util.ArrayList;
import java.util.List;

public class StyleInfoList extends ArrayList<StyleInfo> {

    public StyleInfo findById(int id) {
        for(StyleInfo info : this) {
            if (info.getId() == id) {
                return info;
            }
        }
        return null;
    }
}
