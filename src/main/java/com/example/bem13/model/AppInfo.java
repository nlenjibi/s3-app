package com.example.bem13.model;

public record AppInfo(
    String hostname,
    String environment,
    String javaVersion,
    String author,
    String labName,
    String startTime
) {}
