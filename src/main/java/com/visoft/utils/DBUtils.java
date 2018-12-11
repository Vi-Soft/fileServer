package com.visoft.utils;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBUtils {

	public final static String DB_NAME = "fileServer";
	public final static MongoClient mongoClient = MongoClients.create();

	// create codec registry for POJOs
	public final static CodecRegistry pojoCodecRegistry = fromRegistries(
			MongoClientSettings.getDefaultCodecRegistry(),
			fromProviders(PojoCodecProvider.builder().automatic(true).build()));

	// get handle to database
	public final static MongoDatabase DB = mongoClient.getDatabase(DB_NAME)
			.withCodecRegistry(pojoCodecRegistry);

}
