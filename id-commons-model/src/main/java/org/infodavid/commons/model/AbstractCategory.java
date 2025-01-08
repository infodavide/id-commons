package org.infodavid.commons.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class AbstractCategory.
 */
@Access(AccessType.FIELD)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = true, of = { "name", "parentId" })
public class AbstractCategory extends AbstractEntity<Long> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8659011020144196759L;

    /** The description. */
    @Size(min = 0, max = 512)
    @Column(name = "description")
    private String description;

    /** The name. */
    @Size(min = 0, max = 48)
    @Column(name = "name")
    private String name;

    /** The parent. */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * Instantiates a new category.
     * @param source the source
     */
    public AbstractCategory(final AbstractCategory source) {
        super(source);
        name = source.name;
        parentId = source.parentId;
    }
}
