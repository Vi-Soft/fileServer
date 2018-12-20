package com.visoft.file.service.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.visoft.file.service.service.util.PropertiesService;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DBConfig {

    private static String DB_NAME = PropertiesService.getDBName();
    private final static MongoClient mongoClient = MongoClients.create();

    private final static CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    public final static MongoDatabase DB = mongoClient.getDatabase(DB_NAME)
            .withCodecRegistry(pojoCodecRegistry);
}
