package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBoxBase;
import ru.protei.portal.ui.common.client.events.InputEvent;

import java.util.function.Function;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

/**
 * Created by bondarenko on 08.11.16.
 */
abstract class ValidableTextBoxBase extends TextBoxBase implements HasValidable{

    ValidableTextBoxBase(Element elem){
        super(elem);
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
    public void setValue( String value ) {
        super.setValue( value );
        validateValue();
    }

    @Override
    public boolean isValid() {
        boolean isEmpty = !isNotNull && isEmpty(getValue());
        boolean isRegexMatched = regexp.test(getValue());
        boolean isValidByFunction = validationFunction == null || validationFunction.apply(getValue());
        return isEmpty || (isRegexMatched && isValidByFunction);
    }

    public void setPlaceholder(String placeholder ) {
        getElement().setAttribute("placeholder", placeholder);
    }

    public void setRegexp( String regexp ){
        this.regexp = RegExp.compile(regexp);
    }

    public void setValidationFunction(Function<String, Boolean> validationFunction) {
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

    private RegExp regexp = RegExp.compile( "\\S+" );
    private Function<String, Boolean> validationFunction;
    private boolean isNotNull = true;
    private static final String REQUIRED_STYLE_NAME="required";
}
