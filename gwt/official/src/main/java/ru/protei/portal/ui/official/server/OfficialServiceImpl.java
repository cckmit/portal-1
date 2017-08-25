package ru.protei.portal.ui.official.server;

import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.service.OfficialService;

import java.util.*;

/**
 * Created by serebryakov on 22/08/17.
 */
@Service( "OfficialService" )
public class OfficialServiceImpl implements OfficialService {

    @Override
    public List<Official> getOfficialList() {
        List<Official> officials = new ArrayList<>();
        Official official1 = new Official();
        official1.setCreated(new Date(2017, 12, 17));
        official1.setProductName("SORM");
        official1.setEmployeesNumber("12");
        official1.setInfo("Разыскные мероприятие");
        official1.setAttachmentExists(true);
        official1.setRegion(new EntityOption("Нижегородская область", 1l));

        Official official2 = new Official();
        official2.setCreated(new Date(2016, 05, 11));
        official2.setProductName("DPI");
        official2.setEmployeesNumber("55");
        official2.setInfo("Анализ трафика");
        official2.setAttachmentExists(true);
        official1.setRegion(new EntityOption("Камчатский край", 2l));

        officials.add(official1);
        officials.add(official2);

        return officials;
    }

    @Override
    public Official getOfficial(long id) {
        for (Official official: officialList) {
            if (official.getId() == id) {
                return official;
            }
        }
        return null;
    }

    @Override
    public Long getOfficialCount() {
        return null;
    }

    @Override
    public Map<String, List<Official>> getOfficialsByRegions() {
        List<Official> officials = new ArrayList<>();
        Official official1 = new Official();
        official1.setCreated(new Date(2017, 12, 17));
        official1.setProductName("SORM");
        official1.setEmployeesNumber("12");
        official1.setInfo("Оперативно разыскные мероприятия проводимые республикой Казахстан при содействии сил внеземного происхождения");
        official1.setAttachmentExists(true);
        official1.setRegion(new EntityOption("Астраханская область", 1l));
        official1.setId(1l);

        Official official2 = new Official();
        official2.setCreated(new Date(2016, 05, 11));
        official2.setProductName("DPI");
        official2.setEmployeesNumber("55");
        official2.setInfo("Эффективные технологии анализа трафика");
        official2.setAttachmentExists(true);
        official2.setRegion(new EntityOption("Камчатский край", 2l));
        official2.setId(2l);

        officials.add(official1);
        officials.add(official2);
        officialList = officials;

        List<Official> firstList = new ArrayList<>();
        firstList.add(official1);

        List<Official> secondList = new ArrayList<>();
        secondList.add(official2);

        Map<String, List<Official>> officialsByRegions = new HashMap<>();
        officialsByRegions.put("Астраханская область", firstList);
        officialsByRegions.put("Камчатский край", secondList);

        return officialsByRegions;
    }

    private List<Official> officialList;
}
