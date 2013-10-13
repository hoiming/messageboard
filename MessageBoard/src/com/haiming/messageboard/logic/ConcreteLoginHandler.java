package com.haiming.messageboard.logic;

import com.haiming.messageboard.bean.User;
import com.haiming.messageboard.dao.Dao;
import com.haiming.messageboard.dao.SqliteDaoImpl;

public class ConcreteLoginHandler implements LoginHandler{
 
	private Dao<User> dao ;
	public ConcreteLoginHandler( ){ 
		this.dao = new SqliteDaoImpl();
	}
	/**
	 * 验证用户名密码是否一致
	 */
	@Override
	public boolean verify(String username, String password) {
		User user = new User(username,password);
		User dbUser = new User();
		try {
			dbUser = dao.getWithoutID(user, User.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (user.equals(dbUser))
			return true;
		return false;
	}

}
