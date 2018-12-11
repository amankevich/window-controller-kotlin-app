package by.squareroot.windowcontroller.api

//{"status": "success", "downLeft": 12000, "downRight": 12000, "upLeft": 15000, "upRight": 15000}
data class WindowControllerSettings(
    val status: String,
    val downLeft: Int,
    val downRight: Int,
    val upLeft: Int,
    val upRight: Int
)