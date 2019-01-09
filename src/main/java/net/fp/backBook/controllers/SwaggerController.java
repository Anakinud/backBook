package net.fp.backBook.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SwaggerController {

    @GetMapping("/docs")
    public String redirectSwagger() {
        return "redirect:/swagger-ui.html";
    }
}