package com.rezeptapp.web;

import java.util.Optional;
import com.rezeptapp.data.api.Recipe;
import com.rezeptapp.data.api.RecipeManager;
import com.rezeptapp.data.api.UserManager;
import com.rezeptapp.data.implemented.UserImpl;
import com.rezeptapp.data.model.Ingredient;
import com.rezeptapp.web.api.MessageAnswer;
import com.rezeptapp.web.api.tokenAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.rezeptapp.data.implemented.EmailService;
import com.rezeptapp.data.implemented.RecipeImpl;
import java.util.List;
import com.rezeptapp.web.api.ShoppingListRequest;





@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecipeController {

    private final RecipeManager recipeManager;
    private final UserManager userManager;

    
    @Autowired
    public RecipeController(RecipeManager recipeManager, UserManager userManager) {
        this.recipeManager = recipeManager;
        this.userManager = userManager;
    }

    @GetMapping("/recipes/{id}") 
    public Recipe getRecipeDetails(@PathVariable int id) {
    Optional<Recipe> recipeOpt = recipeManager.getRecipeById(id);

    if (recipeOpt.isPresent()) {
        return recipeOpt.get(); 
    } 
    else 
        {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rezept nicht gefunden");
        }
    }


    @PostMapping("/shopping-list/send-email")
public ResponseEntity<MessageAnswer> sendShoppingListByEmail(@RequestBody ShoppingListRequest request) {


    String userEmail = userManager.getEmailFromToken(request.getToken());

    if (userEmail == null) {
        return new ResponseEntity<>(new MessageAnswer("Ungültiger Token oder Benutzer nicht eingeloggt."), HttpStatus.UNAUTHORIZED);
    }

    if (request.getShoppingListText() == null || request.getShoppingListText().trim().isEmpty()) {
         return new ResponseEntity<>(new MessageAnswer("Einkaufsliste ist leer oder enthält keinen Text."), HttpStatus.BAD_REQUEST);
    }

    
    try {
        EmailService emailService = new EmailService(); 
        String subject = "Deine Einkaufsliste";

        emailService.sendRecipeEmail(userEmail, subject, request.getShoppingListText()); 

        return new ResponseEntity<>(new MessageAnswer("Einkaufsliste erfolgreich an " + userEmail + " gesendet."), HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new MessageAnswer("E-Mail mit Einkaufsliste konnte nicht gesendet werden."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}











    @PostMapping("/recipes/{id}/send-email")
    public ResponseEntity<MessageAnswer> sendRecipeByEmail(
        @PathVariable int id,
        @RequestParam String token) { 

 
    String recipientEmail = userManager.getEmailFromToken(token);

    if (recipientEmail == null) {
        return new ResponseEntity<>(new MessageAnswer("Ungültiger Token oder Benutzer nicht eingeloggt."), HttpStatus.UNAUTHORIZED);
    }
    Optional<Recipe> recipeOpt = recipeManager.getRecipeById(id);

    if (recipeOpt.isPresent()) {
        Recipe recipe = recipeOpt.get();
        try {
            EmailService emailService = new EmailService(); 
            String emailSubject = "Rezept: " + recipe.getName();
            
           StringBuilder ingredientsText = new StringBuilder();
            if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) { 
                for (Ingredient ingredient : recipe.getIngredients()) {
                    String amountStr = ingredient.getAmount() > 0 ? String.valueOf(ingredient.getAmount()) : "";
                    String unitStr = (ingredient.getUnit() != null && !ingredient.getUnit().isEmpty()) ? ingredient.getUnit() : ""; 
                    String nameStr = ingredient.getName() != null ? ingredient.getName() : ""; 

                   
                    String line = "- ";
                    if (!amountStr.isEmpty()) {
                        line += amountStr + " ";
                    }
                    if (!unitStr.isEmpty()) {
                        line += unitStr + " ";
                    }
                    line += nameStr; 
                    ingredientsText.append(line.trim()).append("\n"); 
                }
                 if (ingredientsText.length() > 0) {
                    ingredientsText.setLength(ingredientsText.length() - 1);
                 }

            } else {
                ingredientsText.append("Keine Zutaten angegeben.");
            }
            String emailText = "Hallo!\n\nHier ist das Rezept für " + recipe.getName() + ":\n\n" +
                               "Zutaten:\n" + ingredientsText.toString() + "\n\n" + 
                               "Zubereitung:\n" + recipe.getInstructions() + "\n\n" +
                               "Viel Spaß beim Kochen!";

            emailService.sendRecipeEmail(recipientEmail, emailSubject, emailText);

            return new ResponseEntity<>(new MessageAnswer("Rezept erfolgreich an " + recipientEmail + " gesendet."), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new MessageAnswer("E-Mail konnte nicht gesendet werden."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } else {
        return new ResponseEntity<>(new MessageAnswer("Rezept nicht gefunden."), HttpStatus.NOT_FOUND);
    }
    }


    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes() {
        return recipeManager.getAllRecipes();
    }


    @PostMapping("/recipes")
public ResponseEntity<MessageAnswer> createRecipe(@RequestBody RecipeImpl recipe) {

    
    boolean success = recipeManager.addRecipe(recipe);

    if (success) {
        return new ResponseEntity<>(new MessageAnswer("Rezept erfolgreich erstellt."), HttpStatus.CREATED);
    } else {
        return new ResponseEntity<>(new MessageAnswer("Rezept konnte nicht gespeichert werden."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}





    


    @PostMapping("/register")
    public ResponseEntity<MessageAnswer> registerUser(@RequestBody UserImpl user) {
        boolean success = userManager.registerUser(user);
        if (success) {
            return new ResponseEntity<>(new MessageAnswer("Benutzer erfolgreich registriert."), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new MessageAnswer("Benutzer mit dieser E-Mail existiert bereits."), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<tokenAnswer> loginUser(@RequestBody UserImpl user) {
        String token = userManager.loginUser(user.getEmail(), user.getPassword());
        if (!token.equals("OFF")) {
            return new ResponseEntity<>(new tokenAnswer(token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}



    