import { Http, HttpHeaders, HttpRequest, HttpResponse } from "./types"

export function makeHttpClient(
  baseURL: string,
  defaultHeaders: HttpHeaders,
  timeout?: number | undefined,
): Http {
  return new HttpImpl(baseURL, defaultHeaders, timeout)
}

class HttpImpl implements Http {
  private readonly baseURL: string
  private readonly timeout: number
  private readonly defaultHeaders: HttpHeaders

  constructor(baseURL: string, defaultHeaders: HttpHeaders, timeout?: number | undefined) {
    this.baseURL = baseURL
    this.timeout = timeout !== undefined ? timeout : 5000
    this.defaultHeaders = defaultHeaders
  }

  async exchange(request: HttpRequest): Promise<HttpResponse> {
    const abortController = "AbortController" in window ? new AbortController() : undefined
    const timeout = this.timeout
    const url = this.baseURL + request.url
    const fetchTask = fetch(url, {
      method: request.method,
      headers: this.toHeaders(request.headers),
      body: request.body,
      signal: abortController?.signal,
    })
    const timeoutTask = new Promise<Response>((_, reject) => {
      if (timeout > 0) {
        setTimeout(() => {
          reject(new Error("Request timeout"))
          abortController?.abort()
        }, timeout)
      }
    })
    const response = await Promise.race([fetchTask, timeoutTask])
    return {
      status: response.status,
      headers: this.fromHeaders(response.headers),
      body: response,
    }
  }

  private toHeaders(headers: HttpHeaders | undefined): Array<Array<string>> {
    const input = headers || {}
    const real: Array<Array<string>> = []
    for (const [key, value] of Object.entries(input)) {
      if (value === undefined) {
        continue
      }
      real.push([key, value])
    }
    for (const [key, value] of Object.entries(this.defaultHeaders)) {
      if (value === undefined || input[key] !== undefined) {
        continue
      }
      real.push([key, value])
    }
    return real
  }

  private fromHeaders(headers: Headers): HttpHeaders {
    const real: HttpHeaders = {}
    headers.forEach((value, key) => {
      real[key.toLowerCase()] = value
    })
    return real
  }
}
