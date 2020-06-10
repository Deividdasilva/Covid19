package com.example.trabalhocovid19

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_country_world_result.*
import kotlinx.android.synthetic.main.activity_states_brazil_result.*
import kotlinx.android.synthetic.main.activity_states_brazil_result.progress
import kotlinx.android.synthetic.main.activity_states_brazil_result.txt_casos
import kotlinx.android.synthetic.main.activity_states_brazil_result.txt_confirmados
import kotlinx.android.synthetic.main.activity_states_brazil_result.txt_curados
import kotlinx.android.synthetic.main.activity_states_brazil_result.txt_obitos
import kotlinx.android.synthetic.main.activity_states_brazil_result.txt_title
import kotlinx.android.synthetic.main.activity_world.*
import java.text.DecimalFormat

class ResultActivity : AppCompatActivity() {

    private var asyncTask: EstatisticasTask? = null
    var paisOuEstado = ""
    var endereco = ""
    var name = ""
    var dados_ = Estatisticas(
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
    private var dados = dados_

    override fun onCreate(savedInstanceState: Bundle?) {
        val name_ = intent.getStringExtra("nome")
        val endereco_ = intent.getStringExtra("endereco")
        val uf_ = intent.getStringExtra("uf")
        paisOuEstado = name_
        endereco = endereco_
        if(endereco == "estados") {
            paisOuEstado = uf_
            name = name_
            setContentView(R.layout.activity_states_brazil_result)
        } else if(endereco == "paises") {
           setContentView(R.layout.activity_country_world_result)
        }
        super.onCreate(savedInstanceState)
        carregarEstatisticas()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun carregarEstatisticas() {
        dados = dados_
        if (dados != dados_) {
            showProgress(false)
        } else {
            if (asyncTask == null) {
                if (EstatisticasHTTP.hasConnection(this)) {
                    if (asyncTask?.status != AsyncTask.Status.RUNNING) {
                        asyncTask = EstatisticasTask()
                        asyncTask?.execute()
                    } else {
                       progress.visibility = View.GONE
                        Toast.makeText(this, "Sem conexão!", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (asyncTask?.status == AsyncTask.Status.RUNNING) {
                showProgress(true)
            }
        }
    }

    inner class EstatisticasTask: AsyncTask<Void, Void, Estatisticas>() {
        override fun onPreExecute() {
            super.onPreExecute()
            showProgress(true)
        }

        override fun doInBackground(vararg params: Void?): Estatisticas? {
            var path = ""
            if(endereco == "estados") {
                path = "/brazil/uf/${paisOuEstado}"
            } else if(endereco == "paises") {
                path = "/${paisOuEstado}"
            }

            val list = EstatisticasHTTP.loadEstatisticas(path)
            return list[0]
        }
        override fun onPostExecute(resultado: Estatisticas?) {
            super.onPostExecute(resultado)
            showProgress(false)
            atualizarEstatisticas(resultado)
        }
    }

    private fun atualizarEstatisticas(resultado: Estatisticas?) {
        if(resultado != null) {
            dados = dados_
            dados = resultado
        }
        if(endereco == "estados") {
            exibirEstado()
        } else if(endereco == "paises") {
            exibirPais()
        }
    }

    fun showProgress(show: Boolean){
        if (!show){
           progress.visibility = if(show) View.VISIBLE else View.GONE
        }
    }

    fun exibirPais() {
        val df = DecimalFormat("###,###")
        txt_casos.text = df.format(dados.cases)
        txt_confirmados.text = df.format(dados.confirmed)
        txt_curados.text = df.format(dados.recovered)
        txt_obitos.text = df.format(dados.deaths)
        txt_title.text = "${paisOuEstado} \nEstatísticas:"
    }

    fun exibirEstado() {
        paisOuEstado = name
        val df = DecimalFormat("###,###")
        txt_casos.text = df.format(dados.cases)
        txt_confirmados.text = df.format(dados.suspects)
        txt_curados.text = df.format(dados.refuses)
        txt_obitos.text = df.format(dados.deaths)
        txt_title.text = "${paisOuEstado} \nEstatísticas:"
    }
}

