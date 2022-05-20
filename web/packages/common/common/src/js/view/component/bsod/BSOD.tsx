import { memo } from "react"
import { Exception } from "@protei-libs/exception"
import { ExceptionDescriber } from "../error/ExceptionDescriber"
import { useIoCBinding } from "../../../ioc"
import { DebugService, DebugService$type } from "../../../service"
import { Button } from "../../widget"

type Props = {
  exception: Exception
}

export const BSOD = memo(function BSOD(props: Props) {
  const { exception } = props
  const debugService = useIoCBinding<DebugService>(DebugService$type)

  return (<>
    <div className="container-fluid pl-3 pr-3">
      <div className="card card-transparent">
        <div className="card-body">
          <div className="row">
            <div className="col-12">
              <i className="far fa-frown-open fa-7x"/>
            </div>
          </div>
          <div className="row">
            <div className="col-12">
              <h4 className="mb-0">Данное приложение столкнулось с непредвиденной ошибкой и нуждается в перезагрузке</h4>
              <h5 className="mt-0">Ниже указано краткое описание ошибки</h5>
            </div>
          </div>
          <div className="row">
            <div className="col-12">
              <pre className="border-left border-dark pl-2">
                <ExceptionDescriber {...exception} render={(header, description) => (<>
                  <div>{header}</div>
                  {description && (
                    <div>{description}</div>
                  )}
                </>)}/>
              </pre>
            </div>
          </div>
          <div className="row">
            <div className="col-12">
              <Button className="btn btn-complete mr-2"
                      value="Перезагрузить приложение"
                      onClick={() => (window.location.reload())}/>
              <Button className="btn btn-default"
                      value="Скачать файл логов"
                      onClick={() => debugService.downloadLogHistory()}/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </>)
})
