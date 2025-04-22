package com.example.normalapp.gui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistryViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {
}