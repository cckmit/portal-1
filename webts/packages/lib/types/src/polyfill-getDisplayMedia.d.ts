/**
 * Screen Capture as extension to the Media Capture API
 * https://www.w3.org/TR/screen-capture/
 */

interface MediaDevices {
  getDisplayMedia(constraints?: DisplayMediaStreamConstraints): Promise<MediaStream>
}

interface DisplayMediaStreamConstraints {
  audio?: boolean | MediaTrackConstraints
  video?: boolean | MediaTrackConstraints
}

interface MediaTrackSupportedConstraints {
  displaySurface?: boolean
  logicalSurface?: boolean
  cursor?: boolean
  restrictOwnAudio?: boolean
}

interface MediaTrackConstraintSet {
  displaySurface?: ConstrainDOMString
  logicalSurface?: ConstrainBoolean
  cursor?: ConstrainDOMString
  restrictOwnAudio?: ConstrainBoolean
}

interface MediaTrackSettings {
  displaySurface?: DisplayCaptureSurfaceType
  logicalSurface?: boolean
  cursor?: CursorCaptureConstraint
  restrictOwnAudio?: boolean
}

interface MediaTrackCapabilities {
  displaySurface?: DisplayCaptureSurfaceType
  logicalSurface?: boolean
  cursor?: Array<CursorCaptureConstraint>
}

type DisplayCaptureSurfaceType = "monitor" | "window" | "application" | "browser"
type CursorCaptureConstraint = "never" | "always" | "motion"
