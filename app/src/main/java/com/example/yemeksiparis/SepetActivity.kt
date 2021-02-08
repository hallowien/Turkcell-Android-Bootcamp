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
import kotlinx.android.synthetic.main.activity_main.rv
import kotlinx.android.synthetic.main.activity_sepet.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList


class SepetActivity : AppCompatActivity() {

    private lateinit var urunlerListe:ArrayList<Urun>
    private lateinit var adapter:SepetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sepet)


        toolbarSepetActivity.title = "Sepet"
        //    toolbarMainActivity.subtitle = "Alt Başlık"
        setSupportActionBar(toolbarSepetActivity)

        rvSepet.setHasFixedSize(true)
        rvSepet.layoutManager = LinearLayoutManager(this@SepetActivity)
        //rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)

        tumUrunler()
    }


    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    fun tumUrunler(){

        val url = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val istek = StringRequest(Request.Method.GET,url,Response.Listener {cevap ->
            try{
                urunlerListe = ArrayList<Urun>()
                val jsonObj = JSONObject(cevap)
                val yemekler = jsonObj.getJSONArray("sepet_yemekler")

                for (i in 0 until yemekler.length()){//3 -> 0,1,2
                    val k = yemekler.getJSONObject(i)

                    val urun = Urun(k.getInt("yemek_id"),
                        k.getString("yemek_adi"),
                        k.getString("yemek_resim_adi"),
                        k.getInt("yemek_fiyat"),
                        k.getInt("yemek_siparis_adet"))
                    urunlerListe.add(urun)
                }
                adapter = SepetAdapter(this@SepetActivity, urunlerListe)
                rvSepet.adapter = adapter

            }catch (e: JSONException){
                e.printStackTrace()
            }
        },Response.ErrorListener { Log.e("Hata","Urun okuma") })

        Volley.newRequestQueue(this@SepetActivity).add(istek)
    }

}
