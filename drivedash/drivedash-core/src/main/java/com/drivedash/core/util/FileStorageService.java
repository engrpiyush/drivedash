package com.drivedash.core.util;

import com.drivedash.core.exception.DrivedashException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles file uploads to local storage (replaceable with an S3 adapter).
 * Mirrors Laravel's {@code Storage::put()} calls scattered across modules.
 */
@Service
public class FileStorageService {

    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    /**
     * Stores a {@link MultipartFile} under {@code subDirectory} and returns
     * the relative path (suitable for persisting in the database).
     *
     * @param file         the incoming multipart file
     * @param subDirectory e.g. "profile", "vehicle", "banner"
     * @return relative path like {@code "profile/uuid-filename.jpg"}
     */
    public String store(MultipartFile file, String subDirectory) {
        String originalFilename = StringUtils.cleanPath(
                java.util.Objects.requireNonNull(file.getOriginalFilename()));

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String storedFilename = UUID.randomUUID() + extension;
        Path targetDir = Paths.get(baseDir, subDirectory);

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(),
                    targetDir.resolve(storedFilename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw DrivedashException.internalError("Failed to store file: " + ex.getMessage());
        }

        return subDirectory + "/" + storedFilename;
    }

    /** Deletes a file given its relative path. Silently ignores missing files. */
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(baseDir, relativePath));
        } catch (IOException ex) {
            // Log but don't throw – deletion is best-effort
        }
    }
}
