package com.visoft.utils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bson.types.ObjectId;

import com.mongodb.client.result.UpdateResult;
import com.visoft.types.PhoneNum;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * @author vlad
 *
 */
public class Scalars {

	public static GraphQLScalarType instant = new GraphQLScalarType("Instant",
			"Instant scalar", new Coercing() {

				@Override
				public String serialize(Object input) {
					// serialize the Instant into string on the way out
					return ((Instant) input).toString();
				}

				@Override
				public Object parseValue(Object input) {
					return serialize(input);
				}

				@Override
				public Instant parseLiteral(Object input) {
					// parse the string values coming in
					if (input instanceof StringValue) {
						return Instant.parse(((StringValue) input).getValue());
					} else {
						return null;
					}
				}
			});
	public static GraphQLScalarType dateTime = new GraphQLScalarType("DateTime",
			"DataTime scalar", new Coercing() {

				@Override
				public String serialize(Object input) {
					// serialize the ZonedDateTime into string on the way out
					return ((ZonedDateTime) input)
							.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
				}

				@Override
				public Object parseValue(Object input) {
					return serialize(input);
				}

				@Override
				public ZonedDateTime parseLiteral(Object input) {
					// parse the string values coming in
					if (input instanceof StringValue) {
						return ZonedDateTime
								.parse(((StringValue) input).getValue());
					} else {
						return null;
					}
				}
			});
	public static GraphQLScalarType objectId = new GraphQLScalarType("ObjectId",
			"ObjectId scalar", new Coercing() {

				@Override
				public String serialize(Object input) {
					// serialize the ObjectId into string on the way out
					return ((ObjectId) input).toString();
				}

				@Override
				public Object parseValue(Object input) {
					return serialize(input);
				}

				@Override
				public ObjectId parseLiteral(Object input) {
					// parse the string values coming in
					if (input instanceof StringValue) {
						return new ObjectId(((StringValue) input).toString());

					} else {
						return null;
					}
				}
			});

	// : UpdateResult
	public static GraphQLScalarType updateResult = new GraphQLScalarType(
			"UpdateResult", "UpdateResult scalar", new Coercing() {

				@Override
				public Object serialize(Object dataFetcherResult)
						throws CoercingSerializeException {
					return ((UpdateResult) dataFetcherResult).toString();
				}

				@Override
				public Object parseValue(Object input)
						throws CoercingParseValueException {
					return serialize(input);
				}

				@Override
				public Object parseLiteral(Object input)
						throws CoercingParseLiteralException {
					return (UpdateResult) input;
				}

			});
	
//	public static GraphQLScalarType phoneNum = new GraphQLScalarType(
//			"PhoneNum", "PhoneNum scalar", new Coercing() {
//
//				@Override
//				public Object serialize(Object dataFetcherResult)
//						throws CoercingSerializeException {
//					return ((PhoneNum) dataFetcherResult).toString();
//				}
//
//				@Override
//				public Object parseValue(Object input)
//						throws CoercingParseValueException {
//					return serialize(input);
//				}
//
//				@Override
//				public Object parseLiteral(Object input)
//						throws CoercingParseLiteralException {
//					return (PhoneNum) input;
//				}
//
//			});
}