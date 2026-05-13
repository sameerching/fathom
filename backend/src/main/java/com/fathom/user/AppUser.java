package com.fathom.user;

import com.fathom.common.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="app_users")
public class AppUser extends BaseEntity {
 @Id private UUID id;
 @Column(nullable=false) private String name;
 @Column(nullable=false, unique=true) private String email;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private UserStatus status;
 @PrePersist void initId(){ if(id==null) id=UUID.randomUUID(); }
 public UUID getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public UserStatus getStatus(){return status;}
 public void setName(String n){name=n;} public void setEmail(String e){email=e;} public void setStatus(UserStatus s){status=s;}
}
