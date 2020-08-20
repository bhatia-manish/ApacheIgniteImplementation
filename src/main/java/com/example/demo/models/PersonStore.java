package com.example.demo.models;

import com.example.demo.configuration.DataSourceConfig;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.jetbrains.annotations.Nullable;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class PersonStore implements CacheStore<Integer, Person> {


    private DataSource dataSource;

    public PersonStore() {
        this.dataSource = DataSourceConfig.dataSource();
    }


    @Override
    public void loadCache(IgniteBiInClosure<Integer, Person> igniteBiInClosure, @Nullable Object... objects) throws CacheLoaderException {
        System.out.println("\n\n\n\n-------------------Loading the cache store----------------------\n\n\n\n");

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("select * from PERSON")) {
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        Person person = new Person();
                        person.setId(rs.getInt(1));
                        person.setOrgId(rs.getInt(2));
                        person.setName(rs.getString(3));
                        person.setSalary(rs.getInt(4));
                        igniteBiInClosure.apply((int) person.getId(), person);
                    }
                }
            }
        } catch (SQLException e) {
            throw new CacheLoaderException("Failed to load values from cache store.", e);
        }
    }

    @Override
    public void sessionEnd(boolean b) throws CacheWriterException {

    }

    @Override
    public Person load(Integer key) throws CacheLoaderException {
        System.out.println("\n\n\n\n---------------------calling load method from cache store-----------------------------\n\n\n\n");

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("select * from PERSON where id = ?")) {
                st.setString(1, key.toString());
                ResultSet rs = st.executeQuery();
                return rs.next() ? new Person(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getInt(4)) : null;
            }
        } catch (SQLException e) {
            throw new CacheLoaderException("Failed to load values from cache store.", e);
        }

    }

    @Override
    public Map<Integer, Person> loadAll(Iterable<? extends Integer> iterable) throws CacheLoaderException {
        System.out.println("\n\n\n\n------------------calling load All method from cache store-------------------\n\n\n\n");
        return null;
    }

    @Override
    public void write(Cache.Entry<? extends Integer, ? extends Person> entry) throws CacheWriterException {
        System.out.println("\n\n\n\n------------------calling write method from cache store-----------------\n\n\n\n");
        Person person = entry.getValue();

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("insert into  PERSON (Id,orgId,name,salary) values(?,?,?,?)")) {
                st.setInt(1, (int) person.getId());
                st.setInt(2, (int) person.getOrgId());
                st.setString(3, person.getName());
                st.setInt(4, person.getSalary());
                int noOfRows = st.executeUpdate();

                System.out.println("Data inserted into database rows " + noOfRows);
            }
        } catch (SQLException e) {
            throw new CacheLoaderException("Failed to delete from cache store.", e);
        }


    }

    @Override
    public void writeAll(Collection<Cache.Entry<? extends Integer, ? extends Person>> collection) throws CacheWriterException {
        System.out.println("calling write All method from cache store");

    }

    @Override
    public void delete(Object o) throws CacheWriterException {
        System.out.println("\n\n\n\n-------------------------calling delete method from cache store-----------------------\n\n\n\n");


        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("delete * from PERSON where id = ?")) {
                st.setInt(1, (int) ((Person) o).getId());
                ResultSet rs = st.executeQuery();

            }
        } catch (SQLException e) {
            throw new CacheLoaderException("Failed to delete from cache store.", e);
        }

    }

    @Override
    public void deleteAll(Collection<?> collection) throws CacheWriterException {
        System.out.println("\n\n\n\n-------------------calling delete  All method from cache store-----------------\n\n\n\n");

    }
}
