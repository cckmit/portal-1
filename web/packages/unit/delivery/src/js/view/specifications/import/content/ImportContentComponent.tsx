import { observer } from "mobx-react-lite"
import { Input, InputFile } from "@protei-portal/common"
import { useLang } from "@protei-portal/common-lang"

export const ImportContentComponent = observer(function ImportContentComponent() {
  const lang = useLang()

  return (
    <div className="card-body no-padding">
      <div className="container-fluid padding-15">
        <div className="card card-transparent no-margin">
          <div className="card-body">
            <div className="row">
              <div className="form-group col-12 col-md-6">
                <label>Наименование спецификации</label>
                {/*<Input />*/}
              </div>
            </div>
            <div className="row">
              <div className="form-group col-12 col-md-6">
                <label>Файл спецификации</label>
                <div>
                  {/*<InputFile>
                    <i className="far fa-file-excel"/>
                    &nbsp;
                    <span className="bold">Выбрать EXCEL файл</span>
                  </InputFile>*/}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
})
