package com.example.yemeksiparis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var urunlerListe:ArrayList<Urun>
    private lateinit var adapter:UrunAdapter
    private lateinit var adapterSepet:SepetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        toolbarMainActivity.title = "Urunler"
        //    toolbarMainActivity.subtitle = "Alt Başlık"
        setSupportActionBar(toolbarMainActivity)

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        //rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)

        tumUrunler()

        fab.setOnClickListener {
            //startActivity(Intent(this@MainActivity, KisiKayitActivity::class.java))
            startActivity(Intent(this@MainActivity, SepetActivity::class.java))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_arama_menu, menu)
        val item = menu.findItem(R.id.action_ara)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Log.e("Gönderilen arama sonuc", query)
        aramaYap(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.e("Harf giriş sonuc", newText)
        aramaYap(newText)
        return true
    }

    fun tumUrunler(){

        val url = "http://kasimadalan.pe.hu/yemekler/tum_yemekler.php"

        val istek = StringRequest(Request.Method.GET,url,Response.Listener {cevap ->
            try{
                urunlerListe = ArrayList<Urun>()
                val jsonObj = JSONObject(cevap)
                val yemekler = jsonObj.getJSONArray("yemekler")

                for (i in 0 until yemekler.length()){//3 -> 0,1,2
                    val k = yemekler.getJSONObject(i)

                    val urun = Urun(k.getInt("yemek_id"),
                        k.getString("yemek_adi"),
                        k.getString("yemek_resim_adi"),
                        k.getInt("yemek_fiyat"), 0)
                    urunlerListe.add(urun)
                }
                adapter = UrunAdapter(this@MainActivity, urunlerListe)
                rv.adapter = adapter

            }catch (e: JSONException){
                e.printStackTrace()
            }
        },Response.ErrorListener { Log.e("Hata","Urun okuma") })

        Volley.newRequestQueue(this@MainActivity).add(istek)
    }

    fun aramaYap(aramaKelime:String){

        val url = "http://kasimadalan.pe.hu/yemekler/tum_yemekler_arama.php"
        val istek = object : StringRequest(Request.Method.POST,url,Response.Listener { cevap ->
            try{
                urunlerListe = ArrayList<Urun>()
                val jsonObj = JSONObject(cevap)
                val yemekler = jsonObj.getJSONArray("yemekler")

                for (i in 0 until yemekler.length()){//3 -> 0,1,2
                    val k = yemekler.getJSONObject(i)

                    val urun = Urun(k.getInt("yemek_id"),
                    k.getString("yemek_adi"),
                    k.getString("yemek_resim_adi"),
                    k.getInt("yemek_fiyat"), 0)

                    urunlerListe.add(urun)
                }

                adapter = UrunAdapter(this, urunlerListe)
                rv.adapter = adapter

            }catch (e: JSONException){
                e.printStackTrace()
            }
        },Response.ErrorListener { Log.e("Hata","Veri okuma") }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["yemek_adi"] = aramaKelime
                return params
            }
        }
        Volley.newRequestQueue(this@MainActivity).add(istek)
    }
}