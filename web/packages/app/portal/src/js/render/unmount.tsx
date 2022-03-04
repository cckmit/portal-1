import ReactDOM from "react-dom"

export function unmount(container: Element | DocumentFragment): void {
  ReactDOM.unmountComponentAtNode(container)
}
