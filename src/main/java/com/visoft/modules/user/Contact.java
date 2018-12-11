package com.visoft.modules.user;

import java.io.Serializable;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import com.visoft.types.EmailAddress;
import com.visoft.types.PhoneNum;
import com.visoft.utils.Const;

/**
 * @author vlad
 *         <p>
 *         Instances of this class are immutable.
 *         </p>
 */
public class Contact implements Serializable {

	private static final long serialVersionUID = -8646894403783447437L;

	private final ObjectId id;
	private final EmailAddress email;
	private final PhoneNum phone;
	private final boolean defaultContact;

	/**
	 * Copy constructor.
	 * 
	 * @param contact
	 */
	public Contact(Contact contact) {
		this(contact.getId(), contact.getEmail(), contact.getPhone(),
				contact.isDefaultContact());
	}

	/**
	 * BsonCreator constructor. Use for read from DB
	 * 
	 * @param id
	 * @param email
	 * @param phone
	 * @param defaultContact
	 */
	@BsonCreator
	public Contact(@BsonProperty(Const.ID) final ObjectId id,
			@BsonProperty("email") final EmailAddress email,
			@BsonProperty("phone") final PhoneNum phone,
			@BsonProperty("defaultContact") final boolean defaultContact) {

		this.id = id;
		this.email = email;
		this.phone = phone;
		this.defaultContact = defaultContact;
	}

	/**
	 * Use for put the new instance to DB
	 * 
	 * @param email
	 * @param phone
	 * @param defaultContact
	 */
	public Contact(@BsonProperty("email") final EmailAddress email,
			@BsonProperty("phone") final PhoneNum phone,
			@BsonProperty("defaultContact") final boolean defaultContact) {

		this(ObjectId.get(), email, phone, defaultContact);

	}

	public ObjectId getId() {
		return id;
	}

	public EmailAddress getEmail() {
		return email;
	}

	public PhoneNum getPhone() {
		return phone;
	}

	public boolean isDefaultContact() {
		return defaultContact;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (defaultContact ? 1231 : 1237);
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (defaultContact != other.defaultContact)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", email=" + email + ", phone=" + phone
				+ ", defaultContact=" + defaultContact + "]";
	}

}
