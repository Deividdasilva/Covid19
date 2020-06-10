package com.example.trabalhocovid19

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_states_brazil.*
import okhttp3.internal.concurrent.Task

class StatesBrazilActivity : AppCompatActivity() {
    var estatisticasList = mutableListOf<Estatisticas>()
    var array_estados = ArrayList<String>()
    var adapter = ResultAdapter(this, array_estados, estatisticasList, "estados")
    private var asyncTask: EstatisticasTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_states_brazil)
        carregarEstatisticas()
    }
    private fun carregarEstatisticas() {
        estatisticasList.clear()
        if (estatisticasList.isNotEmpty()) {
            showProgress(false)
        } else {
            if (asyncTask == null) {
                if (EstatisticasHTTP.hasConnection(this)) {
                    if (asyncTask?.status != AsyncTask.Status.RUNNING) {
                        asyncTask = EstatisticasTask()
                        asyncTask?.execute()
                    } else {
                        progressStates.visibility = View.GONE
                        Toast.makeText(this, "Sem conexão!", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (asyncTask?.status == AsyncTask.Status.RUNNING) {
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
            val path = ""
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
            getEstados()
            initRecycler()
        }
    }

    fun showProgress(show: Boolean){
        if (!show){
            progressStates.visibility = if(show) View.VISIBLE else View.GONE
        }
    }

    fun getEstados() {
        for(index in 0 .. estatisticasList.size - 1) {
            val elemento = estatisticasList[index]
            array_estados.add(elemento.state.toString())
        }
    }

    fun initRecycler() {
        rv.adapter = adapter
        val layout = LinearLayoutManager(this)
        rv.layoutManager = layout
    }
}
