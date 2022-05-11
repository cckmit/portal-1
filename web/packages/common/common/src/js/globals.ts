export const DEV = process.env.NODE_ENV !== "production"
export const buildHash = "__webpack_hash__" in window ? __webpack_hash__ : ""
export const version = "__app_version__" in window ? __app_version__ : ""
