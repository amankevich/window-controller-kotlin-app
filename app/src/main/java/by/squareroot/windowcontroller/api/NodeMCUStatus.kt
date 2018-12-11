package by.squareroot.windowcontroller.api

const val NODE_MCU_POSITION_UP = "up"
const val NODE_MCU_POSITION_DOWN = "down"

data class NodeMCUStatus(val status: String, val position: String)