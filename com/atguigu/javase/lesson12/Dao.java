package com.atguigu.javase.lesson12;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Dao<T> {

	private Class<T> clazz;
	
	public Dao() {
//		System.out.println("Dao's Constrctor...");
//		System.out.println(this);
//		System.out.println(this.getClass()); 
		
		//��ȡ Dao ����ĸ���
//		Class clazz2 = this.getClass().getSuperclass();
//		System.out.println(clazz2); //Dao 
		
		//��ȡ Dao ����Ĵ����Ͳ����ĸ���: Dao<Person>
		Type type = this.getClass().getGenericSuperclass();
		
		//��ȡ����ķ��Ͳ���
		if(type instanceof ParameterizedType){
			ParameterizedType parameterizedType = 
					(ParameterizedType) type;
			
			Type [] args = parameterizedType.getActualTypeArguments();
//			System.out.println(Arrays.asList(args)); 
			
			if(args != null && args.length > 0){
				Type arg = args[0];
				
				if(arg instanceof Class){
					clazz = (Class<T>) arg;
				}
			}
		}
	}
	
	T get(Integer id){
		System.out.println(clazz); 
		return null;
	}
	
	void save(T entity){
		
	}
	
}
