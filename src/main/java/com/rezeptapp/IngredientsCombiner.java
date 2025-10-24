package com.rezeptapp;

import java.sql.*;
import java.util.*;


public class IngredientsCombiner {

    private Connection conn;
    

    public IngredientsCombiner(Connection conn) {
        this.conn = conn;
    }

    public Set<String> getIngredientsForRecipe(int recipeId) throws SQLException {
        Set<String> ingredients = new HashSet<>();
        String sql = "SELECT amount, ingredient FROM recipe_ingredients WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(rs.getString("ingredient"));
                }
            }
        }
        return ingredients;
    }

    
    

}