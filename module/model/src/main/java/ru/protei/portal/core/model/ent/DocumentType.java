package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Вид документа
 */
@JdbcEntity(table = "document_type")
public class DocumentType implements Serializable, Removable {

    @JdbcId(idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn
    private String name;

    @JdbcColumn(name = "short_name")
    private String shortName;

    @JdbcColumn(name = "document_category")
    @JdbcEnumerated(EnumType.STRING)
    private En_DocumentCategory documentCategory;

    @JdbcColumn(name = "gost")
    private String gost;

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGost() {
        return gost;
    }

    public void setGost(String gost) {
        this.gost = gost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentType)) return false;
        DocumentType that = (DocumentType) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean isAllowedRemove() {
        return true;
    }
}
