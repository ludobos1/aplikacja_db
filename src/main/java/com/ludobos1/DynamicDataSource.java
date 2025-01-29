package com.ludobos1;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
  @Override
  protected Object determineCurrentLookupKey() {
    // Pobiera aktualnie ustawiony klucz bazy danych (np. db1, db2)
    return DataSourceContextHolder.getCurrentDb();
  }
}