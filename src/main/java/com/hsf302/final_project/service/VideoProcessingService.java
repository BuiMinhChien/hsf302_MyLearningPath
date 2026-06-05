package com.hsf302.final_project.service;

import com.hsf302.final_project.dto.response.ProcessedVideoResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoProcessingService {
    private final S3Service s3Service;
    private final Path uploadDir = Paths.get("temp/uploads");
    private final Path hlsDir = Paths.get("temp/hls");

    public ProcessedVideoResult processVideo(MultipartFile videoFile) {
        try {
            Files.createDirectories(uploadDir);
            Files.createDirectories(hlsDir);
            String originalFilename = videoFile.getOriginalFilename();
            String extension = ".mp4";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String videoId = UUID.randomUUID().toString();
            String savedFilename = videoId + extension;
            Path uploadedVideoPath = uploadDir.resolve(savedFilename);
            Files.copy(
                    videoFile.getInputStream(),
                    uploadedVideoPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
            // ======================
            // HLS
            // ======================
            Path videoHlsDir = hlsDir.resolve(videoId);
            Files.createDirectories(videoHlsDir);
            Path outputPlaylist = videoHlsDir.resolve("index.m3u8");
            String ffmpegCommand = String.format(
                            "ffmpeg -i \"%s\" " +
                                    "-codec:v libx264 " +
                                    "-codec:a aac " +
                                    "-start_number 0 " +
                                    "-hls_time 5 " +
                                    "-hls_list_size 0 " +
                                    "-f hls \"%s\"",
                            uploadedVideoPath.toAbsolutePath(),
                            outputPlaylist.toAbsolutePath()
                    );
            ProcessBuilder pb;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", ffmpegCommand);
            } else {
                pb = new ProcessBuilder("bash", "-c", ffmpegCommand
                );
            }
            pb.redirectErrorStream(true);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg processing failed");
            }
            // ======================
            // Upload HLS folder
            // ======================
            uploadHlsFolder(
                    videoHlsDir,
                    videoId
            );
            String playlistUrl =
                    s3Service.getFileUrl(
                            "videos/hls/" +
                                    videoId +
                                    "/index.m3u8"
                    );
            // cleanup
            deleteDirectory(videoHlsDir);
            Files.deleteIfExists(uploadedVideoPath);
            return new ProcessedVideoResult(playlistUrl, videoId);
        } catch (Exception e) {
            throw new RuntimeException("Video processing failed", e);
        }
    }

    private void uploadHlsFolder(Path hlsFolder, String videoId) throws IOException {
        Files.walk(hlsFolder)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String key = "videos/hls/" + videoId + "/" + file.getFileName();
                        s3Service.uploadFile(
                                key,
                                file.toFile()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void deleteDirectory(Path path)
            throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
