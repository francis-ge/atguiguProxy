package com.atgugu.javase.lesson12;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.junit.Test;

public class ProxyTest {

	/**
	 * 定义一个 Service 接口.
	 * 定义如下方法:
	 * 
	 * addNew(Person person);
	 * delete(Integer id);
	 * update(Person person);
	 * 
	 * 并提供具体的实现类.
	 * 
	 * 使用动态代理实现事务操作: 
	 * 1. 在具体调用每个 Service 方法前, 都打印: 开始事务
	 * 2. 方法正常结束, 都打印: 事务提交
	 * 3. 若在调用目标方法处异常情况下: 打印: 事务回滚. 
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
	 * 关于动态代理的细节
	 * 1. 需要一个被代理的对象. 
	 * 2. 类加载器通常是和被代理对象使用相同的类加载器
	 * 
	 * 3. 一般地, Proxy.newInstance() 的返回值是一个被代理对象实现的接口的类型. 
	 * 当然也可以是其他的接口的类型.
	 * 注意: 第二个参数, 必须是一个接口类型的数组. 
	 * 提示: 若代理对象不需要额外实现被代理对象实现的接口以外的接口, 
	 * 可以使用 target.getClass().getInterfaces()
	 * 
	 * 4. InvocationHandler 通常使用匿名内部类的方式: 被代理对象需要是 final 类型的.
	 * 5. InvocationHandler 的 invoke() 方法中的第一个参数 Object 类型的 proxy
	 * 指的是正在被返回的那个代理对象, 一般情况下使用. 
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
				 * ClassLoader: 由动态代理产生的对象由哪个类加载器来加载. 
				 * 通常情况下和被代理对象使用一样的类加载器
				 * Class<?>[]: 由动态代理产生的对象必须需要实现的接口的 Class 数组
				 * InvocationHandler: 当具体调用代理对象的方法时, 将产生什么行为.
				 */
				(ArithmeticCalculator) Proxy.newProxyInstance(
						arithmeticCalculator.getClass().getClassLoader(), 
						new Class[]{ArithmeticCalculator.class}, 
						new InvocationHandler() {
							/**
							 * proxy:
							 * method: 正在被调用的方法
							 * args: 调用方法时传入的参数.
							 */
							@Override
							public Object invoke(Object proxy, Method method, Object[] args)
									throws Throwable {
//								System.out.println("method: " + method);
//								System.out.println(Arrays.asList(args));
								
								System.out.println("^_^ The method " 
										+ method.getName() + " begins with "
										+ Arrays.asList(args)); 
								
								//调用被代理类的目标方法
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
