import { injectable } from "inversify"
import { Http, HttpRequest, HttpResponse, makeHttpClient } from "@protei-libs/http"
import { isExceptionNamed, newException } from "@protei-libs/exception"
import { ExceptionName } from "../../model"
import { detectException } from "../../infrastructure"

export const HttpTransport$type = Symbol("HttpTransport")

export interface HttpTransport {
  exchange(request: HttpRequest): Promise<HttpResponse>
}

@injectable()
export class HttpTransportImpl implements HttpTransport {
  async exchange(request: HttpRequest): Promise<HttpResponse> {
    try {
      const http = this.getHttp()
      const response = await http.exchange(request)
      return response
    } catch (e) {
      if (e instanceof Error && e.message.includes("timeout")) {
        const message = "Request timed out"
        throw newException(ExceptionName.API_TIMEOUT, { message })
      }
      let error = detectException(e)
      if (!isExceptionNamed(error, ExceptionName.API)) {
        const message = "Failed to exchange http request"
        error = newException(ExceptionName.API, { message, cause: error })
      }
      throw error
    }
  }

  private getHttp(): Http {
    if (this.http === undefined) {
      this.http = makeHttpClient("", {})
    }
    return this.http
  }

  constructor() {}

  private http: Http | undefined
}
