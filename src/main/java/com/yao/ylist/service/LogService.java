package com.yao.ylist.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Copyright: Copyright (c) 2023-2028</p>
 * <p>Company: linuxido.com</p>
 *
 * @description: TODO
 * @author: yaowei
 * @date: 2024/12/30 10:10
 * @version: V0.1
 */
@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private String rootPath;

    public Path getLogRootPath() {
        if (rootPath == null || rootPath.isEmpty()) {
            // 使用用户的主目录作为默认路径
            return Paths.get(System.getProperty("user.home"));
        }
        // 返回第一个路径作为根路径
        return Paths.get(rootPath);
    }

    private final Path logDir = getLogRootPath();

    public List<String> listLogs() throws IOException {
        try {
            return Files.walk(logDir)
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error listing logs: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Resource loadFileAsResource(String filename) throws IOException {
        try {
            String decodedFilename = java.net.URLDecoder.decode(filename, "UTF-8");
            System.out.println("filename="+filename);
            Path filePath = logDir.resolve(decodedFilename).normalize();

            if (!filePath.startsWith(logDir)) {
                logger.warn("Invalid path traversal attempt: {}", filename);
                throw new SecurityException("Invalid path traversal attempt.");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                logger.warn("File not found: {}", filename);
                throw new FileNotFoundException("File not found " + filename);
            }
        } catch (IOException e) {
            logger.error("Error loading file as resource: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<String> findLogFiles(LocalDate startTime, LocalDate endTime) {
        List<String> allLogFiles = new ArrayList<>();
        try {
            allLogFiles = listLogs();
        } catch (IOException e) {
            throw new RuntimeException("Error listing log files", e);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startStr = startTime.format(formatter);
        String endStr = endTime.format(formatter);

        return allLogFiles.stream()
            .filter(filename -> {
                // 假设日志文件名包含日期，例如：app-2023-10-01.log
                String datePart = filename.split("-")[1].split("\\.")[0];
                return datePart.compareTo(startStr) >= 0 && datePart.compareTo(endStr) <= 0;
            })
            .collect(Collectors.toList());
    }

    public List<String> getFileList(String path) {
        Path logRootPath = getLogRootPath();
        Path currentPath = Paths.get(path != null ? path : logRootPath.toString()).normalize();

        // 验证路径是否在安全根目录内
        if (!currentPath.startsWith(logRootPath)) {
            throw new IllegalArgumentException("Invalid path");
        }

        File[] files = currentPath.toFile().listFiles();
        return files != null ? Arrays.stream(files)
            .map(File::getName)
            .collect(Collectors.toList()) : Collections.emptyList();
    }


}
