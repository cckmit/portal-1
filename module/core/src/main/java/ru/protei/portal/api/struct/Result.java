package ru.protei.portal.api.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by michael on 27.06.16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect
@XmlRootElement(name = "result")
public class Result<T> {

    public Result() {
    }

    @JsonProperty
    private En_ResultStatus status;

    @JsonProperty
    private T data;

    @JsonProperty
    private String message;

    @JsonIgnore
    private List<ApplicationEvent> events;

    @JsonIgnore
    public boolean isOk () {
        return status == En_ResultStatus.OK;
    }

    @JsonIgnore
    public boolean isError () {
        return status != En_ResultStatus.OK;
    }

    @XmlElement(name="status")
    public En_ResultStatus getStatus () {
        return status;
    }

    @XmlElement(name="data")
    public T getData () {
        return data;
    }

    @XmlElement(name="message")
    public String getMessage() {
        return message;
    }

    public List<ApplicationEvent> getEvents() {
        return events;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public void setStatus( En_ResultStatus status ) {
        this.status = status;
    }

    public void setData( T data ) {
        this.data = data;
    }

    public Result( En_ResultStatus status, T data, String message, List<ApplicationEvent> events ) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.events = events;
    }

    @JsonIgnore
    public static <T> Result<T> error( En_ResultStatus status ) {
        return error( status, null);
    }
    @JsonIgnore
    public static <T> Result<T> error( En_ResultStatus status, String message ) {
        return new Result<T>( status, null, message, null);
    }

    @JsonIgnore
    public static <T> Result<T> error( En_ResultStatus status, String message, List<ApplicationEvent> events ) {
        return new Result<T>( status, null, message, events);
    }

    @JsonIgnore
    public static <T> Result<T> ok() {
        return ok(null);
    }

    @JsonIgnore
    public static <T> Result<T> ok( T data ) {
        return new Result<T>( En_ResultStatus.OK, data, null, null);
    }

    @JsonIgnore
    public static <T> Result<T> ok( T data, String message ) {
        return new Result<T>( En_ResultStatus.OK, data, message, null);
    }

    @JsonIgnore
    public static <T> Result<T> ok( T data, List<ApplicationEvent> events ) {
        return new Result<T>( En_ResultStatus.OK, data, null, events);
    }

    @JsonIgnore
    public Result<T> publishEvent( ApplicationEvent event ) {
        if (event == null) return this;
        if (this.events == null) this.events = new ArrayList<>();
        this.events.add( event );
        return this;
    }

    @JsonIgnore
    public <E extends ApplicationEvent> Result<T> publishEvents( List<E> events ) {
        if (events == null) return this;
        if (this.events == null) this.events = new ArrayList<>();
        this.events.addAll( events );
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", events=" + events +
                '}';
    }


    /**
     * Если результрат успешен
     */
    @JsonIgnore
    public Result<T> ifOk( Consumer<? super T> consumer ) {
        if (consumer != null && isOk()) {
            consumer.accept( data );
        }
        return this;
    }

    @JsonIgnore
    public Result<T> ifError( Consumer<Result<T>> consumer ) {
        if (consumer != null && !isOk()) {
            consumer.accept( this );
        }
        return this;
    }

    /**
     *  Поведение подобно Optional от Result<T> на основании En_ResultStatus вместо проверки на null
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
    public <U> Result<U> map( Function<? super T, ? extends U> mapper) {
        if (mapper == null || !isOk())
            return error( status, message, events );
        else {
            return ok( mapper.apply( data ), events );
        }
    }

    /**
     * Когда вызываемая функция возвращает Result
     */
    @JsonIgnore
    public <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
        if (mapper == null || !isOk())
            return error( status, message, events );
        else {
            return mapper.apply( data ).publishEvents( events );
        }
    }

    /**
     * Если результрат не успешен
     * получить тот же результат выполнив другое действие (новая Result цепочка)
     * Обработка всех информационных потоков (данные, события, ошибки и прочее содержимое Result)
     * должно осуществляться в перекладывающей функции (Function<Result<T>, Result<T>> mapper ),
     * так как зависит от бизнеслогики.
     */
    @JsonIgnore
    public Result<T> orElseGet( Function<Result<T>, Result<T>> mapper ) {
        if (mapper == null) {
            return this;
        }
        if (isOk()) {
            return this;
        }
        return mapper.apply(this);
    }

    /**
     * Подменить результат в случае !isOK()
     */
    @JsonIgnore
    public T orElse( T alternativeData ) {
        if (isOk()) {
            return getData();
        }
        return alternativeData;
    }

    @JsonIgnore
    public <X extends Throwable> Result<T> orElseThrow( Function<Result<T>, ? extends X> exceptionSupplier ) throws X {
        if (!isOk()) {
            throw exceptionSupplier.apply( this );
        }
        return this;
    }
}
