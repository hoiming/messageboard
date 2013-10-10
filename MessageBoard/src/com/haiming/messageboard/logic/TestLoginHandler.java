package com.haiming.messageboard.logic;

/**
 * 无论输入什么，都返回真。让验证通过。
 * @author Haiming-Liang
 *
 */
public class TestLoginHandler implements LoginHandler{

	@Override
	public boolean verify(String username, String password) {
		return true;
	}

}
