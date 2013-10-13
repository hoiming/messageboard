package com.haiming.messageboard.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.haiming.messageboard.annotation.Column;
import com.haiming.messageboard.annotation.Entity;
import com.haiming.messageboard.annotation.Id;
import com.haiming.messageboard.bean.Page;
import com.haiming.messageboard.exception.NotFoundAnnotationException;

public class SqliteDaoImpl<T> implements Dao<T> {

	// 别名
	private static final String TABLE_ALIAS = "t";

	@Override
	public Page getNextPage(int currPageIndex, String sql) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get(Object id, Class<T> clazz) throws NotFoundAnnotationException,
			InstantiationException, IllegalAccessException, SQLException,
			IllegalArgumentException, InvocationTargetException,
			IntrospectionException {
		String idFieldName = "";
		Field[] fields = clazz.getDeclaredFields();
		boolean flag = false;
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				idFieldName = field.getAnnotation(Id.class).value();
				flag = true;
				break;
			}
		}
		if (!flag) {
			throw new NotFoundAnnotationException(clazz.getName()
					+ " object not found id property.");
		}
		// 拼装SQL
		Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
		sqlWhereMap.put(TABLE_ALIAS + "." + idFieldName, id);
		List<T> list = findAllByConditions(sqlWhereMap, clazz);
		return list.size() > 0 ? list.get(0) : null;
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
		setParameter(fieldValues, ps);
		ps.execute();
		DaoUtils.close(ps, null);
		// 之后加上Log日志记录，现在输出到控制台
		System.out.println(sql + "\n" + clazz.getSimpleName() + "添加成功");
	}

	/**
	 * 设置SQL占位符的值
	 * 
	 * @param fieldValues
	 * @param ps
	 * @throws SQLException
	 */
	private void setParameter(List<Object> values, PreparedStatement ps)
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

	/**
	 * 更新一条记录
	 * 
	 * @throws NotFoundAnnotationException
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException 
	 */
	@Override
	public void update(T t) throws NotFoundAnnotationException,
			IntrospectionException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, SQLException {
		Class<?> clazz = t.getClass();
		// 获得表名
		String tableName = getTableName(clazz);
		// 获得字段
		List<Object> fieldNames = new ArrayList<Object>();
		List<Object> fieldValues = new ArrayList<Object>();
		List<String> placeholders = new ArrayList<String>();
		String idFieldName = "";
		Object idFieldValue = "";
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			PropertyDescriptor pd = new PropertyDescriptor(field.getName(),
					t.getClass());
			if (field.isAnnotationPresent(Id.class)) {
				idFieldName = field.getAnnotation(Id.class).value();
				idFieldValue = pd.getReadMethod().invoke(t);
			} else if (field.isAnnotationPresent(Column.class)) {
				fieldNames.add(field.getAnnotation(Column.class).value());
				fieldValues.add(pd.getReadMethod().invoke(t));
				placeholders.add("?");
			}
		}
		// ID作为更新条件，放在集合的最后一个元素
		fieldNames.add(idFieldName);
		fieldValues.add(idFieldValue);
		placeholders.add("?");
		// 拼接SQL
		StringBuilder sql = new StringBuilder("");
		sql.append("update ").append(tableName).append(" set ");
		int index = fieldNames.size() - 1;
		for (int i = 0; i < index; i++) {
			sql.append(fieldNames.get(i)).append("=")
					.append(placeholders.get(i)).append(",");
		}
		sql.deleteCharAt(sql.length() - 1).append(" where ")
				.append(fieldNames.get(index)).append("=").append("?");
		//设置SQL参数占位符的值
		System.out.println(sql);
		PreparedStatement ps = DaoUtils.getConnection().prepareStatement(sql.toString());
		setParameter(fieldValues,ps);
		//执行SQL
		ps.execute();
		DaoUtils.close(ps, null);
		System.out.println(sql + "\n" + clazz.getSimpleName() + "修改成功");

	}

	@Override
	public List<T> findAllByConditions(Map<String, Object> sqlWhereMap,
			Class<T> clazz) throws NotFoundAnnotationException, SQLException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			IntrospectionException {
		List<T> list = new ArrayList<T>();
		String tableName = getTableName(clazz);
		String idFieldName = "";

		// 获取要查询的字段
		StringBuilder fieldNames = new StringBuilder();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String propertyName = field.getName();
			if (field.isAnnotationPresent(Id.class)) {
				idFieldName = field.getAnnotation(Id.class).value();
				fieldNames.append(TABLE_ALIAS + "." + idFieldName)
						.append(" as ").append(propertyName).append(",");
			} else if (field.isAnnotationPresent(Column.class)) {
				fieldNames
						.append(TABLE_ALIAS + "."
								+ field.getAnnotation(Column.class).value())
						.append(" as ").append(propertyName).append(",");
			}
		}
		fieldNames.deleteCharAt(fieldNames.length() - 1);
		// 拼装SQL
		String sql = "select " + fieldNames + " from " + tableName + " "
				+ TABLE_ALIAS;
		PreparedStatement ps = null;
		List<Object> values = null;
		if (sqlWhereMap != null) {
			List<Object> sqlWhereWithValue = getSqlWhereWithValues(sqlWhereMap);
			if (sqlWhereWithValue != null) {
				String sqlWhere = (String) sqlWhereWithValue.get(0);
				sql += sqlWhere;
				values = (List<Object>) sqlWhereWithValue.get(1);
			}
		}
		System.out.println(sql);
		// 设置参数占位符的值
		if (values != null) {
			ps = DaoUtils.getConnection().prepareStatement(sql);
			setParameter(values, ps);
		} else {
			ps = DaoUtils.getConnection().prepareStatement(sql);
		}

		// 执行SQL
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			T t = clazz.newInstance();
			initObject(t, fields, rs);
			list.add(t);
		}
		// 释放资源
		DaoUtils.close(ps, rs);
		System.out.println(sql);
		return list;
	}

	/**
	 * 根据结果集初始化对象
	 * 
	 * @param t
	 * @param fields
	 * @param rs
	 * @throws SQLException
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void initObject(T t, Field[] fields, ResultSet rs)
			throws SQLException, IntrospectionException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		for (Field field : fields) {
			String propertyName = field.getName();
			Object paramVal = null;
			Class<?> clazzField = field.getType();
			// 暂时没有支持数据的类型
			if (clazzField == String.class) {
				paramVal = rs.getString(propertyName);
			} else if (clazzField == short.class || clazzField == Short.class) {
				paramVal = rs.getShort(propertyName);
			} else if (clazzField == int.class || clazzField == Integer.class) {
				paramVal = rs.getInt(propertyName);
			} else if (clazzField == long.class || clazzField == Long.class) {
				paramVal = rs.getLong(propertyName);
			} else if (clazzField == float.class || clazzField == Float.class) {
				paramVal = rs.getFloat(propertyName);
			} else if (clazzField == double.class || clazzField == Double.class) {
				paramVal = rs.getDouble(propertyName);
			} else if (clazzField == boolean.class
					|| clazzField == Boolean.class) {
				paramVal = rs.getBoolean(propertyName);
			} else if (clazzField == byte.class || clazzField == Byte.class) {
				paramVal = rs.getByte(propertyName);
			} else if (clazzField == char.class
					|| clazzField == Character.class) {
				paramVal = rs.getCharacterStream(propertyName);
			} else if (clazzField == Date.class) {
				paramVal = rs.getTimestamp(propertyName);
			}
			// 调用Bean的set方法
			PropertyDescriptor pd = new PropertyDescriptor(propertyName,
					t.getClass());
			pd.getWriteMethod().invoke(t, paramVal);
		}

	}

	/**
	 * 根据条件，返回sql条件和条件中占位符的值
	 * 
	 * @param sqlWhereMap
	 * @return
	 */
	private List<Object> getSqlWhereWithValues(Map<String, Object> sqlWhereMap) {
		if (sqlWhereMap.size() < 1)
			return null;
		List<Object> list = new ArrayList<Object>();
		List<Object> fieldValues = new ArrayList<Object>();
		StringBuilder sqlWhere = new StringBuilder(" where ");
		Set<Entry<String, Object>> entrySets = sqlWhereMap.entrySet();
		for (Iterator<Entry<String, Object>> iterator = entrySets.iterator(); iterator
				.hasNext();) {
			Entry<String, Object> entrySet = iterator.next();
			fieldValues.add(entrySet.getValue());
			Object value = entrySet.getValue();
			if (value.getClass() == String.class) {
				sqlWhere.append(entrySet.getKey()).append(" like ").append("?")
						.append(" and ");
			} else {
				sqlWhere.append(entrySet.getKey()).append("=").append("?")
						.append(" and ");
			}
		}
		sqlWhere.delete(sqlWhere.lastIndexOf("and"), sqlWhere.length());
		list.add(sqlWhere.toString());
		list.add(fieldValues);
		return list;
	}

	/**
	 * 删除一个记录
	 */
	@Override
	public void delete(Object id, Class<T> clazz) throws Exception {
		// 获得表名
		String tableName = getTableName(clazz);
		// 获得ID字段名和值
		String idFieldName = "";
		boolean flag = false;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				idFieldName = field.getAnnotation(Id.class).value();
				flag = true;
				break;
			}
		}
		if (!flag) {
			throw new NotFoundAnnotationException(clazz.getName()
					+ " object not found id property.");
		}
		// 拼装SQL
		String sql = "delete from " + tableName + " where " + idFieldName
				+ "=?";
		PreparedStatement ps = DaoUtils.getConnection().prepareStatement(sql);
		ps.setObject(1, id);
		ps.execute();
		DaoUtils.close(ps, null);
		System.out.println(sql + "\n" + clazz.getSimpleName() + "删除成功！");

	}

	@Override
	public T getWithoutID(Object obj, Class<T> clazz) throws Exception {
		String fieldName = "";
		Object fieldValue;
		Field[] fields = clazz.getDeclaredFields();
		Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
		 
		boolean flag = false;
		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class)) {
				fieldName = field.getAnnotation(Column.class).value();
				PropertyDescriptor pd = new PropertyDescriptor(fieldName,clazz);
				fieldValue = pd.getReadMethod().invoke(obj);
				sqlWhereMap.put(fieldName, fieldValue);
				flag = true;
				
			}
		}
		if (!flag) {
			throw new NotFoundAnnotationException(clazz.getName()
					+ " object not found column property.");
		}
		// 拼装SQL
		
		List<T> list = findAllByConditions(sqlWhereMap, clazz);
		return list.size() > 0 ? list.get(0) : null;
	}
}
