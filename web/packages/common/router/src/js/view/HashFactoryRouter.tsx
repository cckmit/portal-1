/* eslint-disable react/no-children-prop */
import { useLayoutEffect, useRef, useState } from "react"
import { HashRouterProps } from "react-router-dom"
import { Router } from "react-router"
import { createHashHistory, HashHistory, Update } from "history"
import { addSlashToHistoryLocation } from "../util/addSlashToHistoryLocation"

// @see HashRouter implementation
export function HashFactoryRouter({ basename, children, window }: HashRouterProps): JSX.Element {
  const historyRef = useRef<HashHistory>()
  if (historyRef.current == null) {
    historyRef.current = createHashHistory({ window })
  }

  const history = historyRef.current
  const [state, setState] = useState({
    action: history.action,
    location: addSlashToHistoryLocation(history.location),
  })

  useLayoutEffect(
    () =>
      history.listen((update: Update) => {
        const updateNext = {
          ...update,
          location: addSlashToHistoryLocation(update.location),
        }
        setState(updateNext)
      }),
    [history],
  )

  return <Router basename={basename} children={children} location={state.location} navigationType={state.action} navigator={history} />
}

