package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ApplyEvent<T> extends GwtEvent<ApplyHandler<T>> {

  private static Type<ApplyHandler<?>> TYPE;

  public static <T> void fire(HasApplyHandlers<T> source, T target) {
    if (TYPE != null) {
      ApplyEvent<T> event = new ApplyEvent<T>(target);
      source.fireEvent(event);
    }
  }

  public static Type<ApplyHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new Type<ApplyHandler<?>>();
    }
    return TYPE;
  }

  private final T target;

  protected ApplyEvent(T target) {
    this.target = target;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final Type<ApplyHandler<T>> getAssociatedType() {
    return (Type) TYPE;
  }

  public T getTarget() {
    return target;
  }

  @Override
  protected void dispatch(ApplyHandler<T> handler) {
    handler.onApply(this);
  }
}
