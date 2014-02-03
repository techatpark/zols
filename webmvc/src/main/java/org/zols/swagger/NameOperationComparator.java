package org.zols.swagger;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.mangofactory.swagger.OperationComparator;
import com.wordnik.swagger.core.DocumentationOperation;

@Component
public class NameOperationComparator implements OperationComparator, Serializable {
    
	private static final long serialVersionUID = -8021586771596910344L;

	@Override
    public int compare(DocumentationOperation first, DocumentationOperation second) {
        return first.getNickname().compareTo(second.getNickname());
    }
}
