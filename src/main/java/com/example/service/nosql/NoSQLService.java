package com.example.service.nosql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by fy on 2017/12/31.
 */
public interface NoSQLService {
    void insertContent(String dbName, String collectionName, Object key, String content);
    String getContent(String dbName, String collectionName, Object key);
    void insertFile(String dbName, String bucketName, String fileName, InputStream inputStream) throws FileNotFoundException;
    InputStream getFileStream(String dbName, String bucketName, String fileName);
    void closeClient();
}
