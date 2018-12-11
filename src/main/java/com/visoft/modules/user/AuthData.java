package com.visoft.modules.user;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import com.visoft.model.GenPojo;
import com.visoft.types.EmailAddress;
import com.visoft.utils.Const;

/**
 * @author vlad
 *
 *         <p>
 *         Instances of this class are immutable.
 *         </p>
 */
public final class AuthData extends GenPojo implements Comparable<AuthData> {

	private static final long serialVersionUID = -3432076453595195253L;

	private final EmailAddress email;
	private final String passwd;

	/**
	 * Copy constructor.
	 * 
	 * @param authData
	 */
	public AuthData(final AuthData authData) {
		this(authData.getId(), authData.getCreatedAt(), authData.getNotBefore(),
				authData.getExpiration(), authData.isDeleted(),
				authData.getEmail(), authData.getPasswd());
	}

	/**
	 * @param id
	 * @param postedBy
	 * @param createdAt
	 * @param expiration
	 * @param notBefore
	 * @param deleted
	 * @param email
	 * @param passwd
	 */
	@BsonCreator
	public AuthData(@BsonProperty(Const.ID) final ObjectId id,
			@BsonProperty("createdAt") final Instant createdAt,
			@BsonProperty("notBefore") final Instant notBefore,
			@BsonProperty("expiration") final Instant expiration,
			@BsonProperty("deleted") final boolean deleted,

			@BsonProperty("email") final EmailAddress email,
			@BsonProperty("passwd") final String passwd) {

		super(id, null, createdAt, notBefore, expiration, deleted);

		this.email = email;
		this.passwd = passwd;
	}

	/**
	 * @param createdAt
	 * @param expiration
	 * @param notBefore
	 * @param email
	 * @param passwd
	 */
	public AuthData(@BsonProperty("createdAt") final Instant createdAt,
			@BsonProperty("notBefore") final Instant notBefore,
			@BsonProperty("expiration") final Instant expiration,

			@BsonProperty() final EmailAddress email,
			@BsonProperty() final String passwd) {
		this(ObjectId.get(), createdAt, notBefore, expiration, false, email,
				passwd);
	}

	/**
	 * Create not-deleted, active from now, immortal instance.
	 * 
	 * @param email
	 * @param passwd
	 */
	public AuthData(@BsonProperty(Const.EMAIL) final EmailAddress email,
			@BsonProperty(Const.PASSWD) final String passwd) {

		this(Instant.now(), Instant.now(),
				Instant.now().plus(7, ChronoUnit.DAYS), email, passwd);

	}

	public EmailAddress getEmail() {
		return email;
	}

	public String getPasswd() {
		return passwd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((passwd == null) ? 0 : passwd.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthData other = (AuthData) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (passwd == null) {
			if (other.passwd != null)
				return false;
		} else if (!passwd.equals(other.passwd))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthData [email=" + email + ", passwd=" + passwd + "]"
				+ super.toString();
	}

	@Override
	public int compareTo(AuthData authData) {
		if (authData != null)
			return this.getEmail().compareTo(authData.getEmail());
		return 1;
	}

}
