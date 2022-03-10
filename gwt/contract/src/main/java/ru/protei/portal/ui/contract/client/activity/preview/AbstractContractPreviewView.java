package ru.protei.portal.ui.contract.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.contract.client.widget.contractspecification.previewitem.ContractSpecificationPreviewItem;

import java.util.List;

public interface AbstractContractPreviewView extends IsWidget {
    void setActivity(AbstractContractPreviewActivity activity);

    HasWidgets getCommentsContainer();

    HasWidgets getTagsContainer();

    void setHeader(String value);

    void setState(String value);

    void setDateSigning(String value);

    void setDateValid(String value);

    void setDateExecution(String value);

    void setDateEndWarranty(String value);

    void setDescription(String value);

    void setDirections(String value);

    void setContractor(String value);

    void setCurator(String value);

    void setDeliveryNumber(String value);

    void setProjectManager(String value);

    void setContractSignManager(String value);

    void setDates(List<Widget> value);

    void setSpecifications(List<ContractSpecificationPreviewItem> value);

    void setOrganization(String value);

    void setParentContract(String value);

    void setChildContracts(String value);

    void setProject(String value, String link);

    HasVisibility footerVisibility();

    void isFullScreen(boolean isFullScreen);
}
