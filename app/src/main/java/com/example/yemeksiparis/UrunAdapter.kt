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

class UrunAdapter(var mContext: Context, var urunlerListe:ArrayList<Urun> ) :RecyclerView.Adapter<UrunAdapter.CardTasarimtutucu>() {

    private lateinit var urun: Urun

    inner class CardTasarimtutucu(tasarim: View) : RecyclerView.ViewHolder(tasarim) {

        var satir_card: CardView
        var urun_ad_yazi: TextView
        var urun_fiyat_yazi: TextView
        var urun_sil_resim: ImageView
        var urun_ekle_resim: ImageView
        var urun_resim:ImageView



        init {
            satir_card = tasarim.findViewById(R.id.satir_card)
            urun_ad_yazi = tasarim.findViewById(R.id.urun_ad_yazi)
            urun_fiyat_yazi = tasarim.findViewById(R.id.urun_fiyat_yazi)
            urun_sil_resim = tasarim.findViewById(R.id.urun_sil_resim)
            urun_ekle_resim = tasarim.findViewById(R.id.urun_ekle_resim)
            urun_resim = tasarim.findViewById(R.id.urun_resim)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimtutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.uruntasarim, parent, false)
        return CardTasarimtutucu(tasarim)
    }


    override fun onBindViewHolder(holder: CardTasarimtutucu, position: Int) {

        var urun = urunlerListe.get(position)

        holder.urun_ad_yazi.text = "${urun.yemek_adi}"
        holder.urun_fiyat_yazi.text = "${urun.yemek_fiyat}TL"


        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${urun.yemek_resim_adi}"
        Picasso.get().load(url).into(holder.urun_resim)

        holder.urun_sil_resim.setOnClickListener {
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

        holder.urun_ekle_resim.setOnClickListener {
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

        holder.satir_card.setOnClickListener {
            val intent = Intent(mContext, UrunDetayActivity::class.java)
            intent.putExtra("nesne", urun.yemek_adi)
            mContext.startActivity(Intent())
        }
    }

    override fun getItemCount(): Int {
        return urunlerListe.size
    }



    fun tumUrunler() {
        val url = "http://kasimadalan.pe.hu/yemekler/tum_yemekler.php"

        val istek = StringRequest(Request.Method.GET, url, Response.Listener { cevap ->

            try {
                val tempListe = ArrayList<Urun>()

                val jsonObj = JSONObject(cevap)
                val urunler = jsonObj.getJSONArray("yemekler")

                for (i in 0 until urunler.length()) {//3 -> 0,1,2
                    val k = urunler.getJSONObject(i)

                    val yemek_id = k.getInt("yemek_id")
                    val yemek_adi = k.getString("yemek_adi")
                    val yemek_resim_adi = k.getString("yemek_resim_adi")
                    val yemek_fiyat = k.getInt("yemek_fiyat")
                    val yemek_siparis_adet = k.getInt("yemek_siparis_adet")

                    val urun = Urun(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat, yemek_siparis_adet)
                    tempListe.add(urun)
                }

                urunlerListe = tempListe

                notifyDataSetChanged()

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Log.e("Hata", "Veri okuma") })

        Volley.newRequestQueue(mContext).add(istek)
    }
}

