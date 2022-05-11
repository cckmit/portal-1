import React, { ChangeEvent, KeyboardEvent, memo, RefObject, useCallback, useImperativeHandle, useRef } from "react"
import { classesOf, shallowEqual } from "@protei-libs/react"

export interface InputRef {
  setSelectionAtTheEnd: () => void
}

export type InputProps = {
  cbRef?: RefObject<InputRef>
  className?: string
  value: string | number | undefined
  state?: "focus" | "active" | "disabled" | "error"
  widthAll?: boolean
  height32px?: boolean
  noFocus?: boolean
  noActive?: boolean
  onChangeFunction?: (value: string) => void
  onEnter?: (event: KeyboardEvent<HTMLInputElement>) => void
  allowedValueRegex?: RegExp
} & Omit<React.DetailedHTMLProps<React.InputHTMLAttributes<HTMLInputElement>, HTMLInputElement>, "className" | "value" | "disabled" | "onEnter">

export const Input = memo(function Input(props: InputProps) {
  const { cbRef, className, value, state, widthAll, height32px, noFocus, noActive, onChangeFunction, onEnter, allowedValueRegex, ...inputProps } = props
  const propsOnChange = inputProps.onChange
  const propsOnKeyDown = inputProps.onKeyDown
  const disabled = state === "disabled" || undefined

  const refInput = useRef<HTMLInputElement>(null)

  const isValueAllowed = useCallback(
    (value: string) => {
      if (allowedValueRegex === undefined) {
        return true
      }
      return allowedValueRegex.test(value)
    },
    [allowedValueRegex],
  )

  const onChange = useCallback(
    (event: ChangeEvent<HTMLInputElement>) => {
      const nextValue = event.target.value
      if (!isValueAllowed(nextValue)) {
        event.preventDefault()
        return
      }
      if (onChangeFunction) {
        onChangeFunction(nextValue)
      }
      propsOnChange && propsOnChange(event)
    },
    [isValueAllowed, onChangeFunction, propsOnChange],
  )

  const onKeyDown = useCallback(
    (event: KeyboardEvent<HTMLInputElement>) => {
      if (event.key === "Enter") {
        onEnter?.(event)
      }
      propsOnKeyDown && propsOnKeyDown(event)
    },
    [onEnter, propsOnKeyDown],
  )

  const focus = useCallback(() => {
    refInput.current?.focus()
  }, [])

  useImperativeHandle(
    cbRef,
    () => ({
      setSelectionAtTheEnd: focus,
    }),
    [focus],
  )

  const classes = classesOf(!className && "form-control", className)

  return <input {...inputProps} ref={refInput} className={classes} value={value ?? ""} onChange={onChange} onKeyDown={onKeyDown} disabled={disabled} />
}, shallowEqual)
