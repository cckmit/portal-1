package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Вид документа
 */
@JdbcEntity(table = "document_type")
public class DocumentType implements Serializable {

    @JdbcId(idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn
    private String name;

    @JdbcColumn(name = "document_category")
    @JdbcEnumerated(EnumType.STRING)
    private En_DocumentCategory documentCategory;

    public DocumentType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public En_DocumentCategory getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(En_DocumentCategory documentCategory) {
        this.documentCategory = documentCategory;
    }
}
