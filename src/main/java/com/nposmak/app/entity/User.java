package com.nposmak.app.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Table(name="users")
@Entity
@Data
public class User {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="email")
	private String email;
	
	@Column(name="password")
	private String password;
	
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;
    
    
	public boolean hasRole(String roleName) {
        Iterator<Role> iterator = this.roles.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getRoleName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    public void addRole(Role role) {
    	if(roles == null){
    		roles = new HashSet<>();
    	}
    	roles.add(role);
    }
}
