import { PersonId } from "./Person"
import { CompanyId } from "../company"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../infrastructure"

export interface PersonShortView {
  id: PersonId
  companyId: CompanyId
  displayName: string
  displayShortName: string
  isFired: boolean
  name?: string
}

export const PersonShortViewSchema = makeJsonSchema<PersonShortView>({
  type: "object",
  properties: {
    id: { type: "number" },
    companyId: { type: "number" },
    displayName: { type: "string" },
    displayShortName: { type: "string" },
    isFired: { type: "boolean" },
    name: { type: "string", nullable: true },
  },
  required: [
    "id",
    "companyId",
    "displayName",
    "displayShortName",
    "isFired",
  ],
})

export const PersonShortViewValidator = makeJsonSchemaValidate<PersonShortView>(PersonShortViewSchema)
