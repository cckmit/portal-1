import { Module } from "./Module"
import { EventBusModule } from "../eventbus"

export interface ModuleWithEventBus<MODULE_TYPE> extends Module<MODULE_TYPE>, EventBusModule {}
