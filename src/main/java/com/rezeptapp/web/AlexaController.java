package com.rezeptapp.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/alexa")
public class AlexaController {

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleAlexaRequest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = Map.of(
            "version", "1.0",
            "response", Map.of(
                "outputSpeech", Map.of(
                    "type", "PlainText",
                    "text", "Willkommen bei der Rezept-App! Sage mir den Rezeptnamen, den ich vorlesen soll."
                ),
                "shouldEndSession", false
            )
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recipe")
    public ResponseEntity<String> readRecipe(@RequestParam("id") int id) {
        String text = "Das Rezept für Spaghetti Bolognese. Du brauchst Hackfleisch, Tomaten und Nudeln. "
                    + "Die Zubereitung dauert 30 Minuten. Guten Appetit!";
        return ResponseEntity.ok(text);
    }
}
