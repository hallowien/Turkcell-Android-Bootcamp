package com.example.yemeksiparis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_urun_detay.*

class UrunDetayActivity : AppCompatActivity() {


    private lateinit var urun:Urun
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urun_detay)


        toolbarUrunDetayActivity.title = "Yemek Detay"
        //    toolbarMainActivity.subtitle = "Alt Başlık"
        setSupportActionBar(toolbarUrunDetayActivity)

        urun = intent.getSerializableExtra("nesne") as Urun

        urunad.setText(urun.yemek_adi)
        urunfiyat.setText(urun.yemek_fiyat)

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${urun.yemek_resim_adi}"
        Picasso.get().load(url).into(imageView)
    }

}