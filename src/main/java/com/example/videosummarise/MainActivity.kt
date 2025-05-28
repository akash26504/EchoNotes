package com.example.videosummarise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.videosummarise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Handle window insets for status bar
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // Apply top padding to toolbar to avoid status bar overlap
                binding.toolbar.setPadding(
                    binding.toolbar.paddingLeft,
                    systemBars.top,
                    binding.toolbar.paddingRight,
                    binding.toolbar.paddingBottom
                )

                // Return the insets to continue the chain
                insets
            }

            setSupportActionBar(binding.toolbar)

            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            appBarConfiguration = AppBarConfiguration(
                setOf(R.id.homeFragment)
            )

            setupActionBarWithNavController(navController, appBarConfiguration)

        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error, finish the activity gracefully
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}