package com.bescom.simplephotoeditorapp.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {
    protected abstract val viewModel: VM
}