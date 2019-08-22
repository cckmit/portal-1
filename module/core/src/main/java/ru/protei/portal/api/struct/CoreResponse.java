package ru.protei.portal.api.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by michael on 27.06.16.
 */
@JsonAutoDetect
public class CoreResponse<T> {

    @JsonProperty
    private En_ResultStatus status;

    @JsonProperty
    private T data;

    @JsonProperty
    private String message;

    @JsonIgnore
    public boolean isOk () {
        return status == En_ResultStatus.OK;
    }

    @JsonIgnore
    public boolean isError () {
        return status != En_ResultStatus.OK;
    }

    public En_ResultStatus getStatus () {
        return status;
    }

    public T getData () {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    @JsonIgnore
    public CoreResponse<T> error (En_ResultStatus status) {
        this.status = status;
        return this;
    }
    @JsonIgnore
    public CoreResponse<T> error (En_ResultStatus status, String message) {
        this.status = status;
        this.message = message;
        return this;
    }
    @JsonIgnore
    public CoreResponse<T> redirect (String to) {
        return this;
    }

    @JsonIgnore
    public CoreResponse<T> success () {
        this.status = En_ResultStatus.OK;
        return this;
    }
    @JsonIgnore
    public CoreResponse<T> success (T data) {
        this.data = data;
        this.status = En_ResultStatus.OK;
        return this;
    }

    public static <T> CoreResponse<T> ok() {
        return new CoreResponse<T>().success();
    }

    public static <T> CoreResponse<T> ok(T result) {
        return new CoreResponse<T>().success(result);
    }

    public static <T> CoreResponse<T> errorSt(En_ResultStatus status) {
        return errorSt(status, null);
    }

    public static <T> CoreResponse<T> errorSt(En_ResultStatus status, String message) {
        return new CoreResponse<T>().error( status, message );
    }

    @Override
    public String toString() {
        return "CoreResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * Если результрат успешен
     */
    @JsonIgnore
    public CoreResponse<T> ifOk( Consumer<? super T> consumer ) {
        if (consumer != null && isOk()) {
            consumer.accept( data );
        }
        return this;
    }

    @JsonIgnore
    public CoreResponse<T> ifError( Consumer<CoreResponse<T>> consumer ) {
        if (consumer != null && !isOk()) {
            consumer.accept( this );
        }
        return this;
    }

    /**
     *  Поведение подобно Optional от CoreResponse<T>
     *      При получении ошибки, дальнейшие действия игнорируются
     *      ошибка продвигается к конечному результату
     */

    /**
     * Когда вызваемая функция возвращает не Result, а конкретное значение
     */
    @JsonIgnore
    public <U> CoreResponse<U> map( Function<? super T, ? extends U> mapper) {
        if (mapper == null || !isOk())
            return errorSt( status, message );
        else {
            return ok( mapper.apply( data ) );
        }
    }

    /**
     * Когда вызваемая функция возвращает Result
     */
    @JsonIgnore
    public <U> CoreResponse<U> flatMap(Function<? super T, CoreResponse<U>> mapper) {
        if (mapper == null || !isOk())
            return errorSt( status, message );
        else {
            return mapper.apply(data);
        }
    }

    /**
     * Антипаттерн, - перенос логики в цепочку!
     * Рекомендуется исользовать map с передачей данных в функцию с названием отражающим смысл фильтрации.
     */
    @Deprecated
    @JsonIgnore
    public CoreResponse<T> filter( Predicate<? super T> predicate) {
        if (predicate  == null || !isOk()) {
            return errorSt( status, message );
        }
        if (data == null) {
            return this;
        } else {
            return predicate.test(data) ? this : ok();
        }
    }

    /**
     * Антипаттерн, - перенос логики в цепочку!
     * Рекомендуется исользовать map с передачей данных в функцию с названием отражающим смысл проверки.
     *
     * Если результрат успешен и не null
     * расширяет метод map проверкой значения на null
     */
    @Deprecated
    @JsonIgnore
    public <U> CoreResponse<U> ifPresentOrElse​( Function<? super T, CoreResponse<U>> flatMapIfPresent,
                                                 Supplier<CoreResponse<U>> onNotPresent ) {
        if (flatMapIfPresent == null || !isOk()) {
            return errorSt( status, message );
        }
        if (data != null) {
            return flatMapIfPresent.apply( data );
        }
        if (onNotPresent == null) {
            return errorSt( status, message );
        }
        return onNotPresent.get();
    }

    /**
     * Если результрат не успешен
     * получить тот же результат выполнив другое действие
     */
    @JsonIgnore
    public CoreResponse<T> orElseGet( Supplier<CoreResponse<T>> supplier ) {
        if (supplier == null) {
            return errorSt( status, message );
        }
        if (!isOk()) {
            return supplier.get();
        }
        return this;
    }

    @JsonIgnore
    public <X extends Throwable> CoreResponse<T> orElseThrow( Function<CoreResponse<T>, ? extends X> exceptionSupplier ) throws X {
        if (!isOk()) {
            throw exceptionSupplier.apply( this );
        }
        return this;
    }
}
