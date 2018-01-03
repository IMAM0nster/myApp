package com.example.service.nosql.mongodb;

import com.example.service.nosql.NoSQLService;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by fy on 2017/12/31.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MongoDBService implements NoSQLService {

    protected static MongoClient mongoClient = null;

    public MongoDBService(){
        if(mongoClient == null){
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            builder.connectionsPerHost(50);
            builder.threadsAllowedToBlockForConnectionMultiplier(50);
            builder.maxWaitTime(1000*60*2);
            builder.connectTimeout(1000*60*1);
            MongoClientOptions myOptions = builder.build();
            try {
                mongoClient = new MongoClient("127.0.0.1",myOptions);

            }catch (MongoException e){
                e.printStackTrace();
            }
        }
    }

    public MongoDatabase getDatabase(String name) {
        if(mongoClient !=null)
            return mongoClient.getDatabase(name);
        else
            return null;
    }
    public MongoCollection<Document> getCollection(String dbName, String collectionName) {
        return this.getDatabase(dbName).getCollection(collectionName);
    }

    public void insertContent(String dbName, String collectionName, Object key, String content) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        Document document = new Document("key", key).append("content", content);
        if(collection.find(eq("key", key)).first()==null)
            collection.insertOne(document);
        else
            collection.replaceOne(eq("key", key), document);
    }



    public String getContent(String dbName, String collectionName, Object key) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        Document document = collection.find(eq("key", key)).first();
        if(document ==null)
            return null;
        else
            return document.getString("content");
    }

    public void insertFile(String dbName, String bucketName, String fileName, InputStream streamToUpLoadFrom) throws FileNotFoundException {
        MongoDatabase mongoDatabase = getDatabase(dbName);
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase, bucketName);
            GridFSFile gridFSFile = gridFSBucket.find(eq("filename", fileName)).first();
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(1024);
            if(gridFSFile!=null){ // if the file already exist, delete it
                ObjectId fileId = gridFSFile.getObjectId();
                gridFSBucket.delete(fileId);
            }
            gridFSBucket.uploadFromStream(fileName,streamToUpLoadFrom,options);

    }

    public InputStream getFileStream(String dbName, String bucketName, String fileName) {
        MongoDatabase mongoDatabase = getDatabase(dbName);
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase,bucketName);
        GridFSFile gridFSFile = gridFSBucket.find(eq("filename",fileName)).first();
        if(gridFSFile==null)
            return null;
        ObjectId objectId = gridFSFile.getObjectId();
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(objectId);
        return downloadStream;
    }

    @Override
    public void closeClient() {
        mongoClient.close();
        mongoClient=null;

    }
}
