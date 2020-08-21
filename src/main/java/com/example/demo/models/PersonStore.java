package com.example.demo.models;

import com.example.demo.configuration.DataSourceConfig;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.jetbrains.annotations.Nullable;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Collection;
import java.util.Map;

public class PersonStore implements CacheStore<Integer, Person> {


    private DataSource dataSource;

    @CacheStoreSessionResource
    private CacheStoreSession ses;

    public PersonStore() {
        this.dataSource = DataSourceConfig.dataSource();
    }


    @Override
    public void loadCache(IgniteBiInClosure<Integer, Person> igniteBiInClosure, @Nullable Object... objects) throws CacheLoaderException {
        System.out.println("\n\n\n\n-------------------Loading the cache store----------------------\n\n\n\n");

        try (Connection conn = connection()) {
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
    public Person load(Integer key) throws CacheLoaderException {
        System.out.println("\n\n\n\n---------------------calling load method from cache store-----------------------------\n\n\n\n");

        try (Connection conn = connection()) {
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

        Connection conn = null;
        try {
            conn = connection();
            PreparedStatement st = conn.prepareStatement("insert into  PERSON (Id,orgId,name,salary) values(?,?,?,?)");
            st.setInt(1, (int) person.getId());
            st.setInt(2, (int) person.getOrgId());
            st.setString(3, person.getName());
            st.setInt(4, person.getSalary());
            int noOfRows = st.executeUpdate();

            System.out.println("Data inserted into database rows " + noOfRows);

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


        try (Connection conn = connection()) {
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


    // Complete transaction or simply close connection if there is no transaction.
    @Override
    public void sessionEnd(boolean commit) {
        try (Connection conn = ses.attachment()) {
            if (conn != null && ses.isWithinTransaction()) {
                if (commit)
                    conn.commit();
                else
                    conn.rollback();
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to end store session.", e);
        }
    }

    // Opens JDBC connection and attaches it to the ongoing
    // session if within a transaction.
    private Connection connection() throws SQLException {
        if (ses.isWithinTransaction()) {
            Connection conn = ses.attachment();

            if (conn == null) {
                conn = openConnection(false);

                // Store connection in the session, so it can be accessed
                // for other operations within the same transaction.
                ses.attach(conn);
            }

            return conn;
        }
        // Transaction can be null in case of simple load or put operation.
        else
            return openConnection(true);
    }

    // Opens JDBC connection.
    private Connection openConnection(boolean autocommit) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb?useSSL=false", "root", "password");
        conn.setAutoCommit(autocommit);

        return conn;
    }
}
