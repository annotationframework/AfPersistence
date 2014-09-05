package org.mindinformatics.ann.framework.module.persistence

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ReflectionToStringBuilder

class Tag {

    String name

    Date dateCreated
    Date lastUpdated

    static belongsTo = Annotation
    static hasMany = [annotations : Annotation]


    static constraints = {
        name(blank:false, unique: true)
        annotations(display:false)
    }

    @Override
    public String toString() {

        return "${id}:${name}"
        //final ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this);
        //reflectionToStringBuilder.setAppendStatics(true);
        //reflectionToStringBuilder.setAppendTransients(true);
        //reflectionToStringBuilder.setExcludeFieldNames(["contentStreamLength", "password"]);
        //return reflectionToStringBuilder.toString();

    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder()
                .append(name).toHashCode();
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Tag){
            final Tag other = (Tag) obj;
            return new EqualsBuilder().append(name, other.name).isEquals();
        } else {
            return false;
        }

    }
}
