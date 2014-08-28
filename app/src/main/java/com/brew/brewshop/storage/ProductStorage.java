package com.brew.brewshop.storage;

import com.brew.brewshop.Recipe;

import java.util.ArrayList;
import java.util.List;

public class ProductStorage {

    public List<Product> getProducts(ProductType type) {
        List<Product> products = new ArrayList<Product>();
        for (int i = 0; i < 20; i++) {
            Product product = new Product();
            product.setProductType(type);
            product.setName(type.toString() + " " + (i+1));
            products.add(product);
        }
        return products;
    }
}
