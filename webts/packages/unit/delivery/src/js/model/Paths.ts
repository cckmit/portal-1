import { Path } from "@protei-portal/common-router"

export const Paths: Path = ({
  path: "",
  c: {
    delivery: {
      path: "delivery",
      c: {
        specifications: {
          path: "delivery_specifications",
          c: {
            import: {
              path: "delivery_specifications_import",
              c: {},
            },
          },
        },
      },
    },
  },
})
