package software.galaniberico.aaframework

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import software.galaniberico.configurator.activities.ConfigurationActivity
import software.galaniberico.configurator.configuration.ConfiguratorConfigurator
import software.galaniberico.configurator.facade.Configurator
import software.galaniberico.navigator.facade.Navigate
import software.galaniberico.restorer.configuration.RestorerConfigurator
import software.galaniberico.restorer.facade.Restorer
import software.galaniberico.restorer.tags.OnRestore
import software.galaniberico.restorer.tags.OnSave
import software.galaniberico.restorer.tags.Restore
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    @Restore
    private val entries = mutableListOf<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null)
            Configurator
                .ensure(
                    "insertRepeated",
                    getString(R.string.bool_conf_title),
                    getString(R.string.bool_conf_summary),
                    false
                )
                .ensure(
                    "maxEntries",
                    getString(R.string.int_conf_title),
                    getString(R.string.int_conf_summary),
                    10
                )
                .ensure(
                    "order",
                    getString(R.string.list_order_conf_title),
                    getString(R.string.list_order_conf_summary),
                    setOf("A-Z", "Z-A"),
                    "A-Z"
                )
                .ensure(
                    "dateFormat",
                    getString(R.string.list_date_conf_title),
                    getString(R.string.list_date_conf_summary),
                    setOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd"),
                    "dd/MM/yyyy"
                )
        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            Navigate.toReturn(NewEntry::class).andThen {
                val entry = Navigate.getResult<Entry>("entry") ?: return@andThen
                if (!Configurator.get(
                        "insertRepeated",
                        true
                    ) && entries.any { it.name == entry.name }
                )
                    return@andThen
                entries.add(entry)
                checkButton(Configurator.get("maxEntries", 10))
                orderEntries(Configurator.get("order", "A-Z"))
            }
        }
        Configurator.addSubscription<String>("maxEntries") { checkButton(it.toInt()) }
        Configurator.addSubscription<String>("order") { orderEntries(it) }
        Configurator.addSubscription<String>("dateFormat") {
            Entry.dateFormat = it
            orderEntries(Configurator.get("order", "A-Z"))
        }
    }

    @OnRestore
    private fun restoring() {
        checkButton(Configurator.get("maxEntries", 10))
        orderEntries(Configurator.get("order", "A-Z"))
    }

    private fun checkButton(maxEntries: Int) {
        findViewById<FloatingActionButton>(R.id.floatingActionButton).isEnabled =
            entries.size < maxEntries
    }

    private fun orderEntries(value: String) {
        when (value) {
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
                Navigate.to(ConfigurationActivity::class)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}