package vc.android.projeemana.utils

data class DataOrException<T, E : Exception?>(
    var data: T? = null,
    var e: E? = null
)