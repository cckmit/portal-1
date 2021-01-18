package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

/**
 * Created by michael on 26.05.17.
 */
@JdbcEntity(table = "company_subscription")
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

    @JdbcJoinedColumn(localColumn = "platform_id", table = "platform", remoteColumn = "id", mappedColumn = "name")
    private String platformName;

    @JdbcJoinedColumn(localColumn = "dev_unit_id", table = "dev_unit", remoteColumn = "id", mappedColumn = "unit_name")
    private String productName;

    public CompanySubscription() {}

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

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private String uniqueKey () {
        return (this.email + "_" + this.companyId + "_" + this.productId + "_" + this.platformId);
    }

    public static boolean isProteiRecipient(String email) {
        return stream(CrmConstants.PROTEI_DOMAINS).anyMatch(email::endsWith);
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
