package ru.protei.portal.api.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.util.function.Consumer;
import java.util.function.Function;

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

    public CoreResponse( En_ResultStatus status, T data, String message ) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    @JsonIgnore
    public static <T> CoreResponse<T> error( En_ResultStatus status ) {
        return error( status, null);
    }
    @JsonIgnore
    public static <T> CoreResponse<T> error( En_ResultStatus status, String message ) {
        return new CoreResponse<T>( status, null, message);
    }

    @JsonIgnore
    public CoreResponse<T> redirect (String to) {
        return this;
    }

    @JsonIgnore
    public static <T> CoreResponse<T> ok() {
        return ok(null);
    }

    @JsonIgnore
    public static <T> CoreResponse<T> ok( T data ) {
        return new CoreResponse<T>( En_ResultStatus.OK, data, null);
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
     *  Поведение подобно Optional от CoreResponse<T> на основании En_ResultStatus вместо проверки на null
     *      При получении ошибки, дальнейшие действия игнорируются
     *      ошибка продвигается к конечному результату
     *
     * Optional функции фильтрации и проверки на пустое значение в данном контексте являются Антипаттерном,
     * так как имеют название не отражающее суть обработки и переносят механизи обработки из бизнеслогики на цепочку:
     *
     *     Result<T> filter( Predicate<? super T> predicate ) {}
     *     <U> Result<U> ifPresentOrElse​( Function<? super T, Result<U>> flatMapIfPresent, Supplier<Result<U>> onNotPresent ) {}
     *
     *  Вместо них рекомендуется исользовать mapping, передавая данные в функцию
     *  с названием отражающим конкретный смысл фильтрации или проверки.
     */

    /**
     * Когда вызваемая функция возвращает не Result, а конкретное значение
     */
    @JsonIgnore
    public <U> CoreResponse<U> map( Function<? super T, ? extends U> mapper) {
        if (mapper == null || !isOk())
            return error( status, message );
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
            return error( status, message );
        else {
            return mapper.apply(data);
        }
    }

    /**
     * Если результрат не успешен
     * получить тот же результат выполнив другое действие
     */
    @JsonIgnore
    public CoreResponse<T> orElseGet( Function<CoreResponse<T>, CoreResponse<T>> mapper ) {
        if (mapper == null) {
            return error( status, message );
        }
        if (!isOk()) {
            return mapper.apply(this);
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
