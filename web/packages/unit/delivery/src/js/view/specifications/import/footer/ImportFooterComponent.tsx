import { useCallback } from "react"
import { observer } from "mobx-react-lite"
import { useNavigate } from "react-router-dom"
import { Button } from "@protei-portal/common"
import { useLang } from "@protei-portal/common-lang"

export const ImportFooterComponent = observer(function ImportFooterComponent() {
  const lang = useLang()
  const navigate = useNavigate()

  const onClose = useCallback(() => {
    navigate(-1)
  }, [navigate])

  const onSave = useCallback(() => {
    navigate(-1)
  }, [navigate])

  return (
    <div className="card-footer text-right forcibly-bottom">
      <Button className="btn btn-complete" value={lang.buttonSave()} onClick={onSave} />
      &nbsp;
      <Button className="btn btn-default close_button" value={lang.buttonCancel()} onClick={onClose} />
    </div>
  )
})
