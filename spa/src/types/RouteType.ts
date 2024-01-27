import React from "react";

export type RouteType = {
    Component: () => React.ReactElement
    path: string
}