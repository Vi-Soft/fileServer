package com.visoft.modules.user;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;
import static com.visoft.utils.ClassUtil.setter;
import static java.util.Collections.unmodifiableList;
import static com.visoft.utils.StringUtil.trim;

import java.util.Arrays;
import java.util.LinkedHashMap;
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
import com.visoft.exceptions.EmailRuntimeException;
import com.visoft.exceptions.UserRuntimeException;
import com.visoft.types.EmailAddress;
import com.visoft.types.PhoneNum;
import com.visoft.utils.Const;
import com.visoft.utils.DBUtils;

import graphql.GraphQLException;

/**
 * @author vlad
 *
 */
public final class UserRepo {

	private static final Logger log = LoggerFactory.getLogger(UserRepo.class);

	private static final MongoCollection<User> users = DBUtils.DB
			.getCollection(Const.USERS_MONGO_COLLECTION, User.class);

	public static final IndexOptions indexOptions = new IndexOptions()
			.unique(true);
	// email must be unique
	public static final String emailIndex = users
			.createIndex(Indexes.ascending("authEmail"), indexOptions);

	private UserRepo() {
		// The class does not instantiable.
	}

	/**
	 * @param authEmail
	 * @param firstName
	 * @param middleName
	 * @param lastname
	 * @return
	 */
	public static User createUser(final String authEmail,
			final String firstName, final String middleName,
			final String lastname, final String role) {

		final Optional<Role> maybeRole = Arrays.asList(Role.values()).stream()
				.filter(item -> item.name().equals(trim(role))).findFirst();

		final Role roleValue = maybeRole
				.orElseThrow(() -> new UserRuntimeException(
						"The role: " + role + " does not exist."));
		
		final AuthData authData = AuthDataRepo.findByEmail(authEmail);

		if (!authData.getEmail().isVerified()) {
			throw new EmailRuntimeException(
					"The authEmail: " + authEmail + " is not verified");
		}

		final User newUser = new User(firstName, middleName, lastname,
				authData.getId(), roleValue);
		return saveUser(newUser, authData);
	}

	/**
	 * @return List<User>
	 */
	public static List<User> getAllUsers() {

		final Bson bsonFilter = eq(Const.DELETED, false);

		final List<User> allUsers = StreamSupport
				.stream(users.find(bsonFilter).spliterator(), false)
				.collect(Collectors.toList());

		return unmodifiableList(allUsers);
	}

	/**
	 * @param id
	 * @return User
	 */
	public static User findById(final String id) {

		final Bson bsonFilter = eq(Const._ID, new ObjectId(id));
		final User user = Optional.ofNullable(users.find(bsonFilter).first())
				.orElseThrow(() -> new UserRuntimeException(
						"UserId: " + id + " does not exist."));
		log.info("UserRepo.findById result is: ", user);
		return user;
	}

	/**
	 * 
	 * Find user by AuthData email.
	 * 
	 * @param email
	 * @return User
	 */
	public static User findByAuthEmail(final String authEmail) {

		final AuthData authData = AuthDataRepo.findByEmail(authEmail);

		final Bson bsonFiter = eq(Const.AUTH_EMAIL, authData.getId());
		final User user = users.find(bsonFiter).first();

		return user;
	}

	/**
	 * 
	 * Find user by contact email.
	 * 
	 * @param email
	 * @return
	 */
	public static User findByEmail(final String email) {

		final Bson bsonFilter = eq("contactLs.email.emailAddr", email);
		final User user = users.find(bsonFilter).first();
		return user;
	}

	/**
	 * Delete an existing user.
	 * 
	 * @param id
	 * @return ModifiedCount must be 1L.
	 */
	public static long deleteUser(final String userId) {
		return setDeleted(userId, true);
	}

	/**
	 * Recovery deleted user.
	 * 
	 * @param id
	 * @return ModifiedCount must be 1L.
	 */
	public static long recoveryUser(final String userId) {
		return setDeleted(userId, false);
	}

	/**
	 * @param id
	 * @param emailMap
	 * @param phoneMap
	 * @return
	 */
	public static long addContact(final String userId,
			final Map<String, String> emailMap,
			final Map<String, String> phoneMap) {

		return addContact(userId, Optional.ofNullable(emailMap),
				Optional.ofNullable(phoneMap));

	}

	/**
	 * Get all user's contacts by userId.
	 * 
	 * @param userId
	 * @return
	 */
	public static List<Contact> getAllContactsByUserId(final String userId) {

		final ObjectId userIdObj = new ObjectId(userId);
		final Bson bsonFilter = eq(Const._ID, userIdObj);

		final List<Contact> contactLs = Optional
				.ofNullable(users.find(bsonFilter).first())
				.orElseThrow(() -> new UserRuntimeException(
						"No user found with ID: " + userId))
				.getContactLs().stream().collect(Collectors.toList());

		return unmodifiableList(contactLs);
	}

	/**
	 * Delete one user's contact by contact ID
	 * 
	 * @param contactId
	 * @return ModifiedCount must be 1L.
	 */
	public static long deleteContact(final String contactId) {

		final Bson bsonFilter = eq("contactLs._id", new ObjectId(contactId));
		final Optional<User> user = Optional
				.ofNullable(users.find(bsonFilter).first());
		final ObjectId contactIdObj = new ObjectId(contactId);

		final Contact contact = user
				.orElseThrow(() -> new UserRuntimeException(
						"No contact found with ID: " + contactId))
				.getContactLs().stream()
				.filter(item -> item.getId().equals(contactIdObj)).findFirst()
				.get();

		// delete the contact
		final UpdateResult updateResult = users.updateOne(bsonFilter,
				pull("contactLs", contact));

		return updateResult.getModifiedCount();
	}

