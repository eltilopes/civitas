package br.com.civitas.arquitetura.util;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import br.com.civitas.arquitetura.entity.Entidade;

@SuppressWarnings({ "unchecked", "rawtypes" })

public class ReflectionUtil {

	public static String getFieldNameID(Class clazz) {
		if (clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class)) {
					field.setAccessible(true);
					try {
						return field.getName();
					} catch (Exception e) {
						throw new IllegalArgumentException("Atributo não encontrado");
					}
				}
			}
		}

		if (clazz.getSuperclass() != Object.class) {
			return getFieldNameID(clazz.getSuperclass());
		}

		throw new IllegalArgumentException("Atributo não encontrado");
	}

	public static Object getValueField(Class clazz, Object object,
			String fieldName) {
		if (clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(fieldName)) {
					field.setAccessible(true);
					try {
						return field.get(object);
					} catch (Exception e) {
						throw new IllegalArgumentException("Atributo não encontrado");
					}
				}
			}
		}

		if (clazz.getSuperclass() != Object.class) {
			return getValueField(clazz.getSuperclass(), object, fieldName);
		}

		throw new IllegalArgumentException("Atributo não encontrado");
	}

	public static <Entity extends Entidade> Serializable valueId(Entity entidade) {

		Class<Entity> classe = (Class<Entity>) entidade.getClass();

		Field[] fields = classe.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)
					|| field.isAnnotationPresent(EmbeddedId.class)) {

				field.setAccessible(true);

				try {
					return (Serializable) field.get(entidade);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}
		return null;
	}

	public static <E extends Entidade> String getNameFieldId(E entidade) {

		Class<E> classe = (Class<E>) entidade.getClass();

		Set<String> set = entidade.notEmptyFields().keySet();
		for (String field : set) {
			try {
				Field f = classe.getDeclaredField(field);

				if (f.isAnnotationPresent(Id.class)
						|| f.isAnnotationPresent(EmbeddedId.class))
					return field;
			} catch (SecurityException e) {
			} catch (NoSuchFieldException e) {
			}
		}

		return null;
	}
}
