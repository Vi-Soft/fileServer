package com.visoft.modules.user;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import static java.util.Collections.unmodifiableList;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import com.visoft.exceptions.AuthDataRuntimeException;
import com.visoft.exceptions.EmailRuntimeException;
import com.visoft.types.EmailAddress;
import com.visoft.utils.Const;
import com.visoft.utils.DBUtils;

/**
 * @author vlad
 *
 */
public final class AuthDataRepo {

	private static final Logger log = LoggerFactory
			.getLogger(AuthDataRepo.class);

	private static final MongoCollection<AuthData> authDataCollection = DBUtils.DB
			.getCollection(Const.AUTH_DATA, AuthData.class);

	public static final IndexOptions indexOptions = new IndexOptions()
			.unique(true);
	// email must be unique
	public static final String emailIndex = authDataCollection
			.createIndex(Indexes.ascending("email"), indexOptions);

	private AuthDataRepo() {

	}

	/**
	 * @param authEmailMap
	 * @param passwd
	 * @return
	 */
	public static AuthData createAuthData(
			final Map<String, String> authEmailMap, final String passwd) {

		final String emailAddr = authEmailMap.get("emailAddr");
		final String label = authEmailMap.get("label");

		final EmailAddress emailAddress = new EmailAddress(emailAddr, label);

		AuthData authData = new AuthData(emailAddress, passwd);
		authDataCollection.insertOne(authData);

		return authData;
	}

	/**
	 * @param id
	 * @return
	 */
	public static AuthData findById(final ObjectId id) {
		final AuthData authData = Optional
				.ofNullable(authDataCollection.find(eq(Const._ID, id)).first())
				.orElseThrow(
						() -> new AuthDataRuntimeException("AuthData with id: "
								+ id.toString() + " does not exist."));
		log.info("AuthDataRepo.findById result is: ", authData);
		return authData;
	}

	/**
	 * @param authEmail
	 * @return
	 */
	public static AuthData findByEmail(final String authEmail) {

		final Bson bsonFilter = eq("email.emailAddr", authEmail);

		final AuthData authData = Optional
				.ofNullable(authDataCollection.find(bsonFilter).first())
				.orElseThrow(() -> new EmailRuntimeException(
						"The authEmail " + authEmail + " does not exists"));

		log.info("AuthDataRepo.findByAuthEmail: authEmail is {}, result is {}",
				authEmail, authData);
		return authData;
	}

	/**
	 * @return AuthDate list
	 */
	public static List<AuthData> getAllAuthData() {

		final List<AuthData> authDataLs = StreamSupport
				.stream(authDataCollection.find().spliterator(), false)
				.collect(Collectors.toList());

		return unmodifiableList(authDataLs);
	}

	/**
	 * @param authEmail
	 * @return ModifiedCount must be 1L.
	 */
	public static long emailVerify(final String authEmailId) {

		final ObjectId authEmailIdObj = new ObjectId(authEmailId);
		final Bson bsonFilter = eq(Const._ID, authEmailIdObj);
		
		// update email.verified 
		final UpdateResult updateResult = authDataCollection
				.updateOne(bsonFilter, set("email.verified", true));

		return updateResult.getModifiedCount();
	}

	/**
	 * <p>
	 * Update authData expiration time.
	 * </p>
	 * 
	 * @param email
	 * @param expiration
	 * @return
	 */
	public static Long updateExpirationTime(final String email,
			final Instant expiration) {

		// update authData expiration time
		UpdateResult updateResult = authDataCollection.updateOne(
				eq("email.emailAddr", email), set("expiration", expiration));

		// find updated authData
		return updateResult.getModifiedCount();
	}
}
