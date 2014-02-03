package org.zols.swagger;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.mangofactory.swagger.EndpointComparator;
import com.wordnik.swagger.core.DocumentationEndPoint;

@Component
public class NameEndPointComparator implements EndpointComparator, Serializable {
    
	private static final long serialVersionUID = 4617938503626112452L;

	@Override
    public int compare(DocumentationEndPoint first, DocumentationEndPoint second) {
        return first.getPath().compareTo(second.getPath());
    }
}
