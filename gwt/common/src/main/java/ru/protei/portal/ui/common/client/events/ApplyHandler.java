package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface ApplyHandler<T> extends EventHandler {

  void onApply(ApplyEvent<T> event);
}
