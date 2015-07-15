package com.atgugu.javase.lesson12;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PersonServiceProxy {

	private Service target = null;

	public PersonServiceProxy(Service target) {
		this.target = target;
	}
	
	public Service getPersonServiceProxy(){
		
		Service proxy = (Service) Proxy.newProxyInstance(target.getClass().getClassLoader(), 
				target.getClass().getInterfaces(), 
				new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args)
							throws Throwable {
						System.out.println("��������");
						
						try {
							Object result = method.invoke(target, args);
							System.out.println("�ύ����");
							return result;
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("�ع�����");
						}
						
						return null;
					}
				});
		
		return proxy;
	}
	
}
