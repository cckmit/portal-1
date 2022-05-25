import { inject, injectable } from "inversify"
import { runInTransaction } from "@protei-libs/store"
import { makeLogger } from "@protei-libs/logger"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import {
  EventBusEventAuthLoginDone,
  EventBusEventAuthLoginDoneType,
  EventBusEventAuthLogoutDone,
  EventBusEventAuthLogoutDoneType,
  PersonId,
  UserLoginId,
} from "@protei-portal/common"
import { AuthStore, AuthStore$type } from "../store"

export const AuthService$type = Symbol("AuthService")

export interface AuthService {
  loginDone(personId: PersonId, loginId: UserLoginId): void

  logoutDone(userInitiated: boolean): void
}

@injectable()
export class AuthServiceImpl implements AuthService {
  loginDone(personId: PersonId, loginId: UserLoginId): void {
    this.log.info("Login done | personId={}, loginId={}", personId, loginId)
    runInTransaction(() => {
      this.authStore.personId = personId
      this.authStore.loginId = loginId
      this.authStore.authorized = true
    })
  }

  logoutDone(userInitiated: boolean): void {
    this.log.info("Logout done | userInitiated={}", userInitiated)
    runInTransaction(() => {
      this.authStore.personId = 0
      this.authStore.loginId = 0
      this.authStore.authorized = false
    })
  }

  constructor(
    @inject(EventBusModule$type) eventBusModule: EventBusModule,
    @inject(AuthStore$type) authStore: AuthStore,
  ) {
    this.eventBusModule = eventBusModule
    this.authStore = authStore
    this.eventBusModule.listenEvent<EventBusEventAuthLoginDone>(
      { topic: EventBusEventAuthLoginDoneType },
      (event) => {
        this.loginDone(event.payload.personId, event.payload.loginId)
      },
    )
    this.eventBusModule.listenEventSync<EventBusEventAuthLogoutDone>(
      { topic: EventBusEventAuthLogoutDoneType },
      (event) => {
        this.logoutDone(event.payload.userInitiated)
      },
    )
  }

  private readonly eventBusModule: EventBusModule
  private readonly authStore: AuthStore
  private readonly log = makeLogger("portal.common.auth")
}
