package com.visoft.modules.user;

import org.bson.types.ObjectId;

/**
 * @author vlad
 *
 */
public class SigninPayload {

	private final ObjectId token;
	private final User user;

	public SigninPayload(final String token, final User user) {
		super();
		this.token = new ObjectId(token);
		this.user = user;
	}

	public ObjectId getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		SigninPayload other = (SigninPayload) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SigninPayload [token=" + token + ", user=" + user + "]";
	}

}
