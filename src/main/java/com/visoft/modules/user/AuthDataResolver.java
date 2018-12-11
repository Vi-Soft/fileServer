package com.visoft.modules.user;

import org.bson.types.ObjectId;

/**
 * @author vlad
 *
 */
public class AuthDataResolver {
	
	public static AuthData authEmail(final ObjectId id) {

		return AuthDataRepo.findById(id);
	}
}
