package com.atguigu.javase.lesson12;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import org.junit.Test;

/**
 * 反射小结:
 * 
 * 1. Class: 是一个类; 一个描述类的类. 封装了描述方法的 Method, 描述字段的 Filed,
 * 描述构造器的 Constructor 等属性.
 * 
 * 2. 如何得到 Class 对象:
 * 2.1 Person.class
 * 2.2 person.getClass()
 * 2.3 Class.forName("com.atguigu.javase.Person")
 * 
 * 3. 关于 Method:
 * 3.1 如何获取 Method:
 * 1). getDeclaredMethods: 得到 Method 的数组.
 * 2). getDeclaredMethod(String methondName, Class ... parameterTypes)
 * 
 * 3.2 如果调用 Method
 * 1). 如果方法时 private 修饰的, 需要先调用 Method 的　setAccessible(true), 使其
 * 变为可访问
 * 2). method.invoke(obj, Object ... args);
 * 
 * 4. 关于 Field:
 * 3.1 如何获取 Field: getField(String fieldName)
 * 3.2 如何获取 Field 的值: 
 * 1). setAccessible(true)
 * 2). field.get(Object obj)
 * 3.3 如何设置 Field 的值:
 * field.set(Obejct obj, Object val)
 * 
 * 5. 了解 Constructor 和 Annotation 
 * 
 * 6. 反射和泛型.
 * 6.1 getGenericSuperClass: 获取带泛型参数的父类, 返回值为: BaseDao<Employee, String>
 * 6.2 Type 的子接口: ParameterizedType
 * 6.3 可以调用 ParameterizedType 的 Type[] getActualTypeArguments() 获取泛型
 * 参数的数组. 
 * 
 * 7. 搞定 ReflectinUtils 即可. 
 *
 */
public class ReflectionTest {

	/**
	 * 通过反射, 获得定义 Class 时声明的父类的泛型参数的类型
	 * 如: public EmployeeDao extends BaseDao<Employee, String>
	 * @param clazz: 子类对应的 Class 对象
	 * @param index: 子类继承父类时传入的泛型的索引. 从 0 开始
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class clazz, int index){
		
		Type type = clazz.getGenericSuperclass();
		
		if(!(type instanceof ParameterizedType)){
			return null;
		}
		
		ParameterizedType parameterizedType = 
				(ParameterizedType) type;
		
		Type [] args = parameterizedType.getActualTypeArguments();
		
		if(args == null){
			return null;
		}
		
		if(index < 0 || index > args.length - 1){
			return null;
		}
		
		Type arg = args[index];
		if(arg instanceof Class){
			return (Class) arg;
		}
		
		return null;
	}
	
	/**
	 * 通过反射, 获得 Class 定义中声明的父类的泛型参数类型
	 * 如: public EmployeeDao extends BaseDao<Employee, String>
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<T> Class<T> getSuperGenericType(Class clazz){
		return getSuperClassGenricType(clazz, 0);
	}
	
	@Test
	public void testGetSuperClassGenricType(){
		Class clazz = EmployeeDao.class;
		//Employee.class
		Class argClazz = getSuperClassGenricType(clazz, 0);
		System.out.println(argClazz);
		
		//String.class
		argClazz = getSuperClassGenricType(clazz, 1);
		System.out.println(argClazz);
	}
	
	@Test
	public void testGenericAndReflection(){
		
		PersonDao personDao = new PersonDao();
		
		Person entity = new Person();
		personDao.save(entity);
		
		//class com.atguigu.javase.lesson12.Person
		Person result = personDao.get(1); 
	}
	
	/**
	 * Annotation 和 反射:
	 * 1. 获取 Annotation
	 * 
	 * getAnnotation(Class<T> annotationClass) 
	 * getDeclaredAnnotations() 
	 * 
	 */
	@Test
	public void testAnnotation() throws Exception{
		String className = "com.atguigu.javase.lesson12.Person";
		
		Class clazz = Class.forName(className);
		Object obj = clazz.newInstance();
		
		Method method = clazz.getDeclaredMethod("setAge", int.class);
		int val = 6;
		
		Annotation annotation = method.getAnnotation(AgeValidator.class);
		if(annotation != null){
			if(annotation instanceof AgeValidator){
				AgeValidator ageValidator = (AgeValidator) annotation;
				
				if(val < ageValidator.min() || val > ageValidator.max()){
					throw new RuntimeException("年龄非法");
				}
			}
		}
		
		method.invoke(obj, 20);
		System.out.println(obj);  
		
	}
	
