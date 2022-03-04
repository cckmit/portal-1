export interface Http {
  exchange(request: HttpRequest): Promise<HttpResponse>
}

export interface HttpRequest {
  url: string
  method: HttpMethod
  headers?: HttpHeaders | undefined
  body?: string | Blob | undefined
}

export interface HttpResponse {
  status: number
  headers: HttpHeaders
  body: Body
}

export type HttpMethod =
  | "get"
  | "GET"
  | "delete"
  | "DELETE"
  | "head"
  | "HEAD"
  | "options"
  | "OPTIONS"
  | "post"
  | "POST"
  | "put"
  | "PUT"
  | "patch"
  | "PATCH"
  | "purge"
  | "PURGE"
  | "link"
  | "LINK"
  | "unlink"
  | "UNLINK"

export type HttpHeaders = { [key: string]: string | undefined }
