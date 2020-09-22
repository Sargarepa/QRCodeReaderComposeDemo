package com.example.qrcodereadercomposedemo.di

import com.example.qrcodereadercomposedemo.ui.LoginViewModel
import com.example.qrcodereadercomposedemo.ui.camera.BarcodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { BarcodeViewModel() }
    viewModel { LoginViewModel() }
}