package com.example.yemeksiparis

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class SepetAdapter(var mContext: Context, var urunlerListe:ArrayList<Urun> ) :RecyclerView.Adapter<SepetAdapter.CardSepetTasarimtutucu>() {

    private lateinit var urun: Urun

    inner class CardSepetTasarimtutucu(tasarim: View) : RecyclerView.ViewHolder(tasarim) {

        var sepetcard: CardView
        var sepet_ad_yazi: TextView
        var sepet_fiyat_yazi: TextView
        var sepet_urun_sil: ImageView
        var sepet_urun_ekle: ImageView
        var sepet_urun_resim:ImageView
        var sepet_urun_adet: TextView

        init {
            sepetcard = tasarim.findViewById(R.id.sepetcard)
            sepet_ad_yazi = tasarim.findViewById(R.id.urun_ad_yazi)
            sepet_fiyat_yazi = tasarim.findViewById(R.id.urun_fiyat_yazi)
            sepet_urun_sil = tasarim.findViewById(R.id.urun_sil_resim)
            sepet_urun_ekle = tasarim.findViewById(R.id.sepet_urun_ekle)
            sepet_urun_resim = tasarim.findViewById(R.id.sepet_urun_resim)
            sepet_urun_adet = tasarim.findViewById(R.id.sepet_urun_adet)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardSepetTasarimtutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.urunsepettasarim, parent, false)
        return CardSepetTasarimtutucu(tasarim)
    }


    override fun onBindViewHolder(holder: CardSepetTasarimtutucu, position: Int) {

        var urun: Urun = urunlerListe.get(position)

        holder.sepet_ad_yazi.text = "${urun.yemek_adi}"
        holder.sepet_fiyat_yazi.text = "${urun.yemek_fiyat}TL"
        holder.sepet_adet_yazi.text = "${urun.yemek_siparis_adet}"


        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${urun.yemek_resim_adi}"
        Picasso.get().load(url).into(holder.sepet_urun_resim)

        holder.sepet_sil_resim.setOnClickListener {
            Toast.makeText(mContext, "${urun.yemek_adi} Sepetten Çıkarıldı", Toast.LENGTH_LONG).show()

            val url = "http://kasimadalan.pe.hu/yemekler/delete_sepet_yemek.php"

            val istek =
                object : StringRequest(Request.Method.POST, url, Response.Listener { cevap ->

                    Log.e("Sil Cevap", cevap)
                    tumUrunler()

                }, Response.ErrorListener { Log.e("Sil", "Hata") }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["yemek_id"] = urun.yemek_id.toString()
                        return params
                    }
                }
            Volley.newRequestQueue(mContext).add(istek)
        }

        holder.sepet_ekle_resim.setOnClickListener {
            Toast.makeText(mContext, "${urun.yemek_adi} Sepete eklendi", Toast.LENGTH_LONG).show()

            val url = "http://kasimadalan.pe.hu/yemekler/insert_sepet_yemek.php"
            val istek =
                object : StringRequest(Request.Method.POST, url, Response.Listener { cevap ->
                    Log.e("Ekle Cevap", cevap)
                    tumUrunler()
                }, Response.ErrorListener { Log.e("Sil", "Hata") }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["yemek_id"] = urun.yemek_id.toString()
                        return params
                    }
                }
            Volley.newRequestQueue(mContext).add(istek)
        }

        holder.sepetcard.setOnClickListener {
            val intent = Intent(mContext, UrunDetayActivity::class.java)
            intent.putExtra("nesne", urun.yemek_adi)
            mContext.startActivity(Intent())
        }
    }

    override fun getItemCount(): Int {
        return urunlerListe.size
    }


    fun tumUrunler() {

        val url = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"
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

                notifyDataSetChanged()

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Log.e("Hata", "Veri okuma") })

        Volley.newRequestQueue(mContext).add(istek)
    }

}

