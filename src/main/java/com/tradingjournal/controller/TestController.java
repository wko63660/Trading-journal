package com.tradingjournal.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Trading Journal API!";
    }
}
