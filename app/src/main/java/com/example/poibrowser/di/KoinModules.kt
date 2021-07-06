package com.example.poibrowser.di

import androidx.fragment.app.FragmentActivity
import com.example.poibrowser.ui.map.MapViewModel
import com.example.poibrowser.utils.helpers.dialog.DialogManager
import com.example.poibrowser.utils.helpers.dialog.DialogManagerImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Tomislav Curis
 */

val AppModule = module {
    factory { (activity: FragmentActivity) -> DialogManagerImpl(activity) as DialogManager }
}

val DataModule = module {
    viewModel { MapViewModel() }
}

