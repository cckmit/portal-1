import { makeObservableStore } from "@protei-libs/store"
import { PersonId, UserLoginId } from "@protei-portal/common-model"

export const AuthStore$type = Symbol("AuthStore")

export interface AuthStore {
  authorized: boolean
  personId: PersonId
  loginId: UserLoginId
}

export const authStore = makeObservableStore<AuthStore>({
  authorized: false,
  personId: 0,
  loginId: 0,
})
