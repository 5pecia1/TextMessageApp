package com.example.contents;

/**
 * 전화번호부의 최소 단위
 * 
 * @author SOL
 *
 */
public class PhoneNumberContent {

	private final String name;
	private final String phoneNumber;
	private final String e_mail;

	private String type;

	public PhoneNumberContent(String name, String phoneNumber, String e_mail) {
		this.phoneNumber = phoneNumber;
		this.name = name;
		this.e_mail = e_mail;

		if(phoneNumber  != null)
			type = "휴대전화";
		else 
			type = "개인";

	}

	public String getName() {
		return name;
	}

	public String getInformation() {

		if(phoneNumber  != null)
			return phoneNumber;
		else if(e_mail  != null)
			return e_mail;

		return "";
	}

	public String getInformationType() {

		return type;
	}

	public boolean isMine(String number) {
		if(number.equals(phoneNumber)
				|| number.equals(e_mail)
				)
			return true;
		
		return false;
	}

}
