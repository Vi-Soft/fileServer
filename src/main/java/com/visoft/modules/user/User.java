package com.visoft.modules.user;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import com.visoft.model.GenPojo;
import com.visoft.utils.ClassUtil;
import com.visoft.utils.Const;

/**
 * @author vlad
 * 
 *         <p>
 *         Instances of this class are immutable.
 *         </p>
 */
@BsonDiscriminator
public final class User extends GenPojo {

	private static final long serialVersionUID = 2063941600507172590L;

	private final String firstName;
	private final String middleName;
	private final String lastName;
	// User's credentials (email, password)
	private final ObjectId authEmail;
	private final List<Contact> contactLs;
	private final Role role;
	/**
	 * Copy constructor.
	 * 
	 * @param user
	 */
	public User(final User user) {
		this(user.getId(), user.getCreatedAt(), user.getNotBefore(),
				user.getExpiration(), user.isDeleted(),

				user.getFirstName(), user.getMiddleName(), user.getLastName(),
				user.getAuthEmail(), user.getContactLs(), user.getRole());
	}

	@BsonCreator
	public User(@BsonProperty(Const.ID) final ObjectId id,
			@BsonProperty("createdAt") final Instant createdAt,
			@BsonProperty("notBefore") final Instant notBefore,
			@BsonProperty("expiration") final Instant expiration,
			@BsonProperty("deleted") final boolean deleted,

			@BsonProperty("firstName") final String firstName,
			@BsonProperty("middleName") final String middleName,
			@BsonProperty("lastName") final String lastName,
			@BsonProperty("authEmail") final ObjectId authEmail,
			@BsonProperty("contactLs") final List<Contact> contactLs,
			@BsonProperty("role") final Role role) {

		super(new ObjectId(id.toString()), null, Instant.from(createdAt),
				Instant.from(notBefore), Instant.from(expiration), deleted);

		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.authEmail = new ObjectId(authEmail.toString());
		this.contactLs = Collections
				.unmodifiableList(new ArrayList<>(contactLs));
		this.role = role;
	}

	public User(@BsonProperty("firstName") final String firstName,
			@BsonProperty("middleName") final String middleName,
			@BsonProperty("lastName") final String lastName,
			@BsonProperty("authEmail") final ObjectId authEmail,
			@BsonProperty("role") final Role role) {
		super(null, Instant.now().plus(365, ChronoUnit.DAYS));
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.authEmail = new ObjectId(authEmail.toString());
		this.contactLs = Collections.unmodifiableList(new ArrayList<>());
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public User setFirstName(final String firstName) throws Exception {
		return ClassUtil.setter(this, "firstName", firstName);
	}

	public String getMiddleName() {
		return middleName;
	}

	public User setMiddleName(final String middleName) throws Exception {
		return ClassUtil.setter(this, "middleName", middleName);
	}

	public String getLastName() {
		return lastName;
	}

	public User setLastName(final String lastName) throws Exception {
		return ClassUtil.setter(this, "lastName", lastName);
	}

	public ObjectId getAuthEmail() {
		return authEmail;
	}

	public User setAuthEmail(final ObjectId authEmail) throws Exception {
		return ClassUtil.setter(this, "authEmail", authEmail);
	}

	public List<Contact> getContactLs() {
		return contactLs;
	}

	public User setContactLs(final List<Contact> contactLs) {
		final List<Contact> newContactLs = Collections
		.unmodifiableList(new ArrayList<>(contactLs));
		return ClassUtil.setter(this, "contactLs", newContactLs);
	}
	
	public Role getRole() {
		return role;
	}
	
	public User addContact(final Contact contact) throws Exception {
		final List<Contact> contacts = new ArrayList<>(this.getContactLs());
		contacts.add(contact);
		return setContactLs(contacts);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((authEmail == null) ? 0 : authEmail.hashCode());
		result = prime * result
				+ ((contactLs == null) ? 0 : contactLs.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((middleName == null) ? 0 : middleName.hashCode());
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
		User other = (User) obj;
		if (authEmail == null) {
			if (other.authEmail != null)
				return false;
		} else if (!authEmail.equals(other.authEmail))
			return false;
		if (contactLs == null) {
			if (other.contactLs != null)
				return false;
		} else if (!contactLs.equals(other.contactLs))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (middleName == null) {
			if (other.middleName != null)
				return false;
		} else if (!middleName.equals(other.middleName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", middleName=" + middleName
				+ ", lastName=" + lastName + ", authEmail=" + authEmail
				+ ", contactLs=" + contactLs + "]" + super.toString();
	}

}
