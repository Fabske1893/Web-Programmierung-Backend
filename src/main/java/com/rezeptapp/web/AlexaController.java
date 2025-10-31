package com.rezeptapp.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/alexa")
public class AlexaController {

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleAlexaRequest(@RequestBody Map<String, Object> request) {
        
        String requestType = ((Map<String, Object>) request.get("request")).get("type").toString();

        if ("LaunchRequest".equals(requestType)) {
            return ResponseEntity.ok(Map.of(
                "version", "1.0",
                "response", Map.of(
                    "outputSpeech", Map.of("type", "PlainText",
                        "text", "Willkommen bei der Rezept-App! Sage mir den Rezeptnamen, den ich vorlesen soll."),
                    "shouldEndSession", false
                )
            ));
        }

        if ("IntentRequest".equals(requestType)) {
            Map<String, Object> intent = (Map<String, Object>) ((Map<String, Object>) request.get("request")).get("intent");
            String intentName = intent.get("name").toString();

            if ("GetRecipeIntent".equals(intentName)) {
                Map<String, Object> slots = (Map<String, Object>) intent.get("slots");
                String recipeName = ((Map<String, Object>) slots.get("recipeName")).get("value").toString();

                String recipeText = "Das Rezept für " + recipeName + 
                    ". Du brauchst Hackfleisch, Tomaten und Nudeln. Die Zubereitung dauert 30 Minuten. Guten Appetit!";

                return ResponseEntity.ok(Map.of(
                    "version", "1.0",
                    "response", Map.of(
                        "outputSpeech", Map.of("type", "PlainText", "text", recipeText),
                        "shouldEndSession", true
                    )
                ));
            }
        }

        return ResponseEntity.ok(Map.of(
            "version", "1.0",
            "response", Map.of(
                "outputSpeech", Map.of("type", "PlainText", "text", "Ich habe das leider nicht verstanden."),
                "shouldEndSession", true
            )
        ));
    }

    // Test-Endpunkt für Browser (GET)
    @GetMapping("/recipe")
    public ResponseEntity<String> readRecipe(@RequestParam("id") int id) {
        String text = "Das Rezept für Spaghetti Bolognese. Du brauchst Hackfleisch, Tomaten und Nudeln. "
                    + "Die Zubereitung dauert 30 Minuten. Guten Appetit!";
        return ResponseEntity.ok(text);
    }
}