	/**
	 * Constructor: 代表构造器
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Test
	public void testConstructor() throws SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String className = "com.atguigu.javase.lesson12.Person";
		Class<Person> clazz = (Class<Person>) Class.forName(className);
		
		//1. 获取 Constructor 对象
		Constructor<Person> [] constructors = 
				(Constructor<Person>[]) Class.forName(className).getConstructors();
		
		for(Constructor<Person> constructor: constructors){
			System.out.println(constructor); 
		}
		
		Constructor<Person> constructor = clazz.getConstructor(String.class, int.class);
		System.out.println(constructor); 
		
		//2. 调用构造器的 newInstance() 方法创建对象
		Object obj = constructor.newInstance("尚硅谷", 1);
				
	}
	
	@Test
	public void testClassField() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String className = "com.atguigu.javase.lesson12.Student";
		String fieldName = "age"; //可能为私有, 可能在其父类中. 
		Object val = 20;
		
		//创建 className 对应类的对象, 并为其 fieldName 赋值为 val
		Object obj = null;
		
		Class clazz = Class.forName(className);
		Field field = getField(clazz, fieldName);
		
		obj = clazz.newInstance();
		setFieldValue(obj, field, val);
		
		Student stu = (Student) obj;
		System.out.println(stu.getAge()); //20
	}
	
	public Object getFieldValue(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException{
		field.setAccessible(true);
		return field.get(obj);
	}

	public void setFieldValue(Object obj, Field field, Object val)
			throws IllegalAccessException {
		field.setAccessible(true);
		field.set(obj, val);
	}

	public Field getField(Class clazz, String fieldName) {
		Field field = null;
		for(Class clazz2 = clazz; clazz2 != Object.class; 
				clazz2 = clazz2.getSuperclass()){
			try {
				field = clazz2.getDeclaredField(fieldName);
			} catch (Exception e) {}
		}
		return field;
	}
	
	/**
	 * Field: 封装了字段的信息. 
	 * 1. 获取字段:
	 * 1.1 Field [] fields = clazz.getDeclaredFields();
	 * 1.2 Field field2 = clazz.getDeclaredField("age");
	 * 
	 * 2. 获取指定对象的指定字段的值.
	 * public Object get(Object obj)
	 * obj 为字段所在的对象.
	 * 
	 * Object val = field.get(person);
	 * 
	 * 注意: 若该字段是私有的, 需先调用 setAccessible(true) 方法
	 * 
	 * 3. 设置指定对象的指定字段的值
	 * public void set(Object obj, Object value)
	 * obj: 字段所在的对象
	 * value: 要设置的值.
	 * 
	 * field.set(person, "atguigu");
	 * 
	 * 
	 */
	@Test
	public void testField() throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		String className = "com.atguigu.javase.lesson12.Person";
		Class clazz = Class.forName(className);
		
		//1. 获取字段
		//1.1 获取 Field 的数组
		Field [] fields = clazz.getDeclaredFields();
		for(Field field: fields){
			System.out.println(field.getName()); 
		}
		
		//1.2 获取指定名字的 Field
		Field field = clazz.getDeclaredField("name");
		System.out.println(field.getName()); 
		
		Person person = new Person("ABC", 12);
		//2. 获取指定对象的 Field 的值
		Object val = field.get(person);
		System.out.println(val);
		
		//3. 设置指定对象的 Field 的值
		field.set(person, "atguigu");
		System.out.println(person.getName());
		
