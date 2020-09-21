package com.example.qrcodereadercomposedemo.di

import com.example.qrcodereadercomposedemo.ui.camera.BarcodeViewmodel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { BarcodeViewmodel() }
}