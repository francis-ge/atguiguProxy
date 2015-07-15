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
 * ����С��:
 * 
 * 1. Class: ��һ����; һ�����������. ��װ������������ Method, �����ֶε� Filed,
 * ������������ Constructor ������.
 * 
 * 2. ��εõ� Class ����:
 * 2.1 Person.class
 * 2.2 person.getClass()
 * 2.3 Class.forName("com.atguigu.javase.Person")
 * 
 * 3. ���� Method:
 * 3.1 ��λ�ȡ Method:
 * 1). getDeclaredMethods: �õ� Method ������.
 * 2). getDeclaredMethod(String methondName, Class ... parameterTypes)
 * 
 * 3.2 ������� Method
 * 1). �������ʱ private ���ε�, ��Ҫ�ȵ��� Method �ġ�setAccessible(true), ʹ��
 * ��Ϊ�ɷ���
 * 2). method.invoke(obj, Object ... args);
 * 
 * 4. ���� Field:
 * 3.1 ��λ�ȡ Field: getField(String fieldName)
 * 3.2 ��λ�ȡ Field ��ֵ: 
 * 1). setAccessible(true)
 * 2). field.get(Object obj)
 * 3.3 ������� Field ��ֵ:
 * field.set(Obejct obj, Object val)
 * 
 * 5. �˽� Constructor �� Annotation 
 * 
 * 6. ����ͷ���.
 * 6.1 getGenericSuperClass: ��ȡ�����Ͳ����ĸ���, ����ֵΪ: BaseDao<Employee, String>
 * 6.2 Type ���ӽӿ�: ParameterizedType
 * 6.3 ���Ե��� ParameterizedType �� Type[] getActualTypeArguments() ��ȡ����
 * ����������. 
 * 
 * 7. �㶨 ReflectinUtils ����. 
 *
 */
public class ReflectionTest {

	/**
	 * ͨ������, ��ö��� Class ʱ�����ĸ���ķ��Ͳ���������
	 * ��: public EmployeeDao extends BaseDao<Employee, String>
	 * @param clazz: �����Ӧ�� Class ����
	 * @param index: ����̳и���ʱ����ķ��͵�����. �� 0 ��ʼ
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
	 * ͨ������, ��� Class �����������ĸ���ķ��Ͳ�������
	 * ��: public EmployeeDao extends BaseDao<Employee, String>
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
	 * Annotation �� ����:
	 * 1. ��ȡ Annotation
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
					throw new RuntimeException("����Ƿ�");
				}
			}
		}
		
		method.invoke(obj, 20);
		System.out.println(obj);  
		
	}
	
	/**
	 * Constructor: ��������
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
		
		//1. ��ȡ Constructor ����
		Constructor<Person> [] constructors = 
				(Constructor<Person>[]) Class.forName(className).getConstructors();
		
		for(Constructor<Person> constructor: constructors){
			System.out.println(constructor); 
		}
		
		Constructor<Person> constructor = clazz.getConstructor(String.class, int.class);
		System.out.println(constructor); 
		
		//2. ���ù������� newInstance() ������������
		Object obj = constructor.newInstance("�й��", 1);
				
	}
	
	@Test
	public void testClassField() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String className = "com.atguigu.javase.lesson12.Student";
		String fieldName = "age"; //����Ϊ˽��, �������丸����. 
		Object val = 20;
		
		//���� className ��Ӧ��Ķ���, ��Ϊ�� fieldName ��ֵΪ val
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
	 * Field: ��װ���ֶε���Ϣ. 
	 * 1. ��ȡ�ֶ�:
	 * 1.1 Field [] fields = clazz.getDeclaredFields();
	 * 1.2 Field field2 = clazz.getDeclaredField("age");
	 * 
	 * 2. ��ȡָ�������ָ���ֶε�ֵ.
	 * public Object get(Object obj)
	 * obj Ϊ�ֶ����ڵĶ���.
	 * 
	 * Object val = field.get(person);
	 * 
	 * ע��: �����ֶ���˽�е�, ���ȵ��� setAccessible(true) ����
	 * 
	 * 3. ����ָ�������ָ���ֶε�ֵ
	 * public void set(Object obj, Object value)
	 * obj: �ֶ����ڵĶ���
	 * value: Ҫ���õ�ֵ.
	 * 
	 * field.set(person, "atguigu");
	 * 
	 * 
	 */
	@Test
	public void testField() throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		String className = "com.atguigu.javase.lesson12.Person";
		Class clazz = Class.forName(className);
		
		//1. ��ȡ�ֶ�
		//1.1 ��ȡ Field ������
		Field [] fields = clazz.getDeclaredFields();
		for(Field field: fields){
			System.out.println(field.getName()); 
		}
		
		//1.2 ��ȡָ�����ֵ� Field
		Field field = clazz.getDeclaredField("name");
		System.out.println(field.getName()); 
		
		Person person = new Person("ABC", 12);
		//2. ��ȡָ������� Field ��ֵ
		Object val = field.get(person);
		System.out.println(val);
		
