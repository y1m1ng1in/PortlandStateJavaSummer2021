package edu.pdx.cs410J.yl6;

import java.util.UUID;

public class User {

  protected UUID id;
  protected String username;
  protected String password;
  protected String email;
  protected String address;
  
  public User(String username, String password, String email, String address) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.password = password;
    this.email = email;
    this.address = address;
  }

  public User(String id, String username, String password, String email, String address) {
    this.id = UUID.fromString(id);
    this.username = username;
    this.password = password;
    this.email = email;
    this.address = address;
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public String getAddress() {
    return address;
  }

}
