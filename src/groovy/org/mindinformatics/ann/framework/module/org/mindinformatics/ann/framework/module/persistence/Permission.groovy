package org.mindinformatics.ann.framework.module.org.mindinformatics.ann.framework.module.persistence

/**
 * Created by jmiranda on 3/11/16.
 */
public enum Permission {

    READ('read'),
    UPDATE('update'),
    DELETE('delete'),
    ADMIN('admin')

    final String id

    Permission(String id) { this.id = id }

    static Permission findById(String id){
        for(Permission permission : values()){
            if( permission.id.equals(id)){
                return permission;
            }
        }
        return null;
    }

    static list() {
        [READ, UPDATE, DELETE, ADMIN]
    }
}

