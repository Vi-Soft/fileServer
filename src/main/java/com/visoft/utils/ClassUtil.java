package com.visoft.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import com.visoft.exceptions.UniversalSetterRuntimeException;

/**
 * @author vlad
 *
 */
public final class ClassUtil {

	private ClassUtil() {

	}

	/**
	 * @param targetInstance
	 * @param fieldName
	 * @param newValue
	 * @return
	 * @throws Exception
	 */
	public static <T> T setter(final T targetInstance, final String fieldName,
			final Object newValue) {

		try {
			final T newInstance = createCopy(targetInstance);

			final Field field = newInstance.getClass()
					.getDeclaredField(fieldName);
			field.setAccessible(true);
			final Field modifiersField = Field.class
					.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field,
					field.getModifiers() & ~Modifier.FINAL);

			field.set(newInstance, newValue);
			return newInstance;
		} catch (Exception e) {
			throw new UniversalSetterRuntimeException(e);
		}
	
	}

	/**
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public static <T> T createCopy(final T item) throws Exception {

		final Class<?> clazz = item.getClass();
		final Constructor<?> copyConstructor = clazz.getConstructor(clazz);

		@SuppressWarnings("unchecked")
		final T copy = (T) copyConstructor.newInstance(item);

		return copy;
	}
	
	/**
	 * @param stream
	 * @param element
	 * @return
	 */
	public static <T> Stream<T> appendToStream(Stream<? extends T> stream, T element) {
	    return Stream.concat(stream, Stream.of(element));
	}
}
