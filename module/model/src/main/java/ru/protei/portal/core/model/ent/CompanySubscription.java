package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

/**
 * Created by michael on 26.05.17.
 */
@JdbcEntity(table = "CompanySubscription")
public class CompanySubscription implements Serializable {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "company_id")
    private Long companyId;

    @JdbcColumn(name = "email_addr")
    private String email;

    @JdbcColumn(name = "lang_code")
    private String langCode;

    @JdbcColumn(name = "platform_id")
    private Long platformId;

    @JdbcColumn(name = "dev_unit_id")
    private Long productId;


    public CompanySubscription() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String uniqueKey () {
        return (this.email + "_" + String.valueOf(this.companyId));
    }

    public static boolean isProteiRecipient(String email){
        return email.endsWith("@protei.ru");
    }

    @Override
    public int hashCode() {
        return uniqueKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompanySubscription && ((CompanySubscription) obj).uniqueKey().equals(uniqueKey());
    }
}
