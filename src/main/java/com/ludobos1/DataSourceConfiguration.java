package com.ludobos1;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

  @Bean(name = "dataSource-db1")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.db1")
  public DataSource dataSourceDb1() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "dataSource-db2")
  @ConfigurationProperties(prefix = "spring.datasource.db2")
  public DataSource dataSourceDb2() {
    return DataSourceBuilder.create().build();
  }
}
