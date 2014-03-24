package org.zols.datastore.domain;

public class Attribute {

    private String name;
    private String label;
    private String description;
    private String type;
    private boolean isReference;
    private boolean isArray;
    private boolean isUnique;
    private boolean isLocaleSpecific;
    private int index;

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isLocaleSpecific() {
        return isLocaleSpecific;
    }

    public void setLocaleSpecific(boolean isLocaleSpecific) {
        this.isLocaleSpecific = isLocaleSpecific;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isIsReference() {
        return isReference;
    }

    public void setIsReference(boolean isReference) {
        this.isReference = isReference;
    }
    
    

}
