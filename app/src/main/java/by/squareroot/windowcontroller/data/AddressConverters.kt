package by.squareroot.windowcontroller.data

import androidx.room.TypeConverter
import java.net.InetAddress

class AddressConverters {
    @TypeConverter
    fun addressToString(address: InetAddress): String {
        return address.toString()
    }

    @TypeConverter
    fun stringToAddress(string: String): InetAddress {
        return InetAddress.getByName(string)
    }
}