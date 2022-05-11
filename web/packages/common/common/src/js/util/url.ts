export function getQueryStringParams(query: string): Record<string, string | undefined> {
  return (/^[?#]/.test(query) ? query.slice(1) : query).split("&").reduce((params: Record<string, string>, param: string) => {
    const [key, value] = param.split("=")
    params[key] = value ? decodeURIComponent(value.replace(/\+/g, " ")) : ""
    return params
  }, {})
}
