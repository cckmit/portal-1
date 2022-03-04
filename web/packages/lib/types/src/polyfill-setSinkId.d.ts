/**
 * Audio Output Devices API
 * https://www.w3.org/TR/audio-output/#dom-htmlmediaelement-setsinkid
 */

interface HTMLMediaElement {
  sinkId: string | undefined
  setSinkId(sinkId: string): Promise<void>
}
