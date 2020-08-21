package com.example.demo.configuration;

import com.example.demo.models.Person;
import com.example.demo.models.PersonStore;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.*;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.apache.ignite.transactions.spring.SpringTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;

@Configuration
@EnableTransactionManagement
public class IgniteCacheConfiguration {
    @Bean("igniteConfiguration")
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setIgniteInstanceName("my-ignite");
        igniteConfiguration.setClientMode(false);


//
//        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
//        dataRegionConfiguration.setMaxSize(4L*1024*1024*1024);
//
//        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
//        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);
//
//        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);

//        TransactionConfiguration txConfig = new TransactionConfiguration();
//        txConfig.setTxManagerFactory(FactoryBuilder.factoryOf(SpringTransactionManager.class));
//        txConfig.setDefaultTxConcurrency(TransactionConcurrency.OPTIMISTIC);
//        txConfig.setDefaultTxIsolation(TransactionIsolation.SERIALIZABLE);

//        igniteConfiguration.setTransactionConfiguration(txConfig);


        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("personCache");
        cacheConfiguration.setReadThrough(true);
        cacheConfiguration.setWriteThrough(true);
        cacheConfiguration.setBackups(0);
        cacheConfiguration.setCacheMode(CacheMode.REPLICATED);
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);


        Factory<PersonStore> factoryBuilder = FactoryBuilder.factoryOf(PersonStore.class);
        cacheConfiguration.setCacheStoreFactory(factoryBuilder);


//        QueryEntity queryEntity = new QueryEntity();
//        queryEntity.setKeyType(java.lang.Integer.class.toString());
//        queryEntity.setValueType(Person.class.toString());


        igniteConfiguration.setCacheConfiguration(cacheConfiguration);
        return igniteConfiguration;
    }

    @Bean(destroyMethod = "close")
    Ignite ignite(IgniteConfiguration igniteConfiguration) throws IgniteException {
        final Ignite start = Ignition.getOrStart(igniteConfiguration);
        return start;
    }

    @Bean("personIgniteCache")
    public IgniteCache<Integer, Person> personIgniteCache(Ignite ignite) {
        return ignite.getOrCreateCache("personCache");
    }

    @Bean
    @Qualifier(value = "transactionManager")
    public SpringTransactionManager transactionManager() {
        SpringTransactionManager transactionManager = new SpringTransactionManager();
        transactionManager.setIgniteInstanceName("my-ignite");
        transactionManager.setTransactionConcurrency(TransactionConcurrency.OPTIMISTIC);
        return transactionManager;
    }

}
