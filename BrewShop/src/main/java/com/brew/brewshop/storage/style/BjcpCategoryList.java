package com.brew.brewshop.storage.style;

import com.brew.brewshop.storage.NameableList;

public class BjcpCategoryList extends NameableList<BjcpCategory> {
    public BjcpCategory findByName(String name) {
        for (BjcpCategory category : this) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    public BjcpCategory findByNumber(int number) {
        for (BjcpCategory category : this) {
            if (category.getId() == number) {
                return category;
            }
        }
        return null;
    }
}
