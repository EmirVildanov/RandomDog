package com.example.randomdog

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import com.example.randomdog.data.Language
import timber.log.Timber
import java.util.*

class RandomDogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    fun isInternetAvailable(activity: Activity?): Boolean {
        if (activity == null) {
            throw IllegalArgumentException("Null context was passed")
        }
        var result = false
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
        return result
    }

    fun changeLanguage(activity: Activity?, language: Language) {
        setLanguage(activity, language)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        with(sharedPreferences.edit()) {
            putString(activity?.getString(R.string.saved_language_key), language.name)
            apply()
        }
    }

    fun setLanguage(activity: Activity?, language: Language? = null) {
        var chosenLanguage = language
        if (chosenLanguage == null) {
            chosenLanguage = try {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val languageName = sharedPreferences.getString(getString(R.string.saved_language_key), null)!!
                Language.valueOf(languageName)
            } catch (e: java.lang.IllegalArgumentException) {
                Language.ENGLISH
            } catch (e: NullPointerException) {
                Language.ENGLISH
            }
        }
        val configuration = Configuration(activity?.resources?.configuration)
        when (chosenLanguage) {
            Language.RUSSIAN -> {
                configuration.locale = Locale("ru")
            }
            Language.ENGLISH -> {
                configuration.locale = Locale.ENGLISH
            }
        }
        activity?.resources?.updateConfiguration(configuration, activity.resources.displayMetrics)
        (activity as MainActivity).bottomNavigationView.menu.clear();
        activity.bottomNavigationView.inflateMenu(R.menu.menu_main);
        Timber.i("Set language: ${chosenLanguage?.name}")
    }

    fun showToast(activity: FragmentActivity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}