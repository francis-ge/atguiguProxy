package com.atgugu.javase.lesson12;

import java.util.HashMap;
import java.util.Map;

public class ServiceImpl implements Service {

	private static Map<Integer, Person> persons = 
			new HashMap<Integer, Person>();
	
	public static Map<Integer, Person> getPersons() {
		return persons;
	}
	
	public ServiceImpl() {
		persons.put(1001, new Person(1001, "AAA"));
		persons.put(1002, new Person(1002, "BBB"));
	}
	
	@Override
	public void addNew(Person person) {
		persons.put(person.getId(), person);
	}

	@Override
	public void delete(Integer id) {
		if(id == 1001){
			throw new RuntimeException("1001 ²»ÄÜ±»É¾³ý");
		}
		persons.remove(id);
	}

	@Override
	public void update(Person person) {
		persons.put(person.getId(), person);
	}

}