	/**
	 * Change phone number in a user's contact, by contactId.
	 * 
	 * @param contactId
	 * @param phoneMap
	 * @return ModifiedCount must be 1L.
	 */
	public static long setContactPhone(final String contactId,
			final Map<String, String> phoneMap) {

		final String phoneNumber = phoneMap.get("phoneNumberStr");
		final String country = phoneMap.get("defaultCountry");

		final PhoneNum phoneNum = new PhoneNum(phoneNumber, country);

		final ObjectId contactIdObj = new ObjectId(contactId);
		final Bson bisonFilter = eq("contactLs._id", contactIdObj);

		// update DB field
		final UpdateResult updateResult = users.updateOne(bisonFilter,
				set("contactLs.$.phone", phoneNum));

		return updateResult.getModifiedCount();

	}

	/**
	 * Change email in a user's contact, by contactId.
	 * 
	 * @param contactId
	 * @param emailMap
	 * @return ModifiedCount must be 1L.
	 */
	public static long setContactEmail(final String contactId,
			final Map<String, String> emailMap) {

		final String emailAddr = emailMap.get("emailAddr");
		final String label = emailMap.get("label");

		final EmailAddress emailAddress = new EmailAddress(emailAddr, label);

		final ObjectId contactIdObj = new ObjectId(contactId);
		final Bson bisonFilter = eq("contactLs._id", contactIdObj);

		// update DB field
		UpdateResult updateResult = users.updateOne(bisonFilter,
				set("contactLs.$.email", emailAddress));

		return updateResult.getModifiedCount();

	}

	/**
	 * Set the DB field "defaultContact" to true in object with specified ID,
	 * all others set to false.
	 * 
	 * @param contactId
	 * @return
	 */
	public static long setDefaultContact(final String contactId) {

		final ObjectId contactIdObj = new ObjectId(contactId);
		final Bson bisonFilter = eq("contactLs._id", contactIdObj);
		final Optional<User> user = Optional
				.ofNullable(users.find(bisonFilter).first());

		final List<Contact> newContactLs = user
				.orElseThrow(() -> new UserRuntimeException(
						"No contact found with ID: " + contactId))
				.getContactLs().stream().map(item -> {
					if (item.getId().equals(contactIdObj)) {
						return setter(item, "defaultContact", true);
					} else {
						return setter(item, "defaultContact", false);
					}
				}).collect(Collectors.toList());

		// update DB field
		final UpdateResult updateResult = users.updateOne(bisonFilter,
				set("contactLs", unmodifiableList(newContactLs)));

		return updateResult.getModifiedCount();
	}

	/**
	 * @param authDataMap
	 * @return SigninPayload
	 */
	public static SigninPayload signinUser(
			final LinkedHashMap<String, String> authDataMap) {

		String authEmail = authDataMap.get(Const.AUTH_EMAIL);
		String passwd = authDataMap.get(Const.PASSWD);

		User user = findByAuthEmail(authEmail);
		// if (user.getPasswd().equals(passwd)) {
		// return new SigninPayload(user.getId().toString(), user);
		// }
		throw new GraphQLException("Invalid credentials");
	};

	/**
	 * @param env
	 * @return User
	 */
	public static User findByToken(final String token) {
		return findById(token);
	}

	private static User saveUser(final User user, final AuthData authData) {

		users.insertOne(user);

		// update authData expiration time to user expiration time
		AuthDataRepo.updateExpirationTime(authData.getEmail().getEmailAddr(),
				user.getExpiration());

		return user;
	}

	private static long setDeleted(final String id,
			final boolean deletedStatus) {

		final ObjectId contactIdObj = new ObjectId(id);
		final Bson bsonFilter = eq(Const._ID, contactIdObj);

		UpdateResult updateResult = users.updateOne(bsonFilter,
				set(Const.DELETED, deletedStatus));

		return updateResult.getModifiedCount();
	}

	private static long addContact(final String userId,
			final Optional<Map<String, String>> emailMap,
			final Optional<Map<String, String>> phoneMap) {

		EmailAddress emailAddress = null;
		if (emailMap.isPresent()) {
			final String emailAddr = emailMap.get().get("emailAddr");
			final String label = emailMap.get().get("label");

			emailAddress = new EmailAddress(emailAddr, label);
		}

		PhoneNum phoneNum = null;
		if (phoneMap.isPresent()) {
			final String phoneNumber = phoneMap.get().get("phoneNumberStr");
			final String country = phoneMap.get().get("defaultCountry");

			phoneNum = new PhoneNum(phoneNumber, country);
		}

		final Contact contact = new Contact(emailAddress, phoneNum, false);

		final ObjectId userIdObj = new ObjectId(userId);
		final Bson bsonFilter = eq(Const._ID, userIdObj);

		// add new contact by userId
		final UpdateResult updateResult = users.updateOne(bsonFilter,
				push("contactLs", contact));

		return updateResult.getModifiedCount();

	}

	private static Bson buildFilter(final UserFilter filter) {
		String namePattern = filter.getNameContains();
		String emailPattern = filter.getEmailContains();

		Bson nameCondition = null;
		Bson emailCondition = null;

		if (namePattern != null && !namePattern.isEmpty()) {
			nameCondition = regex(Const.FIRST_NAME, ".*" + namePattern + ".*",
					"i");
		}

		if (emailPattern != null && !emailPattern.isEmpty()) {

			emailCondition = regex(Const.EMAIL, ".*" + emailPattern + ".*",
					"i");
		}

		if (nameCondition != null && emailCondition != null) {
			return and(nameCondition, emailCondition);
		}

		return nameCondition != null ? nameCondition : emailCondition;
	}
}
