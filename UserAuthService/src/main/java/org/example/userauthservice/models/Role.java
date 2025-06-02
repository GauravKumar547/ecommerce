package org.example.userauthservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role extends BaseModel {
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
}