package com.rezeptapp.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.model.Ingredient;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/alexa")
@CrossOrigin(origins = "*")
public class AlexaController {

    private final RecipeManager recipeManager;

    @Autowired
    public AlexaController(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    
    @PostMapping
    public ResponseEntity<Map<String, Object>> handleAlexaRequest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = Map.of(
            "version", "1.0",
            "response", Map.of(
                "outputSpeech", Map.of(
                    "type", "PlainText",
                    "text", "Willkommen bei der Rezept-App! Sage mir die Rezeptnummer, die ich vorlesen soll."
                ),
                "shouldEndSession", false
            )
        );
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/recipe")
    public ResponseEntity<String> readRecipe(@RequestParam("id") int id) {
        try {
            Optional<Recipe> recipeOpt = recipeManager.getRecipeById(id);
            if (recipeOpt.isEmpty()) {
                return ResponseEntity.ok("Ich konnte das Rezept nicht finden.");
            }

            Recipe recipe = recipeOpt.get();
            StringBuilder sb = new StringBuilder();
            sb.append("Das Rezept für ").append(recipe.getTitle()).append(". ");

            if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                sb.append("Zutaten: ");
                for (Ingredient ing : recipe.getIngredients()) {
                    String amount = ing.getAmount() > 0 ? ing.getAmount() + " " : "";
                    String unit = (ing.getUnit() != null && !ing.getUnit().isEmpty()) ? ing.getUnit() + " " : "";
                    String name = (ing.getName() != null) ? ing.getName() : "";
                    sb.append(amount).append(unit).append(name).append(", ");
                }
            } else {
                sb.append("Keine Zutaten angegeben. ");
            }

            if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
                sb.append("Zubereitung: ").append(recipe.getInstructions());
            } else {
                sb.append("Leider keine Zubereitung gefunden.");
            }

            return ResponseEntity.ok(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Fehler beim Lesen des Rezepts.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
