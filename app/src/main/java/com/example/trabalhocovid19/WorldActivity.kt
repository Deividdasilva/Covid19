package com.example.trabalhocovid19

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_world.*
import java.text.DecimalFormat

class WorldActivity : AppCompatActivity() {
    private var estatisticasList = mutableListOf<Estatisticas>()
    private var asyncTask: EstatisticasTask? = null
    val arrayEstatisticas = Estatisticas(
        country = null,
        uf = null,
        state = null,
        suspects = 0,
        refuses = 0,
        cases = 0,
        confirmed = 0,
        deaths = 0,
        recovered = 0,
        date = null,
        hour = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world)
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
                    } else {
                        progress.visibility = View.GONE
                        Toast.makeText(this, "falha conexao", Toast.LENGTH_LONG).show()
                    }
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
            Dados()
            exibir()
        }
    }

    fun showProgress(show: Boolean){
        if (!show){
            progress.visibility = if(show) View.VISIBLE else View.GONE
        }
    }

    fun Dados() {
        for(index in 0 .. estatisticasList.size - 1) {
            val pais = estatisticasList[index]
            arrayEstatisticas.cases = arrayEstatisticas.cases + pais.cases
            arrayEstatisticas.confirmed = arrayEstatisticas.confirmed + pais.confirmed
            arrayEstatisticas.deaths = arrayEstatisticas.deaths + pais.deaths
            arrayEstatisticas.recovered = arrayEstatisticas.recovered + pais.recovered
        }
    }

    fun exibir() {
        val df = DecimalFormat("###,###")
        txt_casos.text = df.format(arrayEstatisticas.cases)
        txt_confirmados.text = df.format(arrayEstatisticas.confirmed)
        txt_curados.text = df.format(arrayEstatisticas.recovered)
        txt_obitos.text = df.format(arrayEstatisticas.deaths)
        txt_title.text = "Estatísticas \n Mundiais:"
    }
}
