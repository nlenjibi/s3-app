package com.bem12.app.photo;

import java.time.LocalDateTime;
import java.util.UUID;

public record PhotoView(
        UUID id,
        String imageUrl,
        String description,
        LocalDateTime createdAt
) {}
