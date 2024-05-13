package software.galaniberico.aniversaryagenda

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private val entries = mutableListOf<Entry>()
    private var sp: SharedPreferences? = null
    private var listener = OnSharedPreferenceChangeListener { prefs, key ->
        Toast.makeText(this, "Preferences changed", Toast.LENGTH_SHORT).show()
        if (key == "maxEntries") {
            checkButton()
        }
        if (key == "order") {
            orderEntries()
        }
        if (key == "dateFormat") {
            Entry.dateFormat = sp!!.getString("dateFormat", "dd/MM/yyyy") ?: "dd/MM/yyyy"
            orderEntries()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sp = getSharedPreferences("NativePreferences", MODE_PRIVATE)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent = result.data ?: return@registerForActivityResult
                    val date = LocalDate.of(
                        data.getStringExtra("year")?.toInt() ?: 0,
                        data.getStringExtra("month")?.toInt() ?: 0,
                        data.getStringExtra("day")?.toInt() ?: 0
                    )
                    val entry = Entry(
                        data.getStringExtra("name") ?: "XXXX",
                        date
                    )
                    if (!sp!!.getBoolean(
                            "insertRepeated",
                            true
                        ) && entries.any { it.name == entry.name }
                    )
                        return@registerForActivityResult

                    entries.add(entry)

                    checkButton()
                    orderEntries()


                }
            }
        findViewById<FloatingActionButton>(R.id.floatingActionButton).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, NewEntry::class.java)
                resultLauncher.launch(intent)
            }
        }

        sp!!.registerOnSharedPreferenceChangeListener(listener)

        if (savedInstanceState != null) {
            val serializedEntries = savedInstanceState.getString("entries") ?: return
            serializedEntries.split("##|##").forEach {
                val data = it.split("###")
                entries.add(
                    Entry(
                        data[0],
                        LocalDate.parse(data[1])
                    )
                )
            }
            checkButton()
            orderEntries()
        }

    }

    private fun checkButton() {
        val maxEntries = sp!!.getString("maxEntries", "30")?.toInt() ?: 30
        findViewById<FloatingActionButton>(R.id.floatingActionButton).isEnabled =
            entries.size < maxEntries
    }

    private fun orderEntries() {
        when (sp!!.getString("order", "A-Z")) {
            "A-Z" -> entries.sortBy { it.name }
            "Z-A" -> entries.sortByDescending { it.name }
        }
        findViewById<LinearLayout>(R.id.entries).removeAllViews()
        entries.forEach {
            findViewById<LinearLayout>(R.id.entries).addView(
                it.getView(this@MainActivity.layoutInflater)
            )
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.configuration -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("entries", entries.joinToString("##|##") { it.toString() })
    }
}