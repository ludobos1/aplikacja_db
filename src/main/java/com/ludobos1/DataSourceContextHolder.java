package com.ludobos1;

public class DataSourceContextHolder {
  private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

  public static void setCurrentDb(String dbType) {
    contextHolder.set(dbType);
  }

  public static String getCurrentDb() {
    return contextHolder.get();
  }

  public static void clear() {
    contextHolder.remove();
  }
}
