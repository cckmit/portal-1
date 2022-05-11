import React, { ChangeEvent, memo, MouseEvent, useCallback, useRef } from "react"
import { classesOf, shallowEqual } from "@protei-libs/react"
import { Button } from "../button/Button"

export type InputFileProps = {
  className?: string
  text?: string
  placeholder?: string
  state?: "focus" | "active" | "disabled" | "error"
  accept?: string
  multiple?: boolean
  onFiles: (files: Array<File>) => void
} & Omit<React.DetailedHTMLProps<React.InputHTMLAttributes<HTMLInputElement>, HTMLInputElement>, "className" | "value" | "disabled" | "type" | "accept" | "multiple" | "onFiles">

export const InputFile = memo(function InputFile(props: InputFileProps) {
  const {
    className, text, placeholder, state,
    accept, multiple, onFiles,
    children,
    ...inputProps
  } = props

  const disabled = state === "disabled" || undefined
  const refInput = useRef<HTMLInputElement>(null)

  const reset = useCallback((event: ChangeEvent<HTMLInputElement>) => {
    // @ts-ignore
    event.target.value = null
    event.preventDefault()
    event.stopPropagation()
  }, [])

  const onChange = useCallback((event: ChangeEvent<HTMLInputElement>) => {
    if (disabled) {
      reset(event)
      return
    }
    const files = event.target.files
    if (files == null || files.length === 0) {
      return
    }
    const filesArray = Array.from(files)
    if (onFiles) {
      onFiles(filesArray)
      reset(event)
    }
  }, [ onFiles, disabled, reset ])

  const onButtonClick = useCallback((event: MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    event.stopPropagation()
    if (disabled) {
      return
    }
    refInput.current?.click()
  }, [ disabled ])

  const classes = classesOf(
    !className && "btn",
    className,
  )

  return (
    <>
      <Button className={classes}
              value={text}
              disabled={disabled}
              onClick={onButtonClick}>
        {children}
      </Button>
      <input {...inputProps}
             ref={refInput}
             className="hide"
             style={{ display: "none" }}
             type="file"
             accept={accept}
             multiple={multiple}
             onChange={onChange}
             disabled={disabled} />
    </>
  )
}, shallowEqual)
