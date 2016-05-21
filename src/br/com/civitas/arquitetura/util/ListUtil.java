package br.com.civitas.arquitetura.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {

	private List<Object> list;
	private List<Object> filteredList;
	private Set<Object> removedItemsFromFilteredList;
	
	@SuppressWarnings({ "unchecked" })
	public ListUtil(List<? extends Object> list) {
		this.list = (List<Object>) list;
		this.filteredList = new ArrayList<Object>();
		this.removedItemsFromFilteredList = new HashSet<Object>();
	}
	
	public List<?> filterListElementsByField(String fieldName, Object filterValue) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		
		filteredList = new ArrayList<Object>();
		
		for(Object listItem: list){
			Field field = listItem.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Object fieldValue = field.get(listItem);
			
			if(filterValue == null){
				filteredList.add(listItem);
			}else if(fieldValue instanceof String && ((String)fieldValue).toUpperCase().contains(((String) filterValue).toUpperCase())){
				filteredList.add(listItem);
			}else if(fieldValue.equals(filterValue)){
				filteredList.add(listItem);
			}
		}
		
		filteredList.removeAll(removedItemsFromFilteredList);
		removedItemsFromFilteredList = new HashSet<Object>();
		return filteredList;
	}

	@SuppressWarnings("unchecked")
	public void setList(List<? extends Object> list) {
		this.list = (List<Object>) list;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getList() {
		return (List<T>) list;
	}
	
	public void setFilteredList(List<Object> filteredList){
		this.filteredList.addAll(filteredList);
	}
	
	public List<Object> getFilteredList() {
		return filteredList;
	}
	
	public void removeItemsFromFilteredList(List<?> removedItems){
		this.removedItemsFromFilteredList.addAll(removedItems);
	}
}
