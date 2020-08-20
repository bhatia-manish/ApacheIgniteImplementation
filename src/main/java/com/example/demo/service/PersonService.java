package com.example.demo.service;

import com.example.demo.models.Person;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {

    private IgniteCache<Integer, Person> personIgniteCache;

    @Autowired
    public PersonService(IgniteCache<Integer, Person> personIgniteCache) {
        this.personIgniteCache = personIgniteCache;
        personIgniteCache.loadCache(null);
    }

    public List<Person> getAllPerson() {
        List<Person> person = new ArrayList<>();
        for (javax.cache.Cache.Entry<Integer, Person> integerPersonEntry : personIgniteCache)
            person.add(integerPersonEntry.getValue());
        return person;
    }

    public Person getPerson(int id) {
        return (Person) personIgniteCache.get(id);
    }

    public Person createPerson(Person person) {
        return personIgniteCache.getAndPut((int) person.getId(), person);
    }

    public Person updatePerson(Person person) {
        return personIgniteCache.getAndPut((int) person.getId(), person);
    }

    public boolean deletePerson(int id) {
        if (personIgniteCache.containsKey(id)) {
            return personIgniteCache.remove(id);
        }
        return false;
    }
}
