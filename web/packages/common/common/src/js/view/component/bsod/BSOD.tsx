import { memo } from "react"
import { Exception } from "@protei-libs/exception"
import { ExceptionDescriber } from "../error/ExceptionDescriber"
import { Button } from "../../widget"

type Props = {
  exception: Exception
}

export const BSOD = memo(function BSOD(props: Props) {
  const { exception } = props
  // const debugService = useIoCBinding<DebugService>(DebugService$type)

  return (<>
    <div className="bsod-screen">
      <div className="content">
        <div className="icon">
          <i className="fa-regular fa-face-frown-slight"/>
        </div>
        <div className="info">
          <div>Данное приложение столкнулось с непредвиденной ошибкой и нуждается в перезагрузке</div>
          <div>Ниже указано краткое описание ошибки</div>
        </div>
        <div className="exception">
          <ExceptionDescriber {...exception} render={(header, description) => (<>
            <div className="header">{header}</div>
            {description && (
              <div className="description">{description}</div>
            )}
          </>)}/>
        </div>
        <div className="controls">
          <Button className="block-button primary"
                  // className="btn btn-primary"
                  value="Перезагрузить приложение"
                  onClick={() => (window.location.reload())}/>
          {/*<Button className="block-button primary"
                  // className="btn btn-primary"
                  value="Скачать файл логов"
                  onClick={() => debugService.downloadLogHistory()}/>*/}
        </div>
      </div>
    </div>
  </>)
})