		//4. 若该字段是私有的, 需要调用 setAccessible(true) 方法
		Field field2 = clazz.getDeclaredField("age");
		field2.setAccessible(true);
		System.out.println(field2.get(person)); 
	}
	
	/**
	 * 获取 clazz 的 methodName 方法. 该方法可能是私有方法, 还可能在父类中(私有方法)
	 * @param clazz
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public Method getMethod(Class clazz, String methodName, 
			Class ... parameterTypes){
		
		for(;clazz != Object.class; clazz = clazz.getSuperclass()){
			try {
				Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {}			
		}
		
		return null;
	}
	
	/**
	 * 由 Object 数组得到其对应的 Class 数组.
	 * @param args
	 * @return
	 */
	public Class [] getParamerTypes(Object ... args){
		Class [] parameterTypes = new Class[args.length];
		
		for(int i = 0; i < args.length; i++){
			parameterTypes[i] = args[i].getClass();
		}
		
		return parameterTypes;
	}
	
	/**
	 * 执行 obj 对象的 method 方法, 参数值为 args
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object invokeMethod(Object obj, Method method, Object[] args)
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException {
		method.setAccessible(true);
		Object result = method.invoke(obj, args);
		return result;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testClassMethod() throws Exception{
		//1. 全类名
		String className = "com.atguigu.javase.lesson12.Student";
		//2. 方法名: 可能在 1 给的类中, 也可能在父类中. 可能是私有方法, 也可能是公有方法.
		String methodName = "method3";
		//3. 执行 2 对应的方法时需要传入的参数列表. 
		Object [] args = {"尚硅谷", 25};
		
		//根据以下条件, 执行 methodName 对应的方法, 并打印返回值.
		
		Class clazz = Class.forName(className);
		Class [] parameterTypes = getParamerTypes(args);
		
		Method method = getMethod(clazz, methodName, parameterTypes);
		Object result = invokeMethod(clazz.newInstance(), method, args);
		
		System.out.println(result); 
	}

	
	
	/**
	 * 
	 * @param obj: 某个类的一个对象
	 * @param methodName: 类的一个方法的方法名. 
	 * 该方法也可能是私有方法, 还可能是该方法在父类中定义的(私有)方法
	 * @param args: 调用该方法需要传入的参数
	 * @return: 调用方法后的返回值
	 */
	public Object invoke2(Object obj, String methodName, 
			Object ... args){
		//1. 获取 Method 对象
		Class [] parameterTypes = new Class[args.length];
		for(int i = 0; i < args.length; i++){
			parameterTypes[i] = args[i].getClass();
		}
		
		try {
			Method method = getMethod(obj.getClass(), methodName, parameterTypes);
			method.setAccessible(true);
			//2. 执行 Method 方法
			//3. 返回方法的返回值
			return method.invoke(obj, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Test
	public void testGetMethod() throws Exception{
		Class clazz = Class.forName("com.atguigu.javase.lesson12.Student");
	
		Method method = getMethod(clazz, "method1", Integer.class);
		System.out.println(method); 
		
		method = getMethod(clazz, "method2");
		System.out.println(method); 
	}
	
	@Test
	public void testInvoke2(){
		Object obj = new Student();
		
		invoke2(obj, "method1", 10); 
		//Student 类的 method1() 方法被调用, 打印: private void method1
		
		Object result = invoke2(obj, "method2"); 
		//Student 类的父类的 method2() 方法被调用, 返回值为 "private String method2"
		System.out.println(result);
	
	}
	
	/**
	 * 若通过 Method 的 invoke() 方法调用方法, 而访问权限不足, 则可以先使该方法
	 * 变为可被访问的: 
	 * 
	 * method.setAccessible(true);
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvokePrivateMethod() throws Exception{
		Object obj = new Student();
		
		Class clazz = obj.getClass();
		Method method = clazz.getDeclaredMethod("method1", Integer.class);
		System.out.println(method); 
		
		//若需要通过反射执行私有方法
		method.setAccessible(true);
		
		method.invoke(obj, 10);
	}
	
	/**
	 * 获取当前类的父类:
	 * 直接调动 Class 对象的 getSuperClass() 方法. 
	 * @throws Exception 
	 */
	@Test
	public void testGetSuperClass() throws Exception{
		String className = "com.atguigu.javase.lesson12.Student";
		
		Class clazz = Class.forName(className);
		Class superClazz = clazz.getSuperclass();
		
		System.out.println(superClazz); 
	}
	
	/**
	 * 
	 * @param className: 某个类的全类名
	 * @param methodName: 类的一个方法的方法名. 该方法也可能是私有方法. 
	 * @param args: 调用该方法需要传入的参数
	 * @return: 调用方法后的返回值
	 */
	public Object invoke(String className, String methodName, Object ... args){
		Object obj = null;
		
		try {
			obj = Class.forName(className).newInstance();
			return invoke(obj, methodName, args);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param obj: 方法执行的那个对象. 
	 * @param methodName: 类的一个方法的方法名. 该方法也可能是私有方法. 
	 * @param args: 调用该方法需要传入的参数
	 * @return: 调用方法后的返回值
	 */
	public Object invoke(Object obj, String methodName, Object ... args){
		//1. 获取 Method 对象
		Class [] parameterTypes = new Class[args.length];
		for(int i = 0; i < args.length; i++){
			parameterTypes[i] = args[i].getClass();
			System.out.println(parameterTypes[i]); 
		}
		
		try {
			Method method = obj.getClass().getMethod(methodName, parameterTypes);
			//2. 执行 Method 方法
			//3. 返回方法的返回值
			return method.invoke(obj, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Test
	public void testInvoke(){
		Object obj = new Person();
		
		invoke(obj, "setName", "尚硅谷", 1);
		invoke("com.atguigu.javase.lesson12.Person", 
				"setName", "尚硅谷", 12);
		
		Object result = 
				invoke("java.text.SimpleDateFormat", "format", new Date());
		System.out.println(result); 
	}
	
	/**
	 * Class 是对一个类的描述
	 * 类的属性: Field
	 * 类的方法: Method
	 * 类的构造器: Constrctor
	 * 
	 * Method: 对应类中的方法.
	 * 1. 获取 Method: 
	 * 1.1  获取类的方法的数组: clazz.getDeclaredMethods();
	 * 1.2 获取类的指定的方法: getDeclaredMethod(String name,
     *                           Class<?>... parameterTypes)   
     *    name: 方法名
     *    parameterTypes: 方法的参数类型(使用Class来来描述)的列表        
     *    
     *    Method method = 
     *    	clazz.getDeclaredMethod("setName", String.class);  
     *    
     *    method = 
     *    	clazz.getDeclaredMethod("setName", String.class, Integer.class);            
	 * 
	 * 1.3 通过 method 对象执行方法:
	 * public Object invoke(Object obj, Object... args)
	 * 
	 * obj: 执行哪个对象的方法?
	 * args: 执行方法时需要传入的参数.
     *
     * Object obj = clazz.newInstance();
     * //method 对应的原方法为: 
     * 	public void setName(String name, Integer age)
     * method.invoke(obj, "尚硅谷", 12);
	 * 
	 */
	@Test
	public void testMethod() throws Exception{
		Class clazz = Class.forName("com.atguigu.javase.lesson12.Person");
		
		//1. 得到 clazz 对应的类中有哪些方法, 不能获取 private 方法. 
		Method [] methods = clazz.getMethods();
		for(Method method: methods){
			System.out.println("^" + method.getName()); 
		}
		
		//2. 获取所有的方法, 包括 private 方法, 且只获取当前类声明的方法. 
		Method [] methods2 = clazz.getDeclaredMethods();
		for(Method method: methods2){
			System.out.println("~" + method.getName()); 
		}
		
		//3. 获取指定的方法. 
		Method method = clazz.getDeclaredMethod("setName", String.class);
		System.out.println(method); 
		
		method = clazz.getDeclaredMethod("test");
		System.out.println(method);
		
		method = clazz.getDeclaredMethod("setName", String.class, Integer.class);
		System.out.println(method);
		
		//4. 执行方法!
		Object obj = clazz.newInstance();
		method.invoke(obj, "尚硅谷", 12);
	}
	
	@Test
	public void testClassLoader() throws ClassNotFoundException, FileNotFoundException{
		//1. 获取一个系统的类加载器
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		System.out.println(classLoader);
		
		//2. 获取系统类加载器的父类加载器. 
		classLoader = classLoader.getParent();
		System.out.println(classLoader); 
		
		//3. 获取扩展类加载器的父类加载器.
		classLoader = classLoader.getParent();
		System.out.println(classLoader);
		
		//4. 测试当前类由哪个类加载器进行加载: 
		classLoader = Class.forName("com.atguigu.javase.lesson12.ReflectionTest")
		     .getClassLoader();
		System.out.println(classLoader); 
		
		//5. 测试 JDK 提供的 Object 类由哪个类加载器负责加载
		classLoader = Class.forName("java.lang.Object")
			     .getClassLoader();
		System.out.println(classLoader); 
		
		//6*. 关于类加载器的一个主要方法. 
		//调用 getResourceAsStream 获取类路径下的文件对应的输入流. 
		InputStream in = null;                                   //com/atguigu/javase/lesson12/
		in = this.getClass().getClassLoader().getResourceAsStream("com/atguigu/javase/lesson12/test.properties");
		System.out.println(in);
				//new FileInputStream("test.properties");
	}
	
	/**
	 * *Class 类的 newInstance() 方法. 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Test
	public void testNewInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String className = "com.atguigu.javase.lesson12.Person";
		Class clazz = Class.forName(className);
		
		//利用 Class 对象的 newInstance() 方法来创建类的一个对象.
		//实际调用的是类的那个 无参数的 构造器!
		//一般地, 一个类若声明了带参数的构造器, 也要声明一个无参数的构造器.
		Object obj = clazz.newInstance();
		System.out.println(obj); 
	}
	
	/**
	 * 关于 Class: 
	 * 1. Class 是一个类
	 * 2. 对象照镜子后可以得到的信息：某个类的数据成员名、方法和构造器、
	 *    某个类到底实现了哪些接口。
	 * 3. 对于每个类而言，JRE 都为其保留一个不变的 Class 类型的对象。
	 *    一个 Class 对象包含了特定某个类的有关信息。
	 * 4. Class 对象只能由系统建立对象
	 * 5. 一个类在 JVM 中只会有一个Class实例 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testClass() throws ClassNotFoundException {
		
		Class clazz = null;
		
		//*1. 得到 Class 对象
		//1.1 直接通过 类名.class 的方式得到
		clazz = Person.class;
		
		//1.2 通过对象调用 getClass() 方法来获取
//		Object obj = new Person();
//		clazz = obj.getClass();
		
		//1.3 通过全类名的方式获取. 用的较多。 
		String className = "com.atguigu.javase.lesson12.Person";
		clazz = Class.forName(className);
		
//		Field[] fields = clazz.getDeclaredFields();
		
		System.out.println(); 
	}

}
