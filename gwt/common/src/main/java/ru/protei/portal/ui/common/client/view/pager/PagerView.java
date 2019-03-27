package ru.protei.portal.ui.common.client.view.pager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Created by shagaleev on 29/11/16.
 */
public class PagerView extends Composite implements AbstractPagerView {

    public PagerView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractPagerActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCurrentPage(int value) {
        currentPage = value;
        updateButtons();
        updateLabel();
    }

    @Override
    public void setTotalCount(long value) {
        totalCount = value;
        updateLabel();
    }

    @Override
    public void setTotalPages(int value) {
        totalPages = value;
        updateButtons();
        updateLabel();
    }

    @UiHandler("fastBackward")
    public void onFastBackwardClicked(ClickEvent event) {
        onPageSelected(0);
    }

    @UiHandler("fastForward")
    public void onFastForwardClicked(ClickEvent event) {
        onPageSelected(totalPages);
    }

    @UiHandler("backward")
    public void backwardClick(ClickEvent event) {
        onPageSelected(currentPage - 1);
    }

    @UiHandler("forward")
    public void forwardClick(ClickEvent event) {
        onPageSelected(currentPage + 1);
    }

    private void onPageSelected(int page) {
        if (page < 0 || page > totalPages || activity == null) {
            return;
        }
        activity.onPageSelected(page);
    }

    private void updateLabel() {
        label.setInnerText(lang.pagerLabel(currentPage + 1, totalPages, totalCount));
    }

    private void updateButtons() {
        toggleArrowButtonsEnabled();
        redrawPageButtons();
    }

    private void toggleArrowButtonsEnabled() {
        boolean isBackwardEnabled = currentPage > 0;
        boolean isForwardEnabled = currentPage < totalPages - 1;
        fastBackward.setEnabled(isBackwardEnabled);
        backward.setEnabled(isBackwardEnabled);
        forward.setEnabled(isForwardEnabled);
        fastForward.setEnabled(isForwardEnabled);
    }

    private void redrawPageButtons() {
        pagesContainer.clear();
        int from = Math.max(0, currentPage - PAGE_BUTTON_COUNT / 2);
        int to = Math.min(from + PAGE_BUTTON_COUNT, totalPages);
        if (to - from < PAGE_BUTTON_COUNT) {
            from = Math.min(from, to - PAGE_BUTTON_COUNT);
            from = Math.max(from, 0);
        }
        for (int page = from; page < to; page++) {
            Button button = makePageButton(page, page == currentPage);
            pagesContainer.add(button);
        }
    }

    private Button makePageButton(int page, boolean isSelected) {
        Button button = buttonProvider.get();
        button.setStyleName("btn m-r-5");
        button.addStyleName(isSelected ? "btn-primary" : "btn-white");
        button.setText(String.valueOf(page + 1));
        button.addClickHandler(event -> onPageSelected(page));
        return button;
    }

    @UiField
    DivElement label;
    @UiField
    Button fastBackward;
    @UiField
    Button backward;
    @UiField
    Button forward;
    @UiField
    Button fastForward;
    @UiField
    HTMLPanel pagesContainer;

    @Inject
    Lang lang;
    @Inject
    Provider<Button> buttonProvider;

    private AbstractPagerActivity activity;
    private int currentPage = 0;
    private int totalPages = 0;
    private long totalCount = 0;

    private static final int PAGE_BUTTON_COUNT = 5;

    interface PagerUiBinder extends UiBinder<HTMLPanel, PagerView> {}
    private static PagerUiBinder ourUiBinder = GWT.create(PagerUiBinder.class);
}