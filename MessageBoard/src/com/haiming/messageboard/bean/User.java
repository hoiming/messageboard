package com.haiming.messageboard.bean;

import com.haiming.messageboard.annotation.Column;
import com.haiming.messageboard.annotation.Entity;
import com.haiming.messageboard.annotation.Id;

@Entity("user")
public class User {
	@Id("id")
	private int id;
	@Column("username")
	private String username;
	@Column("password")
	private String password;
	public User(){}
	public User(int id,String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}
	public User(String username,String password){ 
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String toString(){ 
		return "User: " + id + " " + username +" " + password;
	}

}
