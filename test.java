package com.xiao.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class test {

	public static void main(String[] args) {
		
		Filter f1 = new Filter();
		f1.setId(10);  //��ѯ����
		
		Filter f2 = new Filter();
		f2.setUserName("Lucy"); //��ѯ����
		
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
		//1.��ȡclass
		Class class1 = f.getClass();
		//2.��ȡtable����  2.1�ж�ע��
		boolean exist = class1.isAnnotationPresent(Table.class);
		if(!exist){
			return null;
		}
		//2.2��ȡע����� -> ���ע��ֵ�������֣�
		Table table = (Table) class1.getAnnotation(Table.class);
		String tablename =  table.value();
		sb.append(tablename.toUpperCase()).append(" WHERE 1 = 1  ");
		
		//3.  ͨ������������������ע���ֶ�
		Field[] fields = class1.getDeclaredFields();
		for (Field field : fields) {
			//4. ����ÿ���ֶζ�Ӧ��SQL
			//4.1 �õ��ֶ�����
			if(!field.isAnnotationPresent(Column.class)){
				// ����Ҫ��ע���ֶ� ����Ҫ����
				continue;
			}
			Column column = field.getAnnotation(Column.class);
			String columnName = column.value();
			
			//4.2 �õ��ֶ�ֵ  - ͨ������
			String fieldName = field.getName();	//�ֶ�����
			String methonName = "get"+fieldName.substring(0,1).toUpperCase()
					+fieldName.substring(1); //ȡ�ֶ�ֵ�ķ�������
			Object columnValue = null;
			try {
				Method method = class1.getMethod(methonName);
				columnValue =  method.invoke(f);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			//4.3 ƴװSQL
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
