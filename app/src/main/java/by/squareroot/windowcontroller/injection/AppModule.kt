package by.squareroot.windowcontroller.injection

import android.app.Application
import android.os.Build
import androidx.room.Room
import by.squareroot.windowcontroller.data.AppDatabase
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton


@Module
class AppModule() {
    @Provides
    @Singleton
    fun providesDatabase(app : Application) : AppDatabase {
        return Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "windowblinds-db").build()
    }

    @Provides
    @Singleton
    fun providesLocale(app : Application) : Locale {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return app.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return app.getResources().getConfiguration().locale;
        }
    }
}