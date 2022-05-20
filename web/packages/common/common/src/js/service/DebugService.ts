import { inject, injectable } from "inversify"
import { portalGetLogHistory } from "@protei-portal/common-logger"
import { buildHash, DEV, version } from "../globals"
import { AppFile } from "../model"
import { dateFormat } from "../util"
import { MediaService, MediaService$type } from "./MediaService"

export const DebugService$type = Symbol("DebugService")

export interface DebugService {
  downloadLogHistory(): Promise<void>
}

@injectable()
export class DebugServiceImpl implements DebugService {

  async downloadLogHistory(): Promise<void> {
    const file = await this.makeLogHistoryFile()
    this.mediaService.downloadAppFile(file)
  }

  private async makeLogHistoryFile(): Promise<AppFile> {
    const logHistory = await portalGetLogHistory()
    const logHistoryHeader = this.makeLogHistoryHeader(logHistory.maxLength)
    const logs = logHistoryHeader.concat(logHistory.log)
    const mimetype = "text/plain"
    const blob = new Blob(logs, { type: mimetype })
    const size = blob.size
    const blobUrl = URL.createObjectURL(blob)
    const filename = this.makeLogHistoryFileName()
    return {
      filename: filename,
      mimetype: mimetype,
      size: size,
      blobUrl: blobUrl,
    }
  }

  private makeLogHistoryHeader(logHistoryMaxLength: number): Array<string> {
    const header: Array<string> = []
    header.push("## PORTAL-ui application\n")
    header.push("\n")
    header.push(`Application version is v${version}\n`)
    header.push(`Application build hash is ${buildHash}\n`)
    header.push(`Application mode is ${DEV ? "development" : "production"}\n`)
    header.push(`Application origin is ${window.location.origin}\n`)
    header.push(`Browser user agent is ${window.navigator.userAgent}\n`)
    header.push(`Browser platform is ${window.navigator.platform}\n`)
    header.push(`Browser window current dimensions is ${window.innerWidth}w x ${window.innerHeight}h\n`)
    header.push(`Current time is ${dateFormat(new Date(), "yyyy.MM.dd HH:mm:ss OOOO")}\n`)
    header.push("\n")
    header.push(`## PORTAL-ui logs (latest ${logHistoryMaxLength} entries)\n`)
    header.push("\n")
    return header
  }

  private makeLogHistoryFileName(): string {
    const date = new Date()
    const dateString = dateFormat(date, "yyyy.MM.dd-HH.mm.ss")
    return `log-portalui-${dateString}.txt`
  }

  constructor(
     @inject(MediaService$type) mediaService: MediaService,
  ) {
    this.mediaService = mediaService
  }

  private readonly mediaService: MediaService
}
