package com.rezeptapp.data.implemented;

import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.model.Ingredient;
import java.util.List;                     
import java.util.ArrayList;                  



public class RecipeImpl implements Recipe {
    private int id; 
    private String name;
    private String pictureUrl;
    private List<Ingredient> ingredients = new ArrayList<>();
    private String instructions;
    private String difficultyLevel;
    private String category;
    private int likes;


    @Override
    public String getName() { return this.name; }
    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getPictureUrl() { return this.pictureUrl; }
    @Override
    public void setPictureUrl(String url) { this.pictureUrl = url; }

    @Override
    public List<Ingredient> getIngredients() { return this.ingredients; }
    @Override
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

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
    @Override
    public int getId() { return this.id; }
    @Override
    public void setId(int id) { this.id = id; }
    @Override
    public int getLikes() { return this.likes; }
    @Override
    public void setLikes(int likes) { this.likes = likes; }
    
    
}