/* eslint-disable */
// @ts-nocheck

import "reflect-metadata"
import "jest"
import "jest-canvas-mock"

jest.mock("inversify", () => {
  return {
    ...(jest.requireActual("inversify")),
    inject: () => {
      return jest.fn()
    },
  };
});

window.URL.createObjectURL = jest.fn()
window.URL.revokeObjectURL = jest.fn()

Object.defineProperty(window, "matchMedia", {
  writable: true,
  value: jest.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
})
