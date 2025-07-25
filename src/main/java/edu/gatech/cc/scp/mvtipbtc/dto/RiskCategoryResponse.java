package edu.gatech.cc.scp.mvtipbtc.dto;

import java.util.Map;

public class RiskCategoryResponse {
    private Map<String, Integer> categories;

    public Map<String, Integer> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Integer> categories) {
        this.categories = categories;
    }
}

