package com.xiao.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class test {

	public static void main(String[] args) {
		
		Filter f1 = new Filter();
		f1.setId(10);  //查询年龄
		
		Filter f2 = new Filter();
		f2.setUserName("Lucy"); //查询名字
		
		Filter f3 = new Filter();
		f3.setEmail("xiao@sinla.com,xiao@163.com");
		
		String sql1 = querySQL(f1);
		String sql2 = querySQL(f2);
		String sql3 = querySQL(f3);
		
		System.out.println(sql1);
		System.out.println(sql2);
		System.out.println(sql3);
		
	}
	
	public static String querySQL(Object f) {

		StringBuilder sb = new StringBuilder();
		sb.append("  SELECT * FROM ");
		//1.获取class
		Class class1 = f.getClass();
		//2.获取table名字  2.1判断注解
		boolean exist = class1.isAnnotationPresent(Table.class);
		if(!exist){
			return null;
		}
		//2.2获取注解对象 -> 获得注解值（表名字）
		Table table = (Table) class1.getAnnotation(Table.class);
		String tablename =  table.value();
		sb.append(tablename.toUpperCase()).append(" WHERE 1 = 1  ");
		
		//3.  通过反射遍历表对象所有注解字段
		Field[] fields = class1.getDeclaredFields();
		for (Field field : fields) {
			//4. 处理每个字段对应的SQL
			//4.1 拿到字段名字
			if(!field.isAnnotationPresent(Column.class)){
				// 非所要的注解字段 不需要处理
				continue;
			}
			Column column = field.getAnnotation(Column.class);
			String columnName = column.value();
			
			//4.2 拿到字段值  - 通过反射
			String fieldName = field.getName();	//字段名字
			String methonName = "get"+fieldName.substring(0,1).toUpperCase()
					+fieldName.substring(1); //取字段值的方法名字
			Object columnValue = null;
			try {
				Method method = class1.getMethod(methonName);
				columnValue =  method.invoke(f);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			//4.3 拼装SQL
			if ( null == columnValue || 
					(columnValue instanceof Integer && 0 == (Integer)columnValue)) {
				continue;
			}
			
			if(columnValue instanceof String){
				String stringValue =(String) columnValue;
				if( stringValue.contains(",") ){
					stringValue = stringValue.replace("," , "' , '");
				}	
				columnValue = "'" + stringValue + "'";
			}
			sb.append(" AND ").append(columnName.toUpperCase()).append(" = ").append(columnValue).append(" ");
		}
		
		return sb.append(" ;  ").toString();
		
	}

}
