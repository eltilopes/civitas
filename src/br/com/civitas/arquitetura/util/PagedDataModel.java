package br.com.civitas.arquitetura.util;

import java.util.List;

import javax.faces.model.DataModel;

@SuppressWarnings("rawtypes")
public class PagedDataModel extends DataModel {

	private int rowIndex = -1;
	private int totalNumRows;
	private int pageSize;
	
	private List<?> list;

	public PagedDataModel(List<?> list, int totalNumRows, int pageSize) {
		super();
		setWrappedData(list);
		this.totalNumRows = totalNumRows;
		this.pageSize = pageSize;
	}

	@Override
	public boolean isRowAvailable() {
		if (list == null){
			return false;
		}

		int rowIndex = getRowIndex();
		return  (rowIndex >= 0 && rowIndex < list.size());
	}

	@Override
	public int getRowCount() {
		return totalNumRows;
	}

	@Override
	public Object getRowData() {
		if (list == null){
			return null;
		} else if (!isRowAvailable()) {
			throw new IllegalArgumentException();
		} else {
			int dataIndex = getRowIndex();
			return list.get(dataIndex);
		}
	}

	@Override
	public int getRowIndex() {
		try {
			return (rowIndex % pageSize);
		} catch (ArithmeticException e) {
			return 0;
		}
	}

	@Override
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	@Override
	public Object getWrappedData() {
		return list;
	}

	@Override
	public void setWrappedData(Object list) {
		this.list = (List<?>) list;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalNumRows() {
		return totalNumRows;
	}
	
}
