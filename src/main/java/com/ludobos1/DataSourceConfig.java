package com.ludobos1;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

  @Bean
  public DataSource dataSource() {
    DynamicDataSource dynamicDataSource = new DynamicDataSource();

    DataSource db1 = DataSourceBuilder.create()
            .url("jdbc:mysql://localhost:3306/ksiegarnia?noAccessToProcedureBodies=true")
            .username("ksiegarnia_admin")
            .password("maslo")
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .build();

    DataSource db2 = DataSourceBuilder.create()
            .url("jdbc:mysql://localhost:3306/ksiegarnia?noAccessToProcedureBodies=true")
            .username("user")
            .password("user2137")
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .build();

    DataSource db3 = DataSourceBuilder.create()
            .url("jdbc:mysql://localhost:3306/ksiegarnia?noAccessToProcedureBodies=true")
            .username("pracownik")
            .password("ksiazka2137")
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .build();

    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put("db1", db1);
    targetDataSources.put("db2", db2);
    targetDataSources.put("db3", db3);

    dynamicDataSource.setTargetDataSources(targetDataSources);

    dynamicDataSource.setDefaultTargetDataSource(db2);

    return dynamicDataSource;
  }
}
