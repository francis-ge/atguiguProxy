package com.atgugu.javase.lesson12;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.junit.Test;

public class ProxyTest {

	/**
	 * ����һ�� Service �ӿ�.
	 * �������·���:
	 * 
	 * addNew(Person person);
	 * delete(Integer id);
	 * update(Person person);
	 * 
	 * ���ṩ�����ʵ����.
	 * 
	 * ʹ�ö�̬����ʵ���������: 
	 * 1. �ھ������ÿ�� Service ����ǰ, ����ӡ: ��ʼ����
	 * 2. ������������, ����ӡ: �����ύ
	 * 3. ���ڵ���Ŀ�귽�����쳣�����: ��ӡ: ����ع�. 
	 * 
	 */
	@Test
	public void testPersonService(){
		Service target = new ServiceImpl();
		
		Service proxy = new PersonServiceProxy(target).getPersonServiceProxy();
		
		System.out.println(ServiceImpl.getPersons());
		proxy.addNew(new Person(1005, "CCC"));
		System.out.println(ServiceImpl.getPersons());
		
		proxy.delete(1001);
		System.out.println(ServiceImpl.getPersons());
		
		proxy.update(new Person(1002, "MMM"));
		System.out.println(ServiceImpl.getPersons());
	}
	
	
	/**
	 * ���ڶ�̬�����ϸ��
	 * 1. ��Ҫһ��������Ķ���. 
	 * 2. �������ͨ���Ǻͱ��������ʹ����ͬ���������
	 * 
	 * 3. һ���, Proxy.newInstance() �ķ���ֵ��һ�����������ʵ�ֵĽӿڵ�����. 
	 * ��ȻҲ�����������Ľӿڵ�����.
	 * ע��: �ڶ�������, ������һ���ӿ����͵�����. 
	 * ��ʾ: �����������Ҫ����ʵ�ֱ��������ʵ�ֵĽӿ�����Ľӿ�, 
	 * ����ʹ�� target.getClass().getInterfaces()
	 * 
	 * 4. InvocationHandler ͨ��ʹ�������ڲ���ķ�ʽ: �����������Ҫ�� final ���͵�.
	 * 5. InvocationHandler �� invoke() �����еĵ�һ������ Object ���͵� proxy
	 * ָ�������ڱ����ص��Ǹ��������, һ�������ʹ��. 
	 * 
	 */
	@Test
	public void testProxy2(){
		final ArithmeticCalculator target = 
				new ArithmeticCalculatorImpl2();
		System.out.println(Arrays.asList(target.getClass().getInterfaces())); 
		
		Object proxy = Proxy.newProxyInstance(
				target.getClass().getClassLoader(), 
				//new Class[]{ArithmeticCalculator.class, Object.class}, 
				target.getClass().getInterfaces(),
				new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method, 
							Object[] args)
							throws Throwable {
						Object obj = null;
						
						try {
							
							obj = method.invoke(target, args);
							
						} catch (Exception e) {
							
							e.printStackTrace();
						}
						
						return obj;
					}
				});
		
		ArithmeticCalculator arithmeticCalculator = (ArithmeticCalculator) proxy;
		System.out.println(arithmeticCalculator.add(4, 2)); 
	}
	
	@Test
	public void testProxy(){
		
		final ArithmeticCalculator arithmeticCalculator = 
				new ArithmeticCalculatorImpl2();
		
		ArithmeticCalculator proxy = 
				/**
				 * ClassLoader: �ɶ�̬��������Ķ������ĸ��������������. 
				 * ͨ������ºͱ��������ʹ��һ�����������
				 * Class<?>[]: �ɶ�̬��������Ķ��������Ҫʵ�ֵĽӿڵ� Class ����
				 * InvocationHandler: ��������ô������ķ���ʱ, ������ʲô��Ϊ.
				 */
				(ArithmeticCalculator) Proxy.newProxyInstance(
						arithmeticCalculator.getClass().getClassLoader(), 
						new Class[]{ArithmeticCalculator.class}, 
						new InvocationHandler() {
							/**
							 * proxy:
							 * method: ���ڱ����õķ���
							 * args: ���÷���ʱ����Ĳ���.
							 */
							@Override
							public Object invoke(Object proxy, Method method, Object[] args)
									throws Throwable {
//								System.out.println("method: " + method);
//								System.out.println(Arrays.asList(args));
								
								System.out.println("^_^ The method " 
										+ method.getName() + " begins with "
										+ Arrays.asList(args)); 
								
								//���ñ��������Ŀ�귽��
								Object result = method.invoke(arithmeticCalculator, args);
								
								System.out.println("^_^ The method " 
										+ method.getName() 
										+ " ends with " + result);
								
								return result;
							}
						});
		
		proxy.mul(1, 2);
		
		int result = proxy.add(2, 5);
		System.out.println(result);
		
	}
	
	@Test
	public void testCalculator() {
		
		ArithmeticCalculator arithmeticCalculator = 
				new ArithmeticCalculatorImpl();
		
		arithmeticCalculator.mul(2, 3);
		
	}

}
