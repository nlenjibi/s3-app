package com.bem12.app.photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class PhotoService {

    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);

    private static final long MAX_SIZE_BYTES = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png",  "png",
            "image/gif",  "gif",
            "image/webp", "webp");

    private final S3Client s3Client;
    private final PhotoRepository photoRepository;
    private final String bucketName;
    private final String cloudFrontDomain;

    public PhotoService(
            S3Client s3Client,
            PhotoRepository photoRepository,
            @Value("${photo.s3.bucket-name:}") String bucketName,
            @Value("${photo.cloudfront.domain:}") String cloudFrontDomain) {
        this.s3Client = s3Client;
        this.photoRepository = photoRepository;
        this.bucketName = bucketName;
        this.cloudFrontDomain = cloudFrontDomain;
    }

    public void upload(MultipartFile file, String description) {
        validate(file);
        String ext = EXTENSIONS.getOrDefault(file.getContentType(), "jpg");
        String key = "uploads/" + UUID.randomUUID() + "." + ext;
        putToS3(file, key);
        try {
            Photo photo = new Photo();
            photo.setImageKey(key);
            photo.setDescription(description == null ? "" : description.strip());
            photoRepository.save(photo);
        } catch (RuntimeException e) {
            deleteFromS3(key);
            throw e;
        }
        log.info("Photo saved: key={}", key);
    }

    public List<PhotoView> listAll() {
        return photoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(p -> new PhotoView(
                        p.getId(),
                        buildUrl(p.getImageKey()),
                        p.getDescription(),
                        p.getCreatedAt()))
                .toList();
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private void validate(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("File must not be empty");
        if (file.getSize() > MAX_SIZE_BYTES) throw new IllegalArgumentException("File exceeds 10 MB limit");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Unsupported file type — accepted: JPEG, PNG, GIF, WebP");
    }

    private void putToS3(MultipartFile file, String key) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            log.error("S3 upload failed for key={}: {}", key, e.getMessage());
            throw new RuntimeException("Upload failed. Please try again.");
        }
    }

    private void deleteFromS3(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (Exception e) {
            log.error("Failed to roll back orphaned S3 object key={}: {}", key, e.getMessage());
        }
    }

    private String buildUrl(String key) {
        if (cloudFrontDomain == null || cloudFrontDomain.isBlank()) {
            return key;
        }
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(cloudFrontDomain)
                .path("/")
                .path(key)
                .build()
                .toUriString();
    }
}
