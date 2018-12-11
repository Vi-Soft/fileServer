package com.visoft.types;

import com.visoft.modules.user.AuthDataRepo;

public class EmailAddressResolver {
	public static EmailAddress emailAddress(final String authEmail) {
		EmailAddress emailAddress = null;
		if (authEmail != null) {
			emailAddress = AuthDataRepo.findByEmail(authEmail).getEmail();
		}

		return emailAddress;
	}
}
