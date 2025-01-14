package com.yao.ylist.controller;

import com.yao.ylist.service.LogService;
import com.yao.ylist.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private LogService logService;

    static final Set<String> previewExtensions = new HashSet<>(Arrays.asList("sh","txt", "log", "csv", "json", "xml", "html", "css", "js", "md", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "png", "jpg", "jpeg", "gif", "bmp", "tiff"));

    @GetMapping("/search")
    public String searchLogs(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTime,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime,
                             Model model) {
        // 根据 startTime 和 endTime 查询日志文件
        List<String> logFiles = logService.findLogFiles(startTime, endTime);
        model.addAttribute("originalLogFiles", logFiles);
        return "logPage/logList";
    }


    @GetMapping("/file-list")
    public ModelAndView browseFiles(@RequestParam(value = "path", required = false) String path) throws IOException {
        ModelAndView modelAndView = new ModelAndView("logPage/fileList");

        List<String> fileNames = logService.getFileList(path);

        Path rootPath = logService.getLogRootPath();
        // 获取当前路径
        Path currentPath = path != null ? Paths.get(path) : rootPath;

        // 获取父路径
        Path parentPath = currentPath.getParent();

        if (rootPath.equals(currentPath)) {
            parentPath = rootPath;
        }

        String parentPathStr = parentPath != null ? parentPath.toString() : rootPath.toString();

        // 区分文件和文件夹，并获取文件类型、权属和大小
        List<Map<String, Object>> fileList = new ArrayList<>();
        for (String fileName : fileNames) {
            Path filePath = currentPath.resolve(fileName);
            boolean isDirectory = Files.isDirectory(filePath);
            BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            String fileType = isDirectory ? "d" : "-";
            String owner = Files.getOwner(filePath).getName();
            long size = isDirectory ? 0 : attributes.size();
            String sizeStr = isDirectory ? "-" : formatSize(size);
            String extension = isDirectory ? "" : fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            String lastModifiedTime = formatDateTime(attributes.lastModifiedTime().toMillis());

            Map<String, Object> fileEntry = new HashMap<>();
            fileEntry.put("name", fileName);
            fileEntry.put("filepath", currentPath.toString() + "/" + fileName);
            fileEntry.put("isDirectory", isDirectory);
            fileEntry.put("fileType", fileType);
            fileEntry.put("owner", owner);
            fileEntry.put("size", sizeStr);
            fileEntry.put("extension", extension);
            fileEntry.put("lastModifiedTime", lastModifiedTime);

            fileList.add(fileEntry);
        }

        // 定义可预览文件扩展名列表
        modelAndView.addObject("previewExtensions", previewExtensions);

        modelAndView.addObject("fileList", fileList);
        modelAndView.addObject("currentPath", currentPath.toString());
        modelAndView.addObject("parentPath", parentPathStr);
        modelAndView.addObject("rootPath", rootPath.toString());

        return modelAndView;
    }

    private String formatSize(long size) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double formattedSize = size;
        while (formattedSize >= 1024 && unitIndex < units.length - 1) {
            formattedSize /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", formattedSize, units[unitIndex]);
    }

    private String formatDateTime(long millis) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).format(formatter);
    }

    @GetMapping("/viewLog")
    public String viewLog2(@RequestParam(value = "filename", required = false)  String filename, Model model) throws IOException {
        // 假设你有一个方法来获取文件的流
        logger.info("Viewing log file: {}", filename);
        String decryptedFilename = filename;
//        String decryptedFilename = FileUtils.decrypt(filename);
//        if (!isValidFilename(decryptedFilename)) {
//            logger.warn("Invalid filename: {}", decryptedFilename);
//        }
        try {
            Path sanitizedPath = FileUtils.sanitizePath(decryptedFilename);
            Resource file = logService.loadFileAsResource(sanitizedPath.toString());
            if (!file.exists() || !file.isReadable()) {
                logger.warn("File not found or not readable: {}", decryptedFilename);
            }
        } catch (Exception e) {
            logger.error("Unexpected error: {}", decryptedFilename, e);
        }

        // 你可以在这里进行分页处理，或者将流传递给前端
        filename = FileUtils.encrypt(filename);
        model.addAttribute("filename", filename);
        model.addAttribute("ognFilename", decryptedFilename);
        return "logPage/logViewer"; // 返回视图名称
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadLog2(@RequestParam(value = "filename", required = false) String filename) {
        logger.info("Downloading log file: {}", filename);
        String decryptedFilename = filename;
//        String decryptedFilename = FileUtils.decrypt(filename);
//        if (!isValidFilename(decryptedFilename)) {
//            logger.warn("Invalid filename: {}", decryptedFilename);
//            return ResponseEntity.badRequest().body("Invalid filename format. Please use a valid filename with the format [a-zA-Z0-9_.-]+.log");
//        }
        try {
            Resource file = logService.loadFileAsResource(FileUtils.sanitizePath(decryptedFilename).toString());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
        } catch (IOException e) {
            logger.error("Error downloading log file: {}", decryptedFilename, e);
            return ResponseEntity.status(500).body("Internal server error while downloading log file: " + decryptedFilename);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid file path: {}", decryptedFilename, e);
            return ResponseEntity.badRequest().body("Invalid file path: " + decryptedFilename);
        }
    }

    @GetMapping("/view")
    public ResponseEntity<String> viewLog2(
            @RequestParam(value = "filename", required = false)  String encryptedFilename,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5242880") int pageSize) throws IOException {

        String filename = FileUtils.decrypt(encryptedFilename);
        Path path = Paths.get( filename);
        List<String> logLines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                logLines.add(line);
            }
        }

        long currentSize = 0;
        int startIndex = 0;
        int endIndex = 0;

        for (int i = 0; i < logLines.size(); i++) {
            String line = logLines.get(i);
            long lineSize = line.getBytes().length + 1; // +1 for newline character

            if (page == 0 && currentSize + lineSize > pageSize) {
                endIndex = i;
                break;
            } else if (page > 0 && currentSize + lineSize > pageSize) {
                if (i - startIndex > 0) {
                    endIndex = i;
                    break;
                } else {
                    startIndex = i;
                    endIndex = i + 1;
                    break;
                }
            }

            currentSize += lineSize;
            endIndex = i + 1;
        }

        StringBuilder responseBuilder = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            responseBuilder.append(logLines.get(i)).append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBuilder.toString());
    }

}
