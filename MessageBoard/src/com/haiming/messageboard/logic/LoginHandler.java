package com.haiming.messageboard.logic;

/**
 * 验证用户是否合法
 * @author Haiming-Liang
 *
 */
public interface LoginHandler {
	boolean verify(String username,String password);
}
