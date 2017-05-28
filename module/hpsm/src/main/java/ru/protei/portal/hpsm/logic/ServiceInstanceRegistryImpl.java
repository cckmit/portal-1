package ru.protei.portal.hpsm.logic;

import ru.protei.portal.core.model.ent.CaseObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Mike on 17.05.2017.
 */
public class ServiceInstanceRegistryImpl implements ServiceInstanceRegistry {

    private Map<String, ServiceInstance> serviceInstanceMap;

    public ServiceInstanceRegistryImpl() {
        this(Collections.emptyList());
    }

    public ServiceInstanceRegistryImpl (List<ServiceInstance> list) {
        serviceInstanceMap = new HashMap<>();
        list.forEach(s -> add(s));
    }

    @Override
    public ServiceInstance get(String id) {
        return serviceInstanceMap.get(id);
    }

    @Override
    public ServiceInstance find(CaseObject object) {
        for (ServiceInstance instance : serviceInstanceMap.values()) {
            if (instance.acceptCase(object))
                return instance;
        }
        return null;
    }

    @Override
    public void each(Consumer<? super ServiceInstance> var1) {
        serviceInstanceMap.values().forEach(var1);
    }

    @Override
    public void add(ServiceInstance instance) {
        this.serviceInstanceMap.put(instance.id(), instance);
    }
}
