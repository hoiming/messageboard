package com.haiming.messageboard.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.haiming.messageboard.annotation.Column;
import com.haiming.messageboard.annotation.Entity;
import com.haiming.messageboard.annotation.Id;
import com.haiming.messageboard.bean.Page;
import com.haiming.messageboard.exception.NotFoundAnnotationException;
import java.sql.Timestamp;

public class SqliteDaoImpl<T> implements Dao<T> {

	@Override
	public Page getNextPage(int currPageIndex, String sql) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T queryOneRecord(String sql, Class clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(T t) throws Exception {
		Class<?> clazz = t.getClass();
		// 获得表名

		String tableName = getTableName(clazz);
		// 获得字段
		StringBuilder fieldNames = new StringBuilder();
		List<Object> fieldValues = new ArrayList<Object>();
		StringBuilder placeholders = new StringBuilder();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			PropertyDescriptor pd = new PropertyDescriptor(field.getName(),
					t.getClass());
			if (field.isAnnotationPresent(Id.class)) {
				fieldNames.append(field.getAnnotation(Id.class).value())
						.append(",");
				// 读取该域的值
				fieldValues.add(pd.getReadMethod().invoke(t));
			} else if (field.isAnnotationPresent(Column.class)) {
				fieldNames.append(field.getAnnotation(Column.class).value())
						.append(",");
				fieldValues.add(pd.getReadMethod().invoke(t));
			}
			placeholders.append("?").append(",");
		}
		// 删除最后一个逗号
		fieldNames.deleteCharAt(fieldNames.length() - 1);
		placeholders.deleteCharAt(placeholders.length() - 1);

		// 拼接sql
		StringBuilder sql = new StringBuilder("");
		sql.append("insert into ").append(tableName).append(" (")
				.append(fieldNames.toString()).append(") values (")
				.append(placeholders.toString()).append(")");
		PreparedStatement ps = DaoUtils.getConnection().prepareStatement(
				sql.toString());
		setParaeter(fieldValues, ps);
		ps.execute();
		DaoUtils.close(ps, null);
		//之后加上Log日志记录，现在输出到控制台
		System.out.println(sql + "\n" +clazz.getSimpleName() + "添加成功");
	}

	/**
	 * 设置SQL占位符的值
	 * 
	 * @param fieldValues
	 * @param ps
	 * @throws SQLException
	 */
	private void setParaeter(List<Object> values, PreparedStatement ps)
			throws SQLException {
		for (int i = 1; i <= values.size(); i++) {
			Object fieldValue = values.get(i - 1);
			Class<?> clazzValue = fieldValue.getClass();
			if (clazzValue == String.class)
				ps.setString(i, (String) fieldValue);
			else if (clazzValue == boolean.class || clazzValue == Boolean.class)
				ps.setBoolean(i, (Boolean) fieldValue);
			else if (clazzValue == char.class || clazzValue == Character.class)
				ps.setObject(i, fieldValue, Types.CHAR);
			else if (clazzValue == Date.class)
				ps.setTimestamp(i, new Timestamp(((Date) fieldValue).getTime()));
			else
				ps.setObject(i, fieldValue, Types.NUMERIC);
		}

	}

	/**
	 * 通过注解获取表名
	 * 
	 * @param clazz
	 * @return
	 * @throws NotFoundAnnotationException
	 */

	private String getTableName(Class<?> clazz)
			throws NotFoundAnnotationException {
		if (clazz.isAnnotationPresent(Entity.class)) {
			Entity entity = clazz.getAnnotation(Entity.class);
			return entity.value();
		} else {
			throw new NotFoundAnnotationException(clazz.getName()
					+ " is not Entity Annotation.");
		}
	}

	@Override
	public void update(T t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<T> findAllByConditions(Map<String, Object> sqlWhere, Class clazz) {
		// TODO Auto-generated method stub
		return null;
	}

}
