package com.example.poibrowser.di

import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.poibrowser.data.source.MapDataSource
import com.example.poibrowser.data.source.MapRepository
import com.example.poibrowser.data.source.MapRepositoryImpl
import com.example.poibrowser.data.source.local.LocalMapDataSource
import com.example.poibrowser.data.source.local.POIBDao
import com.example.poibrowser.data.source.local.POIBDatabase
import com.example.poibrowser.data.source.remote.APIConstants
import com.example.poibrowser.data.source.remote.FourSquareAPI
import com.example.poibrowser.data.source.remote.RemoteMapDataSource
import com.example.poibrowser.ui.map.MapViewModel
import com.example.poibrowser.utils.helpers.dialog.DialogManager
import com.example.poibrowser.utils.helpers.dialog.DialogManagerImpl
import com.example.poibrowser.utils.network.InternetConnectionManager
import com.example.poibrowser.utils.network.InternetConnectionManagerImpl
import com.example.poibrowser.utils.network.NullOnEmptyConverterFactory
import com.example.poibrowser.utils.network.ResponseInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.sin

/**
 * @author Tomislav Curis
 */

val AppModule = module {
    factory { (activity: FragmentActivity) -> DialogManagerImpl(activity) as DialogManager }
}

val DataModule = module {

    single {
        Room
            .databaseBuilder(androidContext(), POIBDatabase::class.java, "poib.db")
            .build()
    }

    single { get<POIBDatabase>().getPOIBDao() as POIBDao}

    single { Dispatchers.IO }

    single(named("local")) { LocalMapDataSource(get(), get()) as MapDataSource }
    single(named("remote")) { RemoteMapDataSource(get()) as MapDataSource}
}

val RepoModule = module {

    single { MapRepositoryImpl(get(qualifier = named("local")),
        get(qualifier = named("remote"))) as MapRepository }

    viewModel { MapViewModel(get()) }
}

val NetModule = module {

    single { InternetConnectionManagerImpl() as InternetConnectionManager }

    single {

        OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)
//            .addInterceptor(NetworkExceptionInterceptor())
            .addInterceptor(ResponseInterceptor(get())).apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(loggingInterceptor)
                }
            }
            .build()
    }

    single {
        (Retrofit.Builder()
            .baseUrl(APIConstants.BASE_URL)
            .client(get())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(FourSquareAPI::class.java)) as FourSquareAPI
    }
}

