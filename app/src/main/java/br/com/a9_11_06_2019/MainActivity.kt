package br.com.a9_11_06_2019

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.net.URL

class MainActivity : AppCompatActivity() {

    val URL_IMAGEM = "http://s2.glbimg.com/wB2k5I1ty4iVdwzurRl40rcoSqo=/e.glbimg.com/og/ed/f/original/2017/07/20/beach-1790049_960_720.jpgX"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //workThread()
        //CarregarImagemTask().execute()
        carregarImagemAnko()
    }

    fun workThread(){

        Thread(Runnable{

            val bitmap = carregarImagem()

            ImgFoto.post {
                ImgFoto.setImageBitmap(bitmap)
            }
        }).start()

    }


    inner class CarregarImagemTask : AsyncTask<Void, Void, Bitmap?>(){

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            ImgFoto.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Bitmap? {
            SystemClock.sleep(3000)
            return carregarImagem()
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            if(result !=null) {
                progressBar.visibility = View.GONE
                ImgFoto.visibility = View.VISIBLE
                ImgFoto.setImageBitmap(result)
            }else{

                alert("Erro ao carregar imagem", "Atenção") {

                    yesButton {  }
                    noButton {  }
                    //okButton {  }

                }.show()

            }
        }
    }


    fun carregarImagemAnko(){

        progressBar.visibility = View.VISIBLE
        ImgFoto.visibility = View.GONE

        doAsync {
            SystemClock.sleep(3000)
            val bitmap = carregarImagem()

            uiThread {

                if(bitmap != null) {

                    ImgFoto.setImageBitmap(bitmap)
                    progressBar.visibility = View.GONE
                    ImgFoto.visibility = View.VISIBLE
                }else{

                    alert("Erro ao carregar imagem", "Atenção"){
                        okButton {  }
                    }.show()
                }
            }
        }

    }

    fun carregarImagem(): Bitmap?{

        try {
            val url = URL(URL_IMAGEM)

            val bitmap = BitmapFactory.decodeStream(
                url.openConnection()
                    .getInputStream()
            )

            return bitmap
        }catch (ex: Exception){
            Log.d("MainActivity", "Imagem não encontrada")
            return null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_a9, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            R.id.menu_thread -> {
                workThread()
            }
            R.id.menu_task -> CarregarImagemTask().execute()
            R.id.menu_anko -> carregarImagemAnko()
            R.id.menu_delete -> ImgFoto.setImageBitmap(null)
        }

        return super.onOptionsItemSelected(item)
    }


}
