package com.yao.ylist.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 文件工具类，提供文件相关操作的辅助方法
 */
public class FileUtils {

    /**
     * 使用Base64加密文件路径
     *
     * @param path 明文路径
     * @return 加密后的路径
     */
    public static String encrypt(String path) {
        return Base64.getEncoder().encodeToString(path.getBytes());
    }

    /**
     * 解密使用Base64加密的文件路径
     *
     * @param encryptedPath 加密后的路径
     * @return 明文路径
     */
    public static String decrypt(String encryptedPath) {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPath);
        return new String(decodedBytes);
    }

    /**
     * 尝试检测文件的字符集
     *
     * @param filePath 文件路径
     * @return 文件的字符集
     * @throws IOException 如果读取文件时发生错误
     */
    public static Charset detectCharset(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String firstLine = reader.readLine();
            if (firstLine != null) {
                // 这里简单地检查第一行是否包含常见的 UTF-8 字符
                // 实际应用中可以使用更复杂的检测方法
                if (isUtf8(firstLine)) {
                    return StandardCharsets.UTF_8;
                }
            }
        }
        // 如果无法确定编码，使用系统默认编码
        return Charset.defaultCharset();
    }

    /**
     * 检查给定的字符串是否包含非 ASCII 字符，用于判断是否为 UTF-8 编码
     *
     * @param line 待检查的字符串
     * @return 如果可能为 UTF-8 编码则返回 true，否则返回 false
     */
    public static boolean isUtf8(String line) {
        // 这里简单地检查是否包含常见的 UTF-8 字符
        // 实际应用中可以使用更复杂的检测方法
        return line.matches(".*[\\u0080-\\uFFFF].*");
    }

    /**
     * 验证给定的文件名是否有效
     *
     * @param filename 待验证的文件名
     * @return 如果文件名有效则返回 true，否则返回 false
     */
    public static boolean isValidFilename(String filename) {
        // 提取文件名部分
        File file = new File(filename);
        String fileNameOnly = file.getName();

        // 验证文件名是否符合预期格式，例如只允许字母、数字和某些特殊字符
        return fileNameOnly.matches("^[a-zA-Z0-9_.-]+\\.log$")
            && !fileNameOnly.contains("<")
            && !fileNameOnly.contains(">")
            && !fileNameOnly.contains(":")
            && !fileNameOnly.contains("\"")
            && !fileNameOnly.contains("|")
            && !fileNameOnly.contains("?")
            && !fileNameOnly.contains("*");
    }

    /**
     * 清理并验证路径，确保没有相对路径符号（如 ../）
     *
     * @param filename 待处理的文件名
     * @return 清理后的路径
     * @throws IllegalArgumentException 如果路径包含相对路径符号
     */
    public static Path sanitizePath(String filename) {
        // 清理路径，确保没有相对路径符号（如 ../）
        Path path = Paths.get(filename).normalize();
        if (path.toString().contains("..")) {
            throw new IllegalArgumentException("Invalid filename");
        }
        return path;
    }

}
