package ru.protei.portal.ui.common.client.widget.selector.product.component;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.product.BaseModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class ComponentModel extends BaseModel {
    @Inject
    public void init() {
        query = new ProductQuery();
        query.addType(En_DevUnitType.COMPONENT);
        query.setState(En_DevUnitState.ACTIVE);
        query.setSortField(En_SortField.prod_name);
        query.setSortDir(En_SortDir.ASC);
    }
    @Override
    protected void refreshOptions(){
        if(requested) return;
        requested = true;
        productService.getProductViewList(query, new RequestCallback<List<ProductShortView>>() {
            @Override
            public void onError(Throwable throwable) {
                requested = false;
                failedToLoad();
            }

            @Override
            public void onSuccess(List<ProductShortView> result) {
                setOptions(result);
            }
        });
    }
    @Inject
    ProductControllerAsync productService;

    private boolean requested;
    private ProductQuery query;
}

