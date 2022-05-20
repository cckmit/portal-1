import { AuthService, AuthServiceImpl, AuthStore } from "@protei-portal/common"
import { makeStubEventBusModule } from "./stub/makeStubEventBusModule"

test("AuthService.loginDone", async () => {
  const eventBusModule = makeStubEventBusModule()
  const authStore: AuthStore = {
    authorized: false,
    personId: 0,
    loginId: 0,
  }
  const service: AuthService = new AuthServiceImpl(eventBusModule, authStore)
  service.loginDone(1, 2)
  expect(authStore.authorized).toBe(true)
  expect(authStore.personId).toBe(1)
  expect(authStore.loginId).toBe(2)
})
