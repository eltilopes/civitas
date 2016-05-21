package br.com.civitas.arquitetura.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsGrouper {

	private transient MapsGrouperListener listener;

	private transient List<Group> groups;

	private transient Map<Group, List<?>> map;

	private transient Field[] fields;

	private transient List<?> originalList;

	private transient Map<Group, Group> createdGroups;

	private static Field[] loadFields(final Class<?> clazz, final String... fieldsName) {
		final Field[] fields = new Field[fieldsName.length];
		try {
			int index = 0;
			for (final String name : fieldsName) {
				fields[index] = clazz.getDeclaredField(name);
				index++;
			}
		} catch (final SecurityException e) {
			throw new GroupingException(e);
		} catch (final NoSuchFieldException e) {
			throw new GroupingException(e);
		}
		return fields;
	}

	public static MapsGrouper create() {
		return new MapsGrouper();
	}

	private Group createGroup(final Object object, final Field... fields) {
		final Object[] list = new Object[fields.length];
		Group group = null;

		try {
			int index = 0;
			for (final Field f : fields) {
				f.setAccessible(true);
				list[index] = f.get(object);
				f.setAccessible(false);
				index++;
			}
		} catch (final RuntimeException e) {
			throw new GroupingException(e);
		} catch (final IllegalAccessException e) {
			throw new GroupingException(e);
		}

		final Group keyGroup = Group.create(list);
		group = createdGroups.get(keyGroup);
		if (group == null) {
			group = Group.create(list, groups.size() + 1);
			createdGroups.put(keyGroup, group);
		}

		return group;
	}

	private void callListener(final Object[] group, final List<?> list) {
		if (listener != null) {
			listener.valueMapped(group, list);
		}
	}

	private void init(final MapsGrouperListener arg0, final Field[] arg1, final List<?> arg2) {
		listener = arg0;
		fields = arg1;
		originalList = arg2;
		groups = new ArrayList<Group>();
		map = new HashMap<Group, List<?>>();
		createdGroups = new HashMap<Group, Group>();
	}

	public MapsGrouper start(final List<?> originalList, final Class<?> clazz, final String... fieldsName) {
		init(null, loadFields(clazz, fieldsName), originalList);
		return executeGroupment();
	}

	public MapsGrouper start(final MapsGrouperListener listener, final List<?> originalList, final Class<?> clazz, final String... fieldsName) {
		init(listener, loadFields(clazz, fieldsName), originalList);
		return executeGroupment();
	}

	private MapsGrouper executeGroupment() {
		int indexA = 0;
		int indexB = 1;

		//ira garantir que o loop ira processar um numero par de arrecadacoes
		if (originalList.size() % 2 != 0) {
			putIndexValueInGroup(indexA);
			indexA++;
			indexB++;
		}

		while (indexB < originalList.size()) {
			putIndexValueInGroup(indexA);
			putIndexValueInGroup(indexB);
			indexA += 2;
			indexB += 2;
		}

		return this;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void putIndexValueInGroup(final int indexList) {
		final Group group = createGroup(originalList.get(indexList), fields);
		final List list = getMappedList(group);
		final Object object = originalList.get(indexList);

		list.add(object);
		callListener(group.getGroupment(), list);
	}

	private List<?> getMappedList(final Group group) {
		List<?> mappedList = map.get(group);
		if (mappedList == null) {
			mappedList = new ArrayList<Object>();
			map.put(group, mappedList);
			groups.add(group);
		}
		return mappedList;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public List<?> getGroupment(final Group group) {
		return map.get(group);
	}

}
