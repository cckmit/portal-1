import { inject, injectable } from "inversify"
import { makeLogger } from "@protei-libs/logger"
import { isExceptionNamed, newException } from "@protei-libs/exception"
import { HttpHeaders, HttpMethod, HttpRequest, HttpResponse } from "@protei-libs/http"
import {
  En_ResultStatus,
  ExceptionName,
  JsonRequest,
  JsonResponse,
  JsonResponseValidator,
  newExceptionApiError,
} from "../../model"
import { detectException, Json, validateApiResponse } from "../../infrastructure"
import { PortalApiJson } from "./json"
import { AuthStore, AuthStore$type } from "../../store"
import { PortalApiRequestIdProvider, PortalApiRequestIdProvider$type } from "./PortalApiRequestIdProvider"
import { HttpTransport, HttpTransport$type } from "./HttpTransport"

export interface PortalApiRequest {
  url: string
  method: HttpMethod
  headers?: HttpHeaders | undefined
  body?: object | undefined
}

export interface PortalApiResponse {
  status: number
  headers: HttpHeaders
  body: unknown | undefined
}


export const PortalApiTransport$type = Symbol("PortalApiTransport")

export interface PortalApiTransport {
  exchange<T>(
    request: PortalApiRequest,
    opts?: {
      json?: Json
    },
  ): Promise<PortalApiResponse>
}

@injectable()
export class PortalApiTransportImpl implements PortalApiTransport {

  async exchange<T>(
    request: PortalApiRequest,
    opts?: {
      json?: Json
    },
  ): Promise<PortalApiResponse> {
    const json = opts?.json || PortalApiJson
    const jsonRequest: JsonRequest = {
      requestId: this.requestIdProvider.next(),
      data: request.body,
    }
    const body = json.stringify(jsonRequest)
    const esRequest: HttpRequest = {
      method: request.method,
      url: this.makeUrl(request),
      headers: this.makeHeaders(request),
      body: body,
    }
    this.log.info("[HTTP] request '{}': {}", esRequest.method, esRequest.url)
    this.log.debug("[HTTP] request '{}': {} (headers:{}) (body:{})", esRequest.method, esRequest.url, esRequest.headers, esRequest.body)
    const response = await this.transport.exchange(esRequest)
    try {
      const body = await response.body.text()
      this.log.info("[HTTP] response '{}': {} {}", esRequest.method, esRequest.url, response.status)
      this.log.debug("[HTTP] response '{}': {} {} (headers:{}) (body:{})", esRequest.method, esRequest.url, response.status, response.headers, body)
      const dto = json.parse(body)
      const res = validateApiResponse(dto, JsonResponseValidator)
      if (res.status !== En_ResultStatus.OK) {
        this.throwApiErrorException(response, res)
      }
      return {
        status: response.status,
        headers: response.headers,
        body: res.data,
      }
    } catch (e) {
      const exception = detectException(e)
      if (isExceptionNamed(exception, ExceptionName.NATIVE)) {
        const message = `Failed to parse json response of '${esRequest.method} ${esRequest.url}' request`
        const ex = newException(ExceptionName.API_PARSE, {message, cause: exception}, this.exchange.bind(this))
        this.log.error("[HTTP] request '{}': {} | failed", esRequest.method, esRequest.url, ex)
        throw ex
      }
      this.log.error("[HTTP] request '{}': {} | failed", esRequest.method, esRequest.url, exception)
      throw exception
    }
  }

  private makeUrl(request: PortalApiRequest): string {
    return this.BASE_URL + request.url
  }

  private makeHeaders(request: PortalApiRequest): HttpHeaders {
    const headers = request.headers || {}
    if (headers["Accept"] === undefined) {
      headers["Accept"] = "application/json"
    }
    if (headers["Content-Type"] === undefined) {
      headers["Content-Type"] = "application/json"
    }
    return headers
  }

  private throwApiErrorException(response: HttpResponse, res: JsonResponse): never {
    throw newExceptionApiError(res, this.exchange.bind(this))
  }

  constructor(
    @inject(AuthStore$type) authStore: AuthStore,
    @inject(PortalApiRequestIdProvider$type) requestIdProvider: PortalApiRequestIdProvider,
    @inject(HttpTransport$type) transport: HttpTransport,
  ) {
    this.authStore = authStore
    this.requestIdProvider = requestIdProvider
    this.transport = transport
  }

  private readonly authStore: AuthStore
  private readonly requestIdProvider: PortalApiRequestIdProvider
  private readonly transport: HttpTransport
  private readonly BASE_URL = "Portal/springApi/jsonApi"
  private readonly log = makeLogger("portal.api.http")
}
