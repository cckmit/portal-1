package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

@JdbcEntity(table = "contract_sla")
public class ContractSLA {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name = "importance_level_id")
    private Integer importanceLevelId;

    @JdbcColumn(name = "time")
    private Long time;

    @JdbcColumn(name = "contract_id")
    private Long contractId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getImportanceLevelId() {
        return importanceLevelId;
    }

    public void setImportanceLevelId(Integer importanceLevelId) {
        this.importanceLevelId = importanceLevelId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    @Override
    public String toString() {
        return "ContractSLA{" +
                "id=" + id +
                ", importanceLevelId=" + importanceLevelId +
                ", time=" + time +
                ", contractId=" + contractId +
                '}';
    }
}
