package org.zols.datastore.model;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public class ZolsPage<T> implements Page<T> {

	private int number;
	private int size;

	private long totalElements;
	private List<T> content;

	public ZolsPage(int number, int size, long totalElements, List<T> content) {
		super();
		this.number = number;
		this.size = size;
		this.totalElements = totalElements;
		this.content = content;
	}

	public int getNumber() {
		return number;
	}

	public int getSize() {
		return size;
	}

	public int getTotalPages() {
		return 0;
	}

	public int getNumberOfElements() {
		return content.size();
	}

	public long getTotalElements() {
		return totalElements;
	}

	public List<T> getContent() {
		return content;
	}

	@Override
	public boolean hasPreviousPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFirstPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasNextPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLastPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasContent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Sort getSort() {
		// TODO Auto-generated method stub
		return null;
	}

}
