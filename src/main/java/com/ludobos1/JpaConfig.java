package com.ludobos1;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

  // Konfiguracja JPA dla db1
  @Bean(name = "entityManagerFactoryDb1")
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryDb1(
          @Qualifier("dataSource-db1") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
    LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
    factoryBean.setDataSource(dataSource);
    factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
    factoryBean.setPackagesToScan("com.example"); // Ustaw pakiet z encjami
    return factoryBean;
  }

  // Konfiguracja JPA dla db2
  @Bean(name = "entityManagerFactoryDb2")
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryDb2(
          @Qualifier("dataSource-db2") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
    LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
    factoryBean.setDataSource(dataSource);
    factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
    factoryBean.setPackagesToScan("com.example"); // Ustaw pakiet z encjami
    return factoryBean;
  }

  // Transaction Manager dla db1
  @Bean(name = "transactionManagerDb1")
  @Primary
  public PlatformTransactionManager transactionManagerDb1(
          @Qualifier("entityManagerFactoryDb1") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  // Transaction Manager dla db2
  @Bean(name = "transactionManagerDb2")
  public PlatformTransactionManager transactionManagerDb2(
          @Qualifier("entityManagerFactoryDb2") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
