package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;

/**
 * Created by bondarenko on 11.11.16.
 */
public class PersonEvents {

    public static class ChangePersonModel {
        public Company company;

        ChangePersonModel(Company company){
            this.company = company;
        }
    }

    public static class PersonCreated {
        public Person person;
        public String origin;
        public PersonCreated(Person person) {
            this.person = person;
        }
        public PersonCreated(Person person, String origin) {
            this.person = person;
            this.origin = origin;
        }
    }
}