		//3. ����ָ������� Field ��ֵ
		field.set(person, "atguigu");
		System.out.println(person.getName());
		
		//4. �����ֶ���˽�е�, ��Ҫ���� setAccessible(true) ����
		Field field2 = clazz.getDeclaredField("age");
		field2.setAccessible(true);
		System.out.println(field2.get(person)); 
	}
	
	/**
	 * ��ȡ clazz �� methodName ����. �÷���������˽�з���, �������ڸ�����(˽�з���)
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
	 * �� Object ����õ����Ӧ�� Class ����.
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
	 * ִ�� obj ����� method ����, ����ֵΪ args
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
		//1. ȫ����
		String className = "com.atguigu.javase.lesson12.Student";
		//2. ������: ������ 1 ��������, Ҳ�����ڸ�����. ������˽�з���, Ҳ�����ǹ��з���.
		String methodName = "method3";
		//3. ִ�� 2 ��Ӧ�ķ���ʱ��Ҫ����Ĳ����б�. 
		Object [] args = {"�й��", 25};
		
		//������������, ִ�� methodName ��Ӧ�ķ���, ����ӡ����ֵ.
		
		Class clazz = Class.forName(className);
		Class [] parameterTypes = getParamerTypes(args);
		
		Method method = getMethod(clazz, methodName, parameterTypes);
		Object result = invokeMethod(clazz.newInstance(), method, args);
		
		System.out.println(result); 
	}

	
	
	/**
	 * 
	 * @param obj: ĳ�����һ������
	 * @param methodName: ���һ�������ķ�����. 
	 * �÷���Ҳ������˽�з���, �������Ǹ÷����ڸ����ж����(˽��)����
	 * @param args: ���ø÷�����Ҫ����Ĳ���
	 * @return: ���÷�����ķ���ֵ
	 */
	public Object invoke2(Object obj, String methodName, 
			Object ... args){
		//1. ��ȡ Method ����
		Class [] parameterTypes = new Class[args.length];
		for(int i = 0; i < args.length; i++){
			parameterTypes[i] = args[i].getClass();
		}
		
		try {
			Method method = getMethod(obj.getClass(), methodName, parameterTypes);
			method.setAccessible(true);
			//2. ִ�� Method ����
			//3. ���ط����ķ���ֵ
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
		//Student ��� method1() ����������, ��ӡ: private void method1
		
		Object result = invoke2(obj, "method2"); 
		//Student ��ĸ���� method2() ����������, ����ֵΪ "private String method2"
		System.out.println(result);
	
	}
	
	/**
	 * ��ͨ�� Method �� invoke() �������÷���, ������Ȩ�޲���, �������ʹ�÷���
	 * ��Ϊ�ɱ����ʵ�: 
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
		
		//����Ҫͨ������ִ��˽�з���
		method.setAccessible(true);
		
		method.invoke(obj, 10);
	}
	
	/**
	 * ��ȡ��ǰ��ĸ���:
	 * ֱ�ӵ��� Class ����� getSuperClass() ����. 
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
	 * @param className: ĳ�����ȫ����
	 * @param methodName: ���һ�������ķ�����. �÷���Ҳ������˽�з���. 
	 * @param args: ���ø÷�����Ҫ����Ĳ���
	 * @return: ���÷�����ķ���ֵ
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
	 * @param obj: ����ִ�е��Ǹ�����. 
	 * @param methodName: ���һ�������ķ�����. �÷���Ҳ������˽�з���. 
	 * @param args: ���ø÷�����Ҫ����Ĳ���
	 * @return: ���÷�����ķ���ֵ
	 */
	public Object invoke(Object obj, String methodName, Object ... args){
		//1. ��ȡ Method ����
		Class [] parameterTypes = new Class[args.length];
		for(int i = 0; i < args.length; i++){
			parameterTypes[i] = args[i].getClass();
			System.out.println(parameterTypes[i]); 
		}
		
		try {
			Method method = obj.getClass().getMethod(methodName, parameterTypes);
			//2. ִ�� Method ����
			//3. ���ط����ķ���ֵ
			return method.invoke(obj, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Test
	public void testInvoke(){
		Object obj = new Person();
		
		invoke(obj, "setName", "�й��", 1);
		invoke("com.atguigu.javase.lesson12.Person", 
				"setName", "�й��", 12);
		
		Object result = 
				invoke("java.text.SimpleDateFormat", "format", new Date());
		System.out.println(result); 
	}
	
	/**
	 * Class �Ƕ�һ���������
	 * �������: Field
	 * ��ķ���: Method
	 * ��Ĺ�����: Constrctor
	 * 
	 * Method: ��Ӧ���еķ���.
	 * 1. ��ȡ Method: 
	 * 1.1  ��ȡ��ķ���������: clazz.getDeclaredMethods();
	 * 1.2 ��ȡ���ָ���ķ���: getDeclaredMethod(String name,
     *                           Class<?>... parameterTypes)   
     *    name: ������
     *    parameterTypes: �����Ĳ�������(ʹ��Class��������)���б�        
     *    
     *    Method method = 
     *    	clazz.getDeclaredMethod("setName", String.class);  
     *    
     *    method = 
     *    	clazz.getDeclaredMethod("setName", String.class, Integer.class);            
	 * 
	 * 1.3 ͨ�� method ����ִ�з���:
	 * public Object invoke(Object obj, Object... args)
	 * 
	 * obj: ִ���ĸ�����ķ���?
	 * args: ִ�з���ʱ��Ҫ����Ĳ���.
     *
     * Object obj = clazz.newInstance();
     * //method ��Ӧ��ԭ����Ϊ: 
     * 	public void setName(String name, Integer age)
     * method.invoke(obj, "�й��", 12);
	 * 
	 */
	@Test
	public void testMethod() throws Exception{
		Class clazz = Class.forName("com.atguigu.javase.lesson12.Person");
		
		//1. �õ� clazz ��Ӧ����������Щ����, ���ܻ�ȡ private ����. 
		Method [] methods = clazz.getMethods();
		for(Method method: methods){
			System.out.println("^" + method.getName()); 
		}
		
		//2. ��ȡ���еķ���, ���� private ����, ��ֻ��ȡ��ǰ�������ķ���. 
		Method [] methods2 = clazz.getDeclaredMethods();
		for(Method method: methods2){
			System.out.println("~" + method.getName()); 
		}
		
		//3. ��ȡָ���ķ���. 
		Method method = clazz.getDeclaredMethod("setName", String.class);
		System.out.println(method); 
		
		method = clazz.getDeclaredMethod("test");
		System.out.println(method);
		
		method = clazz.getDeclaredMethod("setName", String.class, Integer.class);
		System.out.println(method);
		
		//4. ִ�з���!
		Object obj = clazz.newInstance();
		method.invoke(obj, "�й��", 12);
	}
	
	@Test
	public void testClassLoader() throws ClassNotFoundException, FileNotFoundException{
		//1. ��ȡһ��ϵͳ���������
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		System.out.println(classLoader);
		
		//2. ��ȡϵͳ��������ĸ��������. 
		classLoader = classLoader.getParent();
		System.out.println(classLoader); 
		
		//3. ��ȡ��չ��������ĸ��������.
		classLoader = classLoader.getParent();
		System.out.println(classLoader);
		
		//4. ���Ե�ǰ�����ĸ�����������м���: 
		classLoader = Class.forName("com.atguigu.javase.lesson12.ReflectionTest")
		     .getClassLoader();
		System.out.println(classLoader); 
		
		//5. ���� JDK �ṩ�� Object �����ĸ���������������
		classLoader = Class.forName("java.lang.Object")
			     .getClassLoader();
		System.out.println(classLoader); 
		
		//6*. �������������һ����Ҫ����. 
		//���� getResourceAsStream ��ȡ��·���µ��ļ���Ӧ��������. 
		InputStream in = null;                                   //com/atguigu/javase/lesson12/
		in = this.getClass().getClassLoader().getResourceAsStream("com/atguigu/javase/lesson12/test.properties");
		System.out.println(in);
				//new FileInputStream("test.properties");
	}
	
	/**
	 * *Class ��� newInstance() ����. 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Test
	public void testNewInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String className = "com.atguigu.javase.lesson12.Person";
		Class clazz = Class.forName(className);
		
		//���� Class ����� newInstance() �������������һ������.
		//ʵ�ʵ��õ�������Ǹ� �޲����� ������!
		//һ���, һ�����������˴������Ĺ�����, ҲҪ����һ���޲����Ĺ�����.
		Object obj = clazz.newInstance();
		System.out.println(obj); 
	}
	
	/**
	 * ���� Class: 
	 * 1. Class ��һ����
	 * 2. �����վ��Ӻ���Եõ�����Ϣ��ĳ��������ݳ�Ա���������͹�������
	 *    ĳ���ൽ��ʵ������Щ�ӿڡ�
	 * 3. ����ÿ������ԣ�JRE ��Ϊ�䱣��һ������� Class ���͵Ķ���
	 *    һ�� Class ����������ض�ĳ������й���Ϣ��
	 * 4. Class ����ֻ����ϵͳ��������
	 * 5. һ������ JVM ��ֻ����һ��Classʵ�� 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testClass() throws ClassNotFoundException {
		
		Class clazz = null;
		
		//*1. �õ� Class ����
		//1.1 ֱ��ͨ�� ����.class �ķ�ʽ�õ�
		clazz = Person.class;
		
		//1.2 ͨ��������� getClass() ��������ȡ
//		Object obj = new Person();
//		clazz = obj.getClass();
		
		//1.3 ͨ��ȫ�����ķ�ʽ��ȡ. �õĽ϶ࡣ 
		String className = "com.atguigu.javase.lesson12.Person";
		clazz = Class.forName(className);
		
//		Field[] fields = clazz.getDeclaredFields();
		
		System.out.println(); 
	}

}
