package com.visoft.types;

import java.util.Locale;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.ShortNumberInfo;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

/**
 * @author vlad
 *
 */
public final class PhoneNumberTest {

	private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
	private final ShortNumberInfo shortInfo = ShortNumberInfo.getInstance();

	public static void main(String[] args) {
		PhoneNumberTest phoneNumber = new PhoneNumberTest();
		Locale locale = new Locale("EN", "US");
		try {
			phoneNumber.getOutputForSingleNumber("035652012",
					"IL", locale);
			// phoneNumber.getOutputForSingleNumber("+972774151218",
			// locale.getCountry(), locale);
			// phoneNumber.getOutputForSingleNumber("+972532247227",
			// locale.getCountry(), locale);
			// phoneNumber.getOutputForSingleNumber("+380978438186",
			// locale.getCountry(), locale);
		} catch (NumberParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The defaultCountry here is used for parsing phoneNumber. The
	 * geocodingLocale is used to specify the language used for displaying the
	 * area descriptions generated from phone number geocoding.
	 * 
	 * @throws NumberParseException
	 */
	private void getOutputForSingleNumber(String phoneNumber,
			String defaultCountry, Locale geocodingLocale)
			throws NumberParseException {
		PhoneNumber number = phoneUtil.parseAndKeepRawInput(phoneNumber,
				defaultCountry);

		System.out.println(
				"country_code: " + Integer.toString(number.getCountryCode()));
		System.out.println("national_number: "
				+ Long.toString(number.getNationalNumber()));
		System.out.println("extension: " + number.getExtension());
		System.out.println(
				"country_code_source: " + number.getCountryCodeSource());
		System.out.println("italian_leading_zero: "
				+ Boolean.toString(number.isItalianLeadingZero()));
		System.out.println("raw_input: " + number.getRawInput());

		boolean isPossible = phoneUtil.isPossibleNumber(number);
		boolean isNumberValid = phoneUtil.isValidNumber(number);
		PhoneNumberType numberType = phoneUtil.getNumberType(number);
		boolean hasDefaultCountry = !defaultCountry.isEmpty()
				&& defaultCountry != "ZZ";

		System.out.println("Result from isPossibleNumber(): "
				+ Boolean.toString(isPossible));
		if (!isPossible) {
			System.out.println("Result from isPossibleNumberWithReason(): "
					+ phoneUtil.isPossibleNumberWithReason(number));
		} else {
			System.out.println("Result from isValidNumber(): "
					+ Boolean.toString(isNumberValid));
			if (isNumberValid) {
				if (hasDefaultCountry) {
					System.out.println("Result from isValidNumberForRegion(): "
							+ Boolean.toString(phoneUtil.isValidNumberForRegion(
									number, defaultCountry)));
				}
			}
			String region = phoneUtil.getRegionCodeForNumber(number);
			System.out.println(
					"Phone Number region: " + region == null ? "" : region);
			System.out.println("Result from getNumberType(): " + numberType);
		}

		if (!isNumberValid) {
			boolean isPossibleShort = shortInfo.isPossibleShortNumber(number);
			System.out.println("Result from isPossibleShortNumber(): "
					+ Boolean.toString(isPossibleShort));
			if (isPossibleShort) {
				System.out.println(
						"Result from isValidShortNumber(): " + Boolean.toString(
								shortInfo.isValidShortNumber(number)));
				if (hasDefaultCountry) {
					boolean isPossibleShortForRegion = shortInfo
							.isPossibleShortNumberForRegion(number,
									defaultCountry);
					System.out.println(
							"Result from isPossibleShortNumberForRegion(): "
									+ Boolean.toString(
											isPossibleShortForRegion));
					if (isPossibleShortForRegion) {
						System.out.println(
								"Result from isValidShortNumberForRegion():  "
										+ Boolean.toString(shortInfo
												.isValidShortNumberForRegion(
														number,
														defaultCountry)));
					}
				}
			}
		}

		String isNumberValidStr = isNumberValid
				? phoneUtil.format(number, PhoneNumberFormat.E164)
				: "invalid";
		System.out.println("E164 format: " + isNumberValidStr);
		System.out.println("Original format: "
				+ phoneUtil.formatInOriginalFormat(number, defaultCountry));
		System.out.println("National format: "
				+ phoneUtil.format(number, PhoneNumberFormat.NATIONAL));
		isNumberValidStr = isNumberValid
				? phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL)
				: "invalid";
		System.out.println("International format: " + isNumberValidStr);
		isNumberValidStr = isNumberValid
				? phoneUtil.formatOutOfCountryCallingNumber(number, "US")
				: "invalid";
		System.out
				.println("Out-of-country format from US: " + isNumberValidStr);
		isNumberValidStr = isNumberValid
				? phoneUtil.formatOutOfCountryCallingNumber(number, "CH")
				: "invalid";
		System.out
				.println("Out-of-country format from CH: " + isNumberValidStr);

		AsYouTypeFormatter formatter = phoneUtil
				.getAsYouTypeFormatter(defaultCountry);
		int rawNumberLength = phoneNumber.length();
		for (int i = 0; i < rawNumberLength; i++) {
			// Note this doesn't handle supplementary characters, but it
			// shouldn't be a big deal as
			// there are no dial-pad characters in the supplementary range.
			char inputChar = phoneNumber.charAt(i);
			System.out.println("Char entered: '" + inputChar + "' Output: "
					+ formatter.inputDigit(inputChar));
		}

		if (isNumberValid) {
			System.out.println(
					"Location: " + PhoneNumberOfflineGeocoder.getInstance()
							.getDescriptionForNumber(number, geocodingLocale));

			System.out.println("Time zone(s): " + PhoneNumberToTimeZonesMapper
					.getInstance().getTimeZonesForNumber(number).toString());

			if (numberType == PhoneNumberType.MOBILE
					|| numberType == PhoneNumberType.FIXED_LINE_OR_MOBILE
					|| numberType == PhoneNumberType.PAGER) {
				System.out.println(
						"Carrier: " + PhoneNumberToCarrierMapper.getInstance()
								.getNameForNumber(number, geocodingLocale));
			}
		}

	}
}
