import { injectable } from "inversify"
import { makeLogger } from "@protei-libs/logger"
import { AppFile } from "../model"
import { IS_ANCHOR_DOWNLOAD_SUPPORTED } from "../environment"

export const MediaService$type = Symbol("MediaService")

export interface MediaService {
  downloadAppFile(file: AppFile): void
}

@injectable()
export class MediaServiceImpl implements MediaService {

  downloadAppFile(file: AppFile): void {
    this.log.info("Download app file: name={}, type={}, size={}, blob={}", file.filename, file.mimetype, file.size, file.blobUrl)
    if (file.blobUrl === "") {
      this.log.warn("Download app file: blob url of file is empty")
      return
    }
    if (IS_ANCHOR_DOWNLOAD_SUPPORTED) {
      this.log.info("Download app file: will download via anchor element")
      const blobUrl = file.blobUrl
      this.openUrlWithAnchor(blobUrl, file.filename)
      return
    }
    this.log.info("Download app file: will download via window popup")
    this.openUrlWithPopup(file.blobUrl)
  }

  private openUrl(url: string): void {
    this.log.info("Open url: url={}", url)
    if (url === "") {
      this.log.warn("Open url: url is empty")
      return
    }
    if (IS_ANCHOR_DOWNLOAD_SUPPORTED) {
      this.log.info("Open url: will download via anchor element")
      this.openUrlWithAnchor(url)
      return
    }
    this.log.info("Open url: will download via window popup")
    this.openUrlWithPopup(url)
  }

  private openUrlWithAnchor(url: string, fileName?: string): void {
    const anchor = document.createElement("a")
    anchor.href = url
    if (fileName !== undefined) {
      anchor.download = fileName
    }
    anchor.target = "_blank"
    document.body.appendChild(anchor)
    anchor.click()
  }

  private openUrlWithPopup(url: string): void {
    window.open(url, "_blank")
  }

  private readonly log = makeLogger("portal.media")
}
