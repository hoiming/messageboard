package com.haiming.messageboard.dao;

import com.haiming.messageboard.bean.Page;

/**
 * 定义该有的接口
 * @author Haiming-Liang
 *
 */
public interface Dao<T> {
	//分页查询,返回下一页的内容
	Page getNextPage(int currPageIndex,String sql);
	T queryOneRecord(String sql);

}
