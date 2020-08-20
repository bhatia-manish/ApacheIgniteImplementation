package com.example.demo.configuration;

import com.example.demo.models.Person;
import com.example.demo.models.PersonStore;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;

@Configuration
public class IgniteCacheConfiguration {
    @Bean
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setIgniteInstanceName("my-ignite");
        igniteConfiguration.setClientMode(false);
        igniteConfiguration.setMetricsLogFrequency(0);
        igniteConfiguration.setQueryThreadPoolSize(2);
        igniteConfiguration.setDataStreamerThreadPoolSize(1);
        igniteConfiguration.setManagementThreadPoolSize(2);
        igniteConfiguration.setPublicThreadPoolSize(2);
        igniteConfiguration.setSystemThreadPoolSize(2);
        igniteConfiguration.setRebalanceThreadPoolSize(1);
        igniteConfiguration.setAsyncCallbackPoolSize(2);
        igniteConfiguration.setPeerClassLoadingEnabled(false);

//
//        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
//        dataRegionConfiguration.setMaxSize(4L*1024*1024*1024);
//
//        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
//        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);
//
//        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);


        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("personCache");
        cacheConfiguration.setReadThrough(true);
        cacheConfiguration.setWriteThrough(true);
        cacheConfiguration.setBackups(0);
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
        final Ignite start = Ignition.start(igniteConfiguration);
        return start;
    }

    @Bean
    public IgniteCache<Integer, Person> personIgniteCache(Ignite ignite) {
        return ignite.getOrCreateCache("personCache");
    }

}
