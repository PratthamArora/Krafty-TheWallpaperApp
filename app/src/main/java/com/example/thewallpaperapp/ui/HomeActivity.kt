package com.example.thewallpaperapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.example.thewallpaperapp.adapter.MyFragmentAdapter
import com.example.thewallpaperapp.common.Common
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.krafty.android.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    PurchasesUpdatedListener {
    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {

    }


    val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val ref by lazy {
        FirebaseDatabase.getInstance().reference.child("Users")
    }
    val mId = mAuth.currentUser?.uid
    val mDatabase = mId?.let { ref.child(it) }


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var provider: List<AuthUI.IdpConfig>

//    lateinit var billingClient: BillingClient

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Common.permissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    toast("Permission Granted")
                else
                    toast("Please allow permission to download wallpapers")

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Common.signInReqCode) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Snackbar.make(
                    drawerLayout,
                    "Welcome   " + user?.displayName.toString(),
                    Snackbar.LENGTH_LONG
                ).show()

                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        Common.permissionRequestCode
                    )

                }
                val viewPager: ViewPager = findViewById(R.id.viewPager)
                val vpAdapter = MyFragmentAdapter(supportFragmentManager)
                viewPager.adapter = vpAdapter
                viewPager.offscreenPageLimit = 3

                val tabLayout: TabLayout = findViewById(R.id.tabLayout)
                tabLayout.setupWithViewPager(viewPager)

                loadUserInfo()


            } else {
                response?.error?.message?.let { toast(it) }
            }
        }
        return super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Krafty"


//        setupBillingClient()


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        //INIT

        provider = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        if (FirebaseAuth.getInstance().currentUser == null)
            showSignInOptions()
        else {
            Snackbar.make(
                drawerLayout,
                "Welcome   " + FirebaseAuth.getInstance().currentUser?.displayName.toString(),
                Snackbar.LENGTH_LONG
            ).show()

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Common.permissionRequestCode
                )

            }

            val viewPager: ViewPager = findViewById(R.id.viewPager)
            val vpAdapter = MyFragmentAdapter(supportFragmentManager)
            viewPager.adapter = vpAdapter
            viewPager.offscreenPageLimit = 3

            val tabLayout: TabLayout = findViewById(R.id.tabLayout)
            tabLayout.setupWithViewPager(viewPager)

            loadUserInfo()
        }
    }

//    private fun setupBillingClient() {
//
//        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
//
//
//
//        billingClient.startConnection(object : BillingClientStateListener {
//            override fun onBillingServiceDisconnected() {
//                toast("Disconnected")
//            }
//
//            override fun onBillingSetupFinished(billingResult: BillingResult?) {
//                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK)
//                    toast("Connected")
//                else
//                    toast("" + billingResult?.responseCode)
//
//
//            }
//        })
//    }

    private fun showSignInOptions() {

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(provider).setTheme(R.style.MyTheme).build(),
            Common.signInReqCode
        )

    }

    @SuppressLint("SetTextI18n")
    private fun loadUserInfo() {
        if (FirebaseAuth.getInstance().currentUser != null) {

            val headerLayout = navView.getHeaderView(0)
            headerLayout.nav_header_email.text = FirebaseAuth.getInstance().currentUser?.email

            headerLayout.nav_header_name.text =
                FirebaseAuth.getInstance().currentUser!!.displayName

            Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                .noFade()
                .into(headerLayout.nav_header_profile_image)

        }

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_aboutApp -> {
                startActivity<AboutAppActivity>()
            }


            R.id.action_donate -> {
                toast("Coming soon")

//                if (billingClient.isReady) {
//
//                    val skuList = ArrayList<String>()
//                    skuList.add("Premium Upgrade")
//
//                    val params = SkuDetailsParams.newBuilder()
//                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
//
//                    billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
//                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                            displayProduct(skuDetailsList)
//                        } else
//                            toast("Cannot display product")
//                    }
//
//                } else
//                    toast("Disconnected")
//
//
//            }
            }

        }
        return true
    }


//    private fun restartApp() {
//        startActivity<MainActivity>()
//        finish()
//
//    }

//    private fun displayProduct(skuDetailsList: List<SkuDetails>) {
//
//        val donateAdapter = DonateAdapter(this, skuDetailsList, billingClient)
//        recycler_category.adapter = donateAdapter
//        recycler_recents.adapter = donateAdapter
//        recycler_trending.adapter = donateAdapter
//
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_view_favs -> {
                // startActivity<FavouriteActivity>()
                toast("Coming soon")
            }
            R.id.nav_view_logout -> {
                AuthUI.getInstance().signOut(this)
                recreate()
                showSignInOptions()
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true


    }
}
