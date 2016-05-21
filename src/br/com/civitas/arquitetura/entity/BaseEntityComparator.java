package br.com.civitas.arquitetura.entity;

import java.io.Serializable;

public abstract class BaseEntityComparator implements Serializable, IEntity {

	private static final long serialVersionUID = 6181140334280931596L;

	public boolean isNew() {
		return (getId() == null);
	}

	public static boolean isEmpty(BaseEntityComparator... entity) {
		boolean empty = false;
		for (BaseEntityComparator baseEntity : entity) {
			if (baseEntity == null || baseEntity.getId() == null) {
				empty = true;
				break;
			}
		}
		return empty;
	}
	
	public static boolean isNotEmpty(BaseEntityComparator... entity) {
		return !isEmpty(entity);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (getId() != null ? getId().hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BaseEntityComparator)) {
			return false;
		}
		BaseEntityComparator other = (BaseEntityComparator) object;
		if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[ID=" + getId() + "]";
	}
}