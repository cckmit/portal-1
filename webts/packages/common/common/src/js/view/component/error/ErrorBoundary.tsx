import React, { ComponentType, ErrorInfo, ReactNode } from "react"
import { Logger } from "@protei-libs/logger"
import { Exception, newException } from "@protei-libs/exception"
import { ExceptionName } from "../../../model"

export interface ErrorBoundaryProps {
  fallback: ComponentType<{ exception: Exception }>
  logger: Logger
  children: ReactNode
}

export interface ErrorBoundaryState {
  error: Exception | undefined
}

export class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {

  constructor(props: ErrorBoundaryProps) {
    super(props)
    this.state = {
      error: undefined,
    }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    const message = "Unexpected exception occurred"
    const exception = makeExceptionReact(message, error, errorInfo)
    this.props.logger.error("UI has crashed", exception)
    this.setState({
      error: exception,
    })
  }

  render(): ReactNode {
    if (this.state.error) {
      return <this.props.fallback exception={this.state.error}/>
    } else {
      return this.props.children
    }
  }
}

function makeExceptionReact(message: string, error: Error, errorInfo: ErrorInfo): Exception {
  let stack = errorInfo.componentStack
  if (stack.startsWith("\n")) {
    stack = stack.slice(1)
  }
  return newException(ExceptionName.REACT, { message, cause: error, stack }, makeExceptionReact)
}
