package ru.protei.portal.ui.common.client.popup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;


/**
 * Базовый попап c реакцией на прокрутку и изменение размеров окна
 */
public abstract class BasePopupView
        extends PopupPanel
        implements ResizeHandler, Window.ScrollHandler
{

    public void show(UIObject relative, Integer width ) {
        switch (popupPosition) {

            case BY_RIGHT_SIDE:
                showUnderRight( relative, width );
                break;
            case BY_LEFT_SIDE:
                showUnderLeft( relative, width );
                break;
            case CENTER:
                showUnderCenter( relative, width );
                break;
            case BY_RIGHT_SIDE_OVER:
                showOverRight( relative, width );
                break;
            case BY_LEFT_SIDE_OVER:
                showOverLeft( relative, width );
                break;
            case CENTER_OVER:
                showOverCenter( relative, width );
                break;
            case FREE:
            default:
                showNear( relative );
                break;
        }
    }

    public void setPosition( Position popupPosition ) {
        this.popupPosition = popupPosition;
    }

    public enum Position {
        FREE,
        BY_RIGHT_SIDE,
        BY_LEFT_SIDE,
        CENTER,
        BY_RIGHT_SIDE_OVER,
        BY_LEFT_SIDE_OVER,
        CENTER_OVER,
    }

    @Override
    public void onResize( ResizeEvent event ) {
        toPosition();
    }

    @Override
    public void onWindowScroll( Window.ScrollEvent event ) {
        toPosition();
    }

    public void showNear( UIObject view ) {
        relative = view;
        showRelativeTo( relative );
        getPositionRoot().getElement().getStyle().setPosition( Style.Position.RELATIVE );
        getPositionRoot().getElement().getStyle().setDisplay( Style.Display.BLOCK );
        getPositionRoot().getElement().getStyle().setWidth( relative.getElement().getParentElement().getOffsetWidth(), Style.Unit.PX );
    }

    /**
     * Правый край попапа по правому краю соотносительного
     */
    public void showUnderRight(final UIObject relative, Integer width ) {
        this.relativeRight = relative;
        this.width = width;

        getPositionRoot().getElement().getStyle().setPosition( Style.Position.RELATIVE );
        getPositionRoot().getElement().getStyle().setDisplay( Style.Display.BLOCK );
        if ( width != null ) {
            getPositionRoot().getElement().getStyle().setWidth( width, Style.Unit.PX );
        }

        setPopupPositionAndShow( new PositionCallback() {
            @Override
            public void setPosition( int popupWidth, int popupHeight ) {
                int relativeLeft = relative.getAbsoluteLeft();
                int widthDiff = popupWidth - relative.getOffsetWidth();
                int popupLeft = relativeLeft - widthDiff;
                int relativeTop = relative.getAbsoluteTop();
                int popupTop = relativeTop + relative.getOffsetHeight();

                //  Arrow
                Element arrow = getArrow();
                if ( arrow != null ) {
                    arrow.getStyle().setMarginLeft( (popupWidth - relative.getOffsetWidth() / 2) - arrow.getOffsetWidth(), Style.Unit.PX );
                }

                setPopupPosition( popupLeft, popupTop );
            }
        } );
    }

    public void showOverRight(final UIObject relative, Integer width ) {
        this.relativeOverLeft = relative;
        this.width = width;

        getPositionRoot().getElement().getStyle().setPosition( Style.Position.RELATIVE );
        getPositionRoot().getElement().getStyle().setDisplay( Style.Display.BLOCK );
        if ( width != null ) {
            getPositionRoot().getElement().getStyle().setWidth( width, Style.Unit.PX );
        }

        setPopupPositionAndShow( new PositionCallback() {
            @Override
            public void setPosition( int popupWidth, int popupHeight ) {
                int relativeLeft = relative.getAbsoluteLeft();
                int widthDiff = popupWidth - relative.getOffsetWidth();
                int popupLeft = relativeLeft - widthDiff;

                int relativeTop = relative.getAbsoluteTop();
                int popupTop = relativeTop - getOffsetHeight();

                setPopupPosition( popupLeft, popupTop );
            }
        } );
    }

    /**
     * Левый край попапа по левому краю соотносительного
     */
    public void showUnderLeft(final UIObject relative, Integer width ) {
        this.relativeLeft = relative;
        this.width = width;

        getPositionRoot().getElement().getStyle().setPosition( Style.Position.RELATIVE );
        getPositionRoot().getElement().getStyle().setDisplay( Style.Display.BLOCK );
        if ( width != null ) {
            getPositionRoot().getElement().getStyle().setWidth( width, Style.Unit.PX );
        }

        setPopupPositionAndShow( new PositionCallback() {
            @Override
            public void setPosition( int popupWidth, int popupHeight ) {
                int popupLeft = relative.getAbsoluteLeft();

                int relativeTop = relative.getAbsoluteTop();
                int popupTop = relativeTop + relative.getOffsetHeight();

                setPopupPosition( popupLeft, popupTop );
            }
        } );
    }

    public void showOverLeft(final UIObject relative, Integer width ) {
        this.relativeOverLeft = relative;
        this.width = width;

        getPositionRoot().getElement().getStyle().setPosition( Style.Position.RELATIVE );
        getPositionRoot().getElement().getStyle().setDisplay( Style.Display.BLOCK );
        if ( width != null ) {
            getPositionRoot().getElement().getStyle().setWidth( width, Style.Unit.PX );
        }

        setPopupPositionAndShow( new PositionCallback() {
            @Override
            public void setPosition( int popupWidth, int popupHeight ) {
                int popupLeft = relative.getAbsoluteLeft();

                int relativeTop = relative.getAbsoluteTop();
                int popupTop = relativeTop - getOffsetHeight();

                setPopupPosition( popupLeft, popupTop );
            }
        } );
    }

    public void showOverCenter(final UIObject relative, Integer width ) {
        this.relativeOverCenter = relative;
        this.width = width;

        getPositionRoot().getElement().getStyle().setPosition( Style.Position.RELATIVE );
        getPositionRoot().getElement().getStyle().setDisplay( Style.Display.BLOCK );
        if ( width != null ) {
            getPositionRoot().getElement().getStyle().setWidth( width, Style.Unit.PX );
        }

        setPopupPositionAndShow( new PositionCallback() {
            @Override
            public void setPosition( int popupWidth, int popupHeight ) {
                int relativeLeft = relative.getAbsoluteLeft();
                int popupLeft = relativeLeft + relative.getOffsetWidth() / 2 - getOffsetWidth() / 2;

                int relativeTop = relative.getAbsoluteTop();
                int popupTop = relativeTop - getOffsetHeight();

                setPopupPosition( popupLeft, popupTop );
            }
        } );
    }

    public void showUnderCenter(final UIObject relative, Integer width ) {
        this.relativeUnderCenter = relative;
        this.width = width;

        getPositionRoot().getElement().getStyle().setPosition( Style.Position.RELATIVE );
        getPositionRoot().getElement().getStyle().setDisplay( Style.Display.BLOCK );
        if ( width != null ) {
            getPositionRoot().getElement().getStyle().setWidth( width, Style.Unit.PX );
        }

        setPopupPositionAndShow( new PositionCallback() {
            @Override
            public void setPosition( int popupWidth, int popupHeight ) {
                int relativeLeft = relative.getAbsoluteLeft();
                int popupLeft = relativeLeft + relative.getOffsetWidth() / 2 - getOffsetWidth() / 2;

                int relativeTop = relative.getAbsoluteTop();
                int popupTop = relativeTop + relative.getOffsetHeight();

                //  Arrow
                Element arrow = getArrow();
                if ( arrow != null ) {
                    arrow.getStyle().setMarginLeft( popupWidth / 2 - arrow.getOffsetWidth(), Style.Unit.PX );
                }

                setPopupPosition( popupLeft, popupTop );
            }
        } );
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        windowResizeHandlerRegistration = Window.addResizeHandler( this );
        scrollRegistration = Window.addWindowScrollHandler( this );
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if ( windowResizeHandlerRegistration != null ) {
            windowResizeHandlerRegistration.removeHandler();
        }
        if ( scrollRegistration != null ) {
            scrollRegistration.removeHandler();
        }
    }

    private void toPosition() {
        if ( relative != null ) {
            showNear( relative );
        }
        else if ( relativeLeft != null ) {
            showUnderLeft( relativeLeft, width );
        }
        else if ( relativeRight != null ) {
            showUnderRight( relativeRight, width );
        }
        else if ( relativeOverCenter != null ) {
            showOverCenter( relativeOverCenter, width );
        }
        else if ( relativeOverLeft != null ) {
            showOverLeft( relativeOverLeft, width );
        }
        else if ( relativeUnderCenter != null ) {
            showUnderCenter( relativeUnderCenter, width );
        }
    }

    protected Element getArrow() {
        return null;
    }

    /**
     * Корневой элемент для рассчета позиционирования окна
     */
    protected abstract UIObject getPositionRoot();

    /**
     * При ресайзе
     */
    protected UIObject relative;

    protected UIObject relativeOverLeft;

    protected UIObject relativeOverCenter;

    protected UIObject relativeLeft;

    protected UIObject relativeRight;

    protected UIObject relativeUnderCenter;

    private Position popupPosition = Position.FREE;

    private Integer width;

    private HandlerRegistration windowResizeHandlerRegistration;

    private HandlerRegistration scrollRegistration;


}