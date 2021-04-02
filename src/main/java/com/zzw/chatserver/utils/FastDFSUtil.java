package com.zzw.chatserver.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;


public class FastDFSUtil {
    private static StorageClient1 client1;

    static {
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            client1 = new StorageClient1(trackerServer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     */
    public static String uploadFile(MultipartFile file) throws IOException, MyException {
        String fileName = file.getOriginalFilename();
        //返回上传到服务器的路径
        return client1.upload_file1(file.getBytes(), fileName.substring(fileName.lastIndexOf(".") + 1), null);
    }

    /**
     * 下载文件
     */
    public static byte[] downloadFile(String fileId) throws IOException, MyException {
        return client1.download_file1(fileId);
    }

    public static String uploadFile(String localFilePath) throws IOException, MyException {
        return client1.upload_file1(localFilePath, localFilePath.substring(localFilePath.lastIndexOf(".") + 1), null);
    }

    /**
     * 获取访问文件的令牌，有全局异常处理
     */
    public static String getToken(String fileId) throws UnsupportedEncodingException, NoSuchAlgorithmException, MyException {
        int ts = (int) Instant.now().getEpochSecond();
        String subStr = fileId.substring(7);
        String token = ProtoCommon.getToken(subStr, ts, "FastDFS1234567890");
        StringBuilder sb = new StringBuilder();
        String IP = "http://服务器外网ip/";
        sb.append(IP);
        sb.append(fileId);
        sb.append("?token=").append(token);
        sb.append("&ts=").append(ts);
        return sb.toString();
    }
}
