package com.rezeptapp.data.implemented;

import com.rezeptapp.data.api.Recipe;

public class RecipeImpl implements Recipe {
    private String name;
    private String pictureUrl;
    private String ingredients;
    private String instructions;
    private String difficultyLevel;
    private String category;


    @Override
    public String getName() { return this.name; }
    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getPictureUrl() { return this.pictureUrl; }
    @Override
    public void setPictureUrl(String url) { this.pictureUrl = url; }

    @Override
    public String getIngredients() { return this.ingredients; }
    @Override
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    @Override
    public String getInstructions() { return this.instructions; }
    @Override
    public void setInstructions(String instructions) { this.instructions = instructions; }

    @Override
    public String getDifficultyLevel() { return this.difficultyLevel; }
    @Override
    public void setDifficultyLevel(String level) { this.difficultyLevel = level; }

    @Override
    public String getCategory() { return this.category; }
    @Override
    public void setCategory(String category) { this.category = category; }
}