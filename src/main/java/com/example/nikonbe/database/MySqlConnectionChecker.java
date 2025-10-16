package com.example.nikonbe.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MySqlConnectionChecker implements ApplicationListener<ApplicationReadyEvent> {

  @Autowired private JdbcTemplate jdbcTemplate;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    try {
      jdbcTemplate.execute("SELECT 1");
      System.out.println(
          "====> Connect database success!"
              + " Database: "
              + jdbcTemplate.getDataSource().getConnection().getCatalog());
    } catch (Exception e) {
      System.out.println("=====> Connect database faild: " + e.getMessage());
    }
  }
}
