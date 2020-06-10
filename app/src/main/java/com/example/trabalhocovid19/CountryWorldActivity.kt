package com.example.trabalhocovid19

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_country_world.*

class CountryWorldActivity : AppCompatActivity() {
    private var estatisticasList = mutableListOf<Estatisticas>()
    var array_paises = ArrayList<String>()
    var adapter = ResultAdapter(this, array_paises, estatisticasList, "paises")
    private var asyncTask: EstatisticasTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_world)
        carregarEstatisticas()
    }
    private fun carregarEstatisticas() {
        estatisticasList.clear()
        if (estatisticasList.isNotEmpty()) {
            showProgress(false)
        } else {
            if(asyncTask == null) {
                if(EstatisticasHTTP.hasConnection(this)) {
                    if(asyncTask?.status != AsyncTask.Status.RUNNING) {
                        asyncTask = EstatisticasTask()
                        asyncTask?.execute()
                    }
                } else {
                    progressWord.visibility = View.GONE
                    Toast.makeText(this, "Sem conexão!", Toast.LENGTH_LONG).show()
                }
            } else if(asyncTask?.status == AsyncTask.Status.RUNNING) {
                showProgress(true)
            }
        }
    }

    inner class EstatisticasTask: AsyncTask<Void, Void, List<Estatisticas>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            showProgress(true)
        }

        override fun doInBackground(vararg params: Void?): List<Estatisticas>? {
            val path = "/countries"
            return EstatisticasHTTP.loadEstatisticas(path)
        }
        override fun onPostExecute(resultado: List<Estatisticas>?) {
            super.onPostExecute(resultado)
            showProgress(false)
            atualizarEstatisticas(resultado)
        }
    }

    private fun atualizarEstatisticas(resultado: List<Estatisticas>?) {
        if(resultado != null) {
            this.estatisticasList.clear()
            this.estatisticasList.addAll(resultado)
            getPaises()
            initRecycler()
        }
    }

    fun showProgress(show: Boolean){
        if (!show){
            progressWord.visibility = if(show) View.VISIBLE else View.GONE
        }
    }

    fun getPaises() {
        for(index in 0 .. estatisticasList.size - 1) {
            val elemento = estatisticasList[index]
            array_paises.add(elemento.country.toString())
        }
    }

    fun initRecycler() {
        rv.adapter = adapter
        val layout = LinearLayoutManager(this)
        rv.layoutManager = layout
    }
}
