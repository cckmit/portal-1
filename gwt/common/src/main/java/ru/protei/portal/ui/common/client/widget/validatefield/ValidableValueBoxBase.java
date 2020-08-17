package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ValueBoxBase;
import ru.protei.portal.ui.common.client.events.InputEvent;

import java.util.function.Function;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

abstract class ValidableValueBoxBase<T> extends ValueBoxBase<T> implements HasValidable {

    ValidableValueBoxBase(Element elem, Renderer<T> renderer, Parser<T> parser){
        super(elem, renderer, parser);
        addDomHandler(event -> validationTimer.schedule(200), InputEvent.getType());
    }

    @Override
    public void setValid(boolean isValid) {
        if(isValid)
            removeStyleName( REQUIRED_STYLE_NAME );
        else
            addStyleName( REQUIRED_STYLE_NAME );
    }

    @Override
    public void setValue( T value ) {
        super.setValue( value );
        validateValue();
    }

    @Override
    public boolean isValid() {
        T value = getValue();
        String valueString = getText();
        boolean isEmpty = !isNotNull && isEmpty(valueString);
        boolean isRegexMatched = regexp.test(valueString);
        boolean isValidByFunction = validationFunction == null || validationFunction.apply(value);
        return isEmpty || (isRegexMatched && isValidByFunction);
    }

    public void setPlaceholder(String placeholder ) {
        getElement().setAttribute("placeholder", placeholder);
    }

    public void setRegexp( String regexp ){
        this.regexp = RegExp.compile(regexp);
    }

    public void setValidationFunction(Function<T, Boolean> validationFunction) {
        this.validationFunction = validationFunction;
    }

    public void setNotNull( boolean value ) {
        this.isNotNull = value;
    }

    private void validateValue() {
        setValid(isValid());
    }

    Timer validationTimer = new Timer(){
        @Override
        public void run() {
            validateValue();
        }
    };

    protected RegExp regexp = RegExp.compile( "\\S+" );
    protected Function<T, Boolean> validationFunction;
    protected boolean isNotNull = true;
    private static final String REQUIRED_STYLE_NAME="required";
}
