package com.ludobos1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {
  @Value("${spring.datasource.db1.url}")
  private String db1Url;

  @Value("${spring.datasource.db1.username}")
  private String db1Username;

  @Value("${spring.datasource.db1.password}")
  private String db1Password;

  @Value("${spring.datasource.db1.driver-class-name}")
  private String db1Driver;

  @Value("${spring.datasource.db2.url}")
  private String db2Url;

  @Value("${spring.datasource.db2.username}")
  private String db2Username;

  @Value("${spring.datasource.db2.password}")
  private String db2Password;

  @Value("${spring.datasource.db2.driver-class-name}")
  private String db2Driver;

  @Value("${spring.datasource.db3.url}")
  private String db3Url;

  @Value("${spring.datasource.db3.username}")
  private String db3Username;

  @Value("${spring.datasource.db3.password}")
  private String db3Password;

  @Value("${spring.datasource.db3.driver-class-name}")
  private String db3Driver;

  @Bean
  public DataSource dataSource() {
    DynamicDataSource dynamicDataSource = new DynamicDataSource();

    DataSource db1 = DataSourceBuilder.create()
            .url(db1Url)
            .username(db1Username)
            .password(db1Password)
            .driverClassName(db1Driver)
            .build();

    DataSource db2 = DataSourceBuilder.create()
            .url(db2Url)
            .username(db2Username)
            .password(db2Password)
            .driverClassName(db2Driver)
            .build();

    DataSource db3 = DataSourceBuilder.create()
            .url(db3Url)
            .username(db3Username)
            .password(db3Password)
            .driverClassName(db3Driver)
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
