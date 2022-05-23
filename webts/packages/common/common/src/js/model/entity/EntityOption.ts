import { makeJsonSchema, makeJsonSchemaValidate } from "../../infrastructure"

export interface EntityOption<T = number> {
  id: T
  displayText?: string
  info?: string
}

export const EntityOptionSchema = makeJsonSchema<EntityOption>({
  type: "object",
  properties: {
    id: { type: "number" },
    displayText: { type: "string", nullable: true },
    info: { type: "string", nullable: true },
  },
  required: [
    "id",
  ],
})

export const EntityOptionValidator = makeJsonSchemaValidate<EntityOption>(EntityOptionSchema)
