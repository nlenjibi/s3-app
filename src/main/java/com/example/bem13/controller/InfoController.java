package com.example.bem13.controller;

import com.example.bem13.model.AppInfo;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class InfoController {

    private static final Instant START_TIME = Instant.now();

    private final Environment environment;

    public InfoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/api/info")
    public AppInfo getInfo() {
        String[] profiles = environment.getActiveProfiles();
        String activeProfile = profiles.length > 0 ? profiles[0] : "dev";
        String hostname = System.getenv("HOSTNAME");

        return new AppInfo(
            hostname != null ? hostname : "localhost",
            activeProfile,
            System.getProperty("java.version"),
            "Timothy Nlenjibi",
            "BEM13 ECS CICD LAB",
            START_TIME.toString()
        );
    }
}
