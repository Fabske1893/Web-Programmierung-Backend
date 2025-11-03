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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;





@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecipeAndUserController {

    private final RecipeManager recipeManager;
    private final UserManager userManager;

    @Autowired
    private EmailService emailService;  // Spring injiziert jetzt

    
    @Autowired
    public RecipeAndUserController(RecipeManager recipeManager, UserManager userManager) {
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
        return new ResponseEntity<>(new MessageAnswer("Ungültiger Token"), HttpStatus.UNAUTHORIZED);
    }
    
    Optional<Recipe> recipeOpt = recipeManager.getRecipeById(id);
    if (recipeOpt.isPresent()) {
        Recipe recipe = recipeOpt.get();
        
        // Async senden - Request blockiert nicht!
        emailService.sendRecipeEmailAsync(
            recipientEmail, 
            "Rezept: " + recipe.getTitle(), 
            buildEmailText(recipe)
        );
        
        // Sofortige Antwort an Client
        return new ResponseEntity<>(
            new MessageAnswer("E-Mail wird versendet..."), 
            HttpStatus.ACCEPTED  // 202 Accepted
        );
    }
    return new ResponseEntity<>(new MessageAnswer("Rezept nicht gefunden"), HttpStatus.NOT_FOUND);
}
   


    @GetMapping("/recipes")
    @Cacheable("recipes")
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
        try {
            // Validierung der Eingaben
            if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return new ResponseEntity<>(new MessageAnswer("Bitte fülle alle Felder korrekt aus."), HttpStatus.BAD_REQUEST);
            }
            
            boolean success = userManager.registerUser(user);
            if (success) {
                return new ResponseEntity<>(new MessageAnswer("Benutzer erfolgreich registriert."), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new MessageAnswer("Benutzer mit dieser E-Mail existiert bereits."), HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new MessageAnswer("Registrierung fehlgeschlagen: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<tokenAnswer> loginUser(@RequestBody UserImpl user) {
        try {
            // Validierung der Eingaben
            if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            String token = userManager.loginUser(user.getEmail(), user.getPassword());
            if (!token.equals("OFF")) {
                return new ResponseEntity<>(new tokenAnswer(token), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<MessageAnswer> deleteRecipe(@PathVariable int id) {
        boolean success = recipeManager.deleteRecipe(id);
        if (success) {
            return new ResponseEntity<>(new MessageAnswer("Rezept erfolgreich gelöscht."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new MessageAnswer("Rezept konnte nicht gelöscht werden."), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/account")
    public ResponseEntity<java.util.Map<String, Object>> getAccount(@RequestParam String token) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        try {
            java.util.Map<String, Object> user = userManager.getUserByToken(token);
            if (user != null) {
                result.put("username", user.get("username"));
                result.put("email", user.get("email"));
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                result.put("error", "User not found");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            result.put("error", "User not found");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}



