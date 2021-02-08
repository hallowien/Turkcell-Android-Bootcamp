package com.example.yemeksiparis

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import java.io.Serializable

class Urun(var yemek_id:Int, var yemek_adi:String, var yemek_resim_adi:String, var yemek_fiyat:Int, var yemek_siparis_adet:Int):Serializable {
}