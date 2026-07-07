package com.julio.bank.api.controller;

import com.julio.bank.api.service.ResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetController {

    private final ResetService resetService;

    public ResetController(ResetService resetService)
    {
        this.resetService = resetService;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset()
    {
        resetService.resetAll();
        return ResponseEntity.ok("OK");
    }
}
