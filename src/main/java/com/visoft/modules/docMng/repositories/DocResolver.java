package com.visoft.modules.docMng.repositories;

import com.visoft.modules.user.User;
import com.visoft.modules.user.UserRepo;

/**
 * @author vlad
 *
 */
public final class DocResolver {

	public static User postedBy(final String id) {
		User user = null;
		if (id != null) {
			user = UserRepo.findById(id);
		}

		return user;
	}
}
