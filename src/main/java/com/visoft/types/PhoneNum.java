package com.visoft.types;

import java.io.Serializable;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.visoft.exceptions.PhoneNumberRuntimeException;

/**
 * @author vlad
 *
 *         <p>
 *         Instances of this class are immutable.
 *         </p>
 */
@BsonDiscriminator
public final class PhoneNum implements Serializable {

	private static final long serialVersionUID = 578751680934855338L;

	private static final Logger log = LoggerFactory.getLogger(PhoneNum.class);

	private final String internationalFormat;
	private final String nationalFormat;
	private final String numberType;
	private final String countryCode;

	@BsonCreator
	public PhoneNum(
			@BsonProperty("internationalFormat") final String internationalFormat,
			@BsonProperty("nationalFormat") final String nationalFormat,
			@BsonProperty("numberType") final String numberType,
			@BsonProperty("countryCode") final String countryCode) {
		this.internationalFormat = internationalFormat;
		this.nationalFormat = nationalFormat;
		this.numberType = numberType;
		this.countryCode = countryCode;
	}

	public PhoneNum(@BsonProperty("phoneNumberStr") final String phoneNumberStr,
			@BsonProperty("defaultCountry") final String defaultCountry) {
		try {
			final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			final PhoneNumber number = phoneUtil
					.parseAndKeepRawInput(phoneNumberStr, defaultCountry);
			if (phoneUtil.isValidNumber(number)) {
				final PhoneNumberType numberType = phoneUtil
						.getNumberType(number);

				this.internationalFormat = phoneUtil.format(number,
						PhoneNumberFormat.INTERNATIONAL);
				this.nationalFormat = phoneUtil.format(number,
						PhoneNumberFormat.NATIONAL);
				this.numberType = numberType.toString();
				this.countryCode = phoneUtil.getRegionCodeForNumber(number);
				log.info("PhoneNum created: {}.", this);
			} else {
				log.error(
						"Error from PhoneNum constructor. Invalid phone number: {}.",
						phoneNumberStr);
				throw new PhoneNumberRuntimeException(
						"Invalid phone number: " + phoneNumberStr);
			}
		} catch (NumberParseException e) {
			log.error(
					"Error from PhoneNum constructor. Number format exception, number: {}.",
					phoneNumberStr);
			throw new PhoneNumberRuntimeException(
					"Number parse exception: " + phoneNumberStr, e);
		}
	}

	public String getInternationalFormat() {
		return internationalFormat;
	}

	public String getNationalFormat() {
		return nationalFormat;
	}

	public String getNumberType() {
		return numberType;
	}

	public String getCountryCode() {
		return countryCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((countryCode == null) ? 0 : countryCode.hashCode());
		result = prime * result + ((internationalFormat == null) ? 0
				: internationalFormat.hashCode());
		result = prime * result
				+ ((nationalFormat == null) ? 0 : nationalFormat.hashCode());
		result = prime * result
				+ ((numberType == null) ? 0 : numberType.hashCode());
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
		PhoneNum other = (PhoneNum) obj;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (internationalFormat == null) {
			if (other.internationalFormat != null)
				return false;
		} else if (!internationalFormat.equals(other.internationalFormat))
			return false;
		if (nationalFormat == null) {
			if (other.nationalFormat != null)
				return false;
		} else if (!nationalFormat.equals(other.nationalFormat))
			return false;
		if (numberType == null) {
			if (other.numberType != null)
				return false;
		} else if (!numberType.equals(other.numberType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PhoneNum [internationalFormat=" + internationalFormat
				+ ", nationalFormat=" + nationalFormat + ", numberType="
				+ numberType + ", countryCode=" + countryCode + "]";
	}

}
