package com.example.sprintfirstprojectlw.models.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @GetMapping("/")
    public String getHome(@RequestParam(name="text", required = false, defaultValue = "Hello Spring!")String text, Model model){
        model.addAttribute("text", text);
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        return "about";
    }

    @GetMapping("/contacts")
    public String contacts(Model model){
        return "contacts";
    }

}
