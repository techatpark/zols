/**
 * package.info.
 */

package org.zols.starter.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {

    /**
     * declares variable id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * declares variable name.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    /**
     * Role.
     *
     */
    public Role() {

    }

    /**
     * Instantiates a new Role.
     *
     * @param aName a name
     */
    public Role(final ERole aName) {
        this.name = aName;
    }

    /**
     * gets the id.
     *
     * @return id
     */
    public Integer getId() {
        return id;
    }
    /**
     * Sets id.
     *
     * @param anId an id
     */
    public void setId(final Integer anId) {
        this.id = anId;
    }
    /**
     * gets the name.
     *
     * @return name
     */
    public ERole getName() {
        return name;
    }
    /**
     * Sets name.
     *
     * @param aName a name
     */
    public void setName(final ERole aName) {
        this.name = aName;
    }
}