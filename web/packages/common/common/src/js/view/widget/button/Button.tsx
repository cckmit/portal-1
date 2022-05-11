import React, { memo } from "react"

export type ButtonProps = {
  className?: string
  value?: string
  disabled?: boolean
} & Omit<React.DetailedHTMLProps<React.ButtonHTMLAttributes<HTMLButtonElement>, HTMLButtonElement>, "lang">

export const Button = memo(function Button(props: ButtonProps) {
  const { className, value, disabled, children, ...rest } = props

  return (
    <button {...rest} className={className} disabled={disabled}>
      {value && value}
      {children}
    </button>
  )
})
