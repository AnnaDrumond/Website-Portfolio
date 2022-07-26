package pt.uc.dei.proj5.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import pt.uc.dei.proj5.entity.User.UserType;

@XmlRootElement
public class UserDto implements Serializable {
	private static final long serialVersionUID = 1L;
	// implements seriaziable sinaliza que pode mandar dados para ficheiros
	// com a anotaçao @XmlRootElement sinalizamos este pojo para transformar o tipo
	// de dados em xml ou json

	private String first_nameDto;
	private String last_nameDto;
	private String usernameDto;
	private String passwordDto;
	// private String tokenDto;
	private String emailDto;
	// private String registerDateDto;
	private String biographyDto;
	private String photo_userDto;
	private boolean registrationApprovedDto;
	private UserType typeUserDto;

	public UserDto() {
	}

	public UserDto(String first_nameDto, String last_nameDto, String usernameDto, String passwordDto, String emailDto,
			String biographyDto, String photo_userDto, boolean registrationApprovedDto, UserType typeUserDto) {
		this.first_nameDto = first_nameDto;
		this.last_nameDto = last_nameDto;
		this.usernameDto = usernameDto;
		this.passwordDto = passwordDto;
		this.emailDto = emailDto;
		this.biographyDto = biographyDto;
		this.photo_userDto = photo_userDto;
		this.registrationApprovedDto = registrationApprovedDto;
		this.typeUserDto = typeUserDto;
	}



	public String getFirst_nameDto() {
		return first_nameDto;
	}

	public void setFirst_nameDto(String first_nameDto) {
		this.first_nameDto = first_nameDto;
	}

	public String getLast_nameDto() {
		return last_nameDto;
	}

	public void setLast_nameDto(String last_nameDto) {
		this.last_nameDto = last_nameDto;
	}

	public String getUsernameDto() {
		return usernameDto;
	}

	public void setUsernameDto(String usernameDto) {
		this.usernameDto = usernameDto;
	}

	public String getPasswordDto() {
		return passwordDto;
	}

	public void setPasswordDto(String passwordDto) {
		this.passwordDto = passwordDto;
	}

	public String getEmailDto() {
		return emailDto;
	}

	public void setEmailDto(String emailDto) {
		this.emailDto = emailDto;
	}

	public String getBiographyDto() {
		return biographyDto;
	}

	public void setBiographyDto(String biographyDto) {
		this.biographyDto = biographyDto;
	}


	public boolean isRegistrationApprovedDto() {
		return registrationApprovedDto;
	}

	public void setRegistrationApprovedDto(boolean registrationApprovedDto) {
		this.registrationApprovedDto = registrationApprovedDto;
	}

	public UserType getTypeUserDto() {
		return typeUserDto;
	}

	public void setTypeUserDto(UserType typeUserDto) {
		this.typeUserDto = typeUserDto;
	}

	public String getPhoto_userDto() {
		return photo_userDto;
	}

	public void setPhoto_userDto(String photo_userDto) {
		this.photo_userDto = photo_userDto;
	}

}
