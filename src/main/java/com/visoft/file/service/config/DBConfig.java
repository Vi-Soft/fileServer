package com.visoft.file.service.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.visoft.file.service.service.util.PropertiesService.getDBName;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Configure class for mongo database
 */
public class DBConfig {

    /**
     * Get connection to mongo database
     */
    private final static MongoClient mongoClient = MongoClients.create();

    /**
     * Get instance mongo database
     */
    private final static CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(
                    PojoCodecProvider
                            .builder()
                            .automatic(true)
                            .build()
            )
    );

    /**
     * Set collection name and uniq indexes
     */
    public final static MongoDatabase DB = mongoClient
            .getDatabase(getDBName())
            .withCodecRegistry(pojoCodecRegistry);
}