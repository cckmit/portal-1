import { ReactNode, StrictMode } from "react"
import ReactDOM, { Container } from "react-dom"

export function mount(container: Container, component: ReactNode): void {
  ReactDOM.render(<StrictMode>{component}</StrictMode>, container)
}
