//package com.yao.ylist.service;
//
///**
// * <p>Copyright: Copyright (c) 2023-2028</p>
// * <p>Company: dcits</p>
// *
// * @description: 用户列表管理器，负责管理用户列表，包括用户的加载、保存、添加、删除和更新
// * @author: yaowei
// * @date: 2025/1/10 11:56
// * @version: V0.1
// */
//
//import javax.annotation.Resource;
//import java.io.*;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 用户列表管理器类
// * 该类提供了用户列表的基本操作，如加载用户列表、保存用户列表、添加用户、删除用户和更新用户
// */
//@Resource
//public class UserListManager {
//
//    // 用户列表文件路径
//    private String filePath;
//    // 用户缓存，用于存储从文件中加载的用户信息
//    private Map<String, String> userCache;
//
//    /**
//     * 构造函数，初始化UserListManager
//     *
//     * @param filePath 用户列表文件的路径
//     */
//    public UserListManager(String filePath) {
//        this.filePath = filePath;
//        this.userCache = new HashMap<>();
//        // 加载用户信息到缓存
//        loadUsers();
//    }
//
//    /**
//     * 从用户列表文件中加载用户信息
//     * 该方法会读取文件中的每一行，解析出用户名和密码，并存储到缓存中
//     */
//    private void loadUsers() {
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // 忽略空行和注释行
//                if (!line.trim().isEmpty() && !line.startsWith("#")) {
//                    // 解析用户名和密码
//                    String[] parts = line.split("=");
//                    if (parts.length == 2) {
//                        userCache.put(parts[0].trim(), parts[1].trim());
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取用户缓存
//     *
//     * @return 包含所有用户信息的Map，键为用户名，值为密码
//     */
//    public Map<String, String> getUserCache() {
//        return userCache;
//    }
//
//    /**
//     * 添加新用户
//     *
//     * @param username 用户名
//     * @param password 密码
//     */
//    public void addUser(String username, String password) {
//        userCache.put(username, password);
//        // 保存更新到文件
//        saveUsers();
//    }
//
//    /**
//     * 删除用户
//     *
//     * @param username 要删除的用户名
//     */
//    public void removeUser(String username) {
//        userCache.remove(username);
//        // 保存更新到文件
//        saveUsers();
//    }
//
//    /**
//     * 更新用户密码
//     *
//     * @param username 用户名
//     * @param newPassword 新密码
//     */
//    public void updateUser(String username, String newPassword) {
//        userCache.put(username, newPassword);
//        // 保存更新到文件
//        saveUsers();
//    }
//
//    /**
//     * 将用户信息保存到文件中
//     * 该方法会遍历用户缓存，将每个用户的信息写入到文件中
//     */
//    private void saveUsers() {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//            for (Map.Entry<String, String> entry : userCache.entrySet()) {
//                // 写入用户名和密码
//                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
