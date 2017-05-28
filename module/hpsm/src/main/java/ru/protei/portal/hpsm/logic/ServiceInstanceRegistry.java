package ru.protei.portal.hpsm.logic;

import ru.protei.portal.core.model.ent.CaseObject;

import java.util.function.Consumer;

/**
 * Created by Mike on 17.05.2017.
 */
public interface ServiceInstanceRegistry {


    ServiceInstance get (String id);
    ServiceInstance find (CaseObject object);
    void each (Consumer<? super ServiceInstance> var1);

    void add (ServiceInstance instance);

}
