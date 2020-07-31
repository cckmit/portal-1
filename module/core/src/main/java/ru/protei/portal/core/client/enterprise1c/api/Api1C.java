package ru.protei.portal.core.client.enterprise1c.api;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.enterprise1c.dto.Contract1C;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.enterprise1c.dto.Country1C;

import java.util.List;

public interface Api1C {

    Result<List<Contractor1C>> getContractors(Contractor1C contractor1C, String homeCompanyName);

    Result<Contractor1C> saveContractor(Contractor1C contractor1C, String homeCompanyName);

    Result<List<Country1C>> getCountries(Country1C Country1C, String homeCompanyName);

    Result<List<Country1C>> getAllCountries(String homeCompanyName);

    Result<Boolean> isResident(Contractor1C contractor, String homeCompanyName);

    Result<Contract1C> saveContract(Contract1C contract1C, String homeCompanyName);

    Result<Contract1C> getContract(Contract1C contract1C, String homeCompanyName);
}
