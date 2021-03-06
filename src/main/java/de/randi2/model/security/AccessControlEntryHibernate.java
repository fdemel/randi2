package de.randi2.model.security;


import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.sid.Sid;


@Entity
public class AccessControlEntryHibernate implements AccessControlEntry, Serializable {

	private static final long serialVersionUID = 6843101926609540483L;
	
	@ManyToOne(targetEntity=AclHibernate.class)
	private Acl acl;
	
	@Embedded
	private PermissionHibernate permission;
	
	private String roleName;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	@ManyToOne(targetEntity=SidHibernate.class)
	private SidHibernate sid;
	private boolean granting = true;
	
	@Override
	public Acl getAcl() {
		return this.acl;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public Permission getPermission() {
		// TODO Auto-generated method stub
		return this.permission;
	}

	@Override
	public Sid getSid() {
		// TODO Auto-generated method stub
		return this.sid;
	}

	@Override
	public boolean isGranting() {
		// TODO Auto-generated method stub
		return this.granting;
	}

	public void setAcl(Acl acl) {
		this.acl = acl;
	}

	public void setPermission(PermissionHibernate permission) {
		this.permission = permission;
	}

	public void setSid(SidHibernate sid) {
		this.sid = sid;
	}

	public void setGranting(boolean granting) {
		this.granting = granting;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	
}
