package com.rezeptapp.data.api;

public interface Recipe {
    String getName();
    void setName(String name);
    String getPictureUrl();
    void setPictureUrl(String url);
    String getIngredients();
    void setIngredients(String ingredients);
    String getInstructions();
    void setInstructions(String instructions);
    String getDifficultyLevel();
    void setDifficultyLevel(String level);
    String getCategory();
    void setCategory(String category);
    int getId();
    void setId(int id);
}