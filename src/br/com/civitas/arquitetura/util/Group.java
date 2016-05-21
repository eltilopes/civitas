package br.com.civitas.arquitetura.util;

import java.util.Arrays;

public final class Group {

	private final transient Integer id;

	private final transient Object[] groupment;

	protected static Group create(final Object[] groupment, final int id) {
		return new Group(groupment, id);
	}

	protected static Group create(final Object[] groupment) {
		return new Group(groupment);
	}

	private Group(final Object[] groupment, final int id) {
		this.groupment = groupment;
		this.id = id;
	}

	private Group(final Object[] groupment) {
		this.groupment = groupment;
		id = null;
	}

	public Object[] getGroupment() {
		return groupment;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(groupment);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		final Group other = (Group) obj;

		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if (!groupmentIsEquals(groupment, other.getGroupment())) {
			return false;
		}

		return true;
	}

	private static boolean groupmentIsEquals(final Object[] group, final Object[] otherGroup) {
		boolean result = true;
		int index = 0;

		if (group.length != otherGroup.length) {
			result = false;
		}

		while (result && index < group.length) {
			//os valores comparados NUNCA podem ser nulos pois daria problema ao criar os agrupamentos
			if (!isValidValues(group[index], otherGroup[index])) {
				result = false;
				break;
			}
			if (group[index].equals(otherGroup[index])) {
				index++;
				continue;
			}
			result = false;
			break;
		}

		return result;
	}

	private static boolean isValidValues(final Object objectI, final Object objectF) {

		boolean result = true;

		if (objectI == null && objectF != null) {
			result = false;
		}
		if (objectI != null && objectF == null) {
			result = false;
		}
		if (objectI == null && objectF == null) {
			result = false;
		}
		return result;
	}
}