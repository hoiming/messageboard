package com.haiming.messageboard.bean;

import com.haiming.messageboard.annotation.Column;
import com.haiming.messageboard.annotation.Entity;

@Entity("user")
public class User {
	@Column("name")
	private String username;
	@Column("job")
	private String password;
	
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
