package ru.protei.portal.core.client.enterprise1c.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.enterprise1c.dto.Country1C;

import java.util.List;

public interface Api1C {

    Result<List<Contractor1C>> getContractor(Contractor1C contractor1C);

    Result<Contractor1C> saveContractor(Contractor1C contractor1C) throws JsonProcessingException;

    Result<List<Country1C>> getCountry(Country1C Country1C);

    Result<List<Country1C>> getCountryVocabulary();
}
