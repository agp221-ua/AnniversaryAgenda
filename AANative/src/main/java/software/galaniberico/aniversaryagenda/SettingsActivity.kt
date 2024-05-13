package software.galaniberico.aniversaryagenda

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.configuration_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.configuration_main, MyPreferencesFragment())
                .commit()
        }
    }
}

class MyPreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.setSharedPreferencesName("NativePreferences");
        val preferenceScreen: PreferenceScreen =
            preferenceManager.createPreferenceScreen(requireContext())
        val sp = requireContext().getSharedPreferences("NativePreferences", MODE_PRIVATE)
        SwitchPreferenceCompat(requireContext()).apply {
            setDefaultValue(false)
            key = "insertRepeated"
            title = requireContext().getString(R.string.bool_conf_title)
            summary = requireContext().getString(R.string.bool_conf_summary)
            preferenceScreen.addPreference(this)
        }

        EditTextPreference(requireContext()).apply {
            setOnBindEditTextListener {
                it.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }
            setOnPreferenceChangeListener { preference, newValue ->
                try {
                    newValue.toString().toInt() // Validar que sea un número entero
                    setPreference(sp, "maxEntries", newValue.toString())
                    return@setOnPreferenceChangeListener true; // Aceptar el cambio
                } catch (e: NumberFormatException) {
                    return@setOnPreferenceChangeListener false; // Rechazar el cambio si no es un número entero
                }
            }
            setDefaultValue("10")
            key = "maxEntries"
            title = requireContext().getString(R.string.int_conf_title)
            summary = requireContext().getString(R.string.int_conf_summary)
            preferenceScreen.addPreference(this)
        }

        ListPreference(requireContext()).apply {
            entries = setOf("A-Z", "Z-A").toTypedArray()
            entryValues = setOf("A-Z", "Z-A").toTypedArray()
            setDefaultValue("A-Z")
            key = "order"
            title = requireContext().getString(R.string.list_order_conf_title)
            summary = requireContext().getString(R.string.list_order_conf_summary)
            setOnPreferenceChangeListener{ _, newValue ->
                setPreference(sp, "order", newValue.toString())
                true
            }
            preferenceScreen.addPreference(this)
        }

        ListPreference(requireContext()).apply {
            entries = setOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd").toTypedArray()
            entryValues = setOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd").toTypedArray()
            setDefaultValue("dd/MM/yyyy")
            key = "dateFormat"
            title = requireContext().getString(R.string.list_date_conf_title)
            summary = requireContext().getString(R.string.list_date_conf_summary)
            setOnPreferenceChangeListener{ _, newValue ->
                setPreference(sp, "dateFormat", newValue.toString())
                true
            }
            preferenceScreen.addPreference(this)
        }

        setPreferenceScreen(preferenceScreen)
    }
    private fun setPreference(sp: SharedPreferences, key: String, value: String){
        Toast.makeText(requireContext(), "Setting $key to $value", Toast.LENGTH_SHORT).show()
        sp.edit()
            .putString(key, value)
            .apply()
    }
}


