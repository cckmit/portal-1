package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by michael on 04.04.16.
 */
public class MigratePersonAction implements MigrateAction {

    private static Pattern PERSON_PROP = Pattern.compile("!begin!([^=]+)=(.*?)!end!;?", Pattern.DOTALL);

    @Autowired
    private PersonDAO dao;

    @Override
    public int orderOfExec() {
        return 1;
    }

    private static Map<String, String> splitProps(String props) {
        if (props == null)
            return Collections.emptyMap();

        Map<String, String> rez = new HashMap<>();

        Matcher m = PERSON_PROP.matcher(props);

        while (m.find()) {
            rez.put(m.group(1), m.group(2));
        }

        return rez;
    }

    private static String nvl(String x, String d) {
        return x != null ? x : d;
    }


    private String makeSql() {
        return " select p.*, p.strClient||'@'||p.strClientIP strCreatorID, \"resource\".func_getfullfio (p.strLastName,p.strFirstName,p.strPatronymic) fullFio," +
                " prop.properties, cat.nCategoryID,cd.strValue category from \"resource\".tm_person p" +
                " left outer join " +
                "        (   " +
                "               select nPersonID, LIST('!begin!'||pp.strValue||' '||c.strValue||'='||p.strValue||'!end!',';') properties " +
                "               from \"resource\".tm_person2property p " +
                "               join \"resource\".tm_category c on (p.nCategoryID=c.nID) " +
                "               join \"resource\".tm_personproperty pp on (pp.nID=p.nPropertyID) " +
                "               group by nPersonID " +
                "        ) prop on (prop.nPersonID=p.nID)" +
                " left outer join \"resource\".tm_person2category cat on (cat.nPersonID=p.nID) " +
                " left outer join \"resource\".tm_category cd on (cd.nID=cat.nCategoryID) " +
                " order by p.nID ";
    }


    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

        new BatchProcessTask<Person>(makeSql())
                .process(src, dao, row -> {
                        Person x = new Person();
                        x.setId(((Number) row.get("nID")).longValue());
                        x.setBirthday((Date) row.get("dtBirthday"));

                        if (row.get("nCompanyID") == null) {
                            System.out.println("company is null for: " + row);
                            x.setCompanyId(-1L);
                        } else
                            x.setCompanyId(((Number) row.get("nCompanyID")).longValue());

                        x.setCreated((Date) row.get("dtCreation"));


                        x.setCreator((String) row.get("strCreatorID"));

                        x.setDisplayName((String) row.get("fullFio"));
                        x.setFirstName((String) row.get("strFirstName"));
                        if (x.getFirstName() == null)
                            x.setFirstName("-");

                        x.setLastName((String) row.get("strLastName"));
                        if (x.getLastName() == null)
                            x.setLastName("-");

                        x.setSecondName((String) row.get("strPatronymic"));

                        x.setInfo((String) row.get("strInfo"));
                        x.setDeleted(row.get("lDeleted") != null && ((Number) row.get("lDeleted")).intValue() != 0);
                        x.setPassportInfo((String) row.get("strPassportInfo"));
                        x.setSex(row.get("nSexID") == null ? "-" : ((Number) row.get("nSexID")).intValue() == 1 ? "M" : "F");

                        x.setPosition(nvl((String) row.get("strPosition"), (String) row.get("category")));

/*            if (row.get("nCategoryID") != null) {
                Tm_PersonRole role = commonMapper.getPersonRole(row.get("category").toString());
                if (role == null)
                {
                    role = new Tm_PersonRole();
                    role.setRoleName(row.get("category").toString());
                    commonMapper.insertRole(role);
                    System.out.println("create new role:" + role.getRoleName() + ", id=" + role.getId());
                }
                x.setWorkRoleID(role.getId());
            }*/

                        if (row.get("properties") != null) {
                            System.out.println("properties: " + row.get("properties"));

                            Map<String, String> xp = splitProps((String) row.get("properties"));
                            x.setAddress(nvl(xp.get("Адрес рабочий"), xp.get("Адрес без категории")));
                            x.setAddressHome(xp.get("Адрес домашний"));

                            x.setEmail(nvl(xp.get("E-mail рабочий"), xp.get("E-mail без категории")));
                            x.setEmail_own(xp.get("E-mail домашний"));

                            x.setFax(nvl(xp.get("Факс рабочий"), xp.get("Факс без категории")));
                            x.setFaxHome(xp.get("Факс домашний"));

                            x.setWorkPhone(nvl(xp.get("Телефон рабочий"), xp.get("Телефон без категории")));
                            x.setMobilePhone(xp.get("Телефон мобильный"));
                            x.setHomePhone(xp.get("Телефон домашний"));

                            x.setJabber(nvl(xp.get("Интернет рабочий"), nvl(xp.get("Интернет без категории"), xp.get("Интернет домашний"))));
                            x.setIcq(nvl(xp.get("ICQ рабочий"), nvl(xp.get("ICQ без категории"), xp.get("ICQ домашний"))));
                        }


                        System.out.println(x.getId() + "/" + x.getDisplayName());
                        return x;
                });
    }
}
