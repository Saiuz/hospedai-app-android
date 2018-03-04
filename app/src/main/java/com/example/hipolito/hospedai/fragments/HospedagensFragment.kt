package com.example.hipolito.hospedai.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.hipolito.hospedai.R
import com.example.hipolito.hospedai.adapters.HospedagensRVAdapter
import com.example.hipolito.hospedai.api.APIService
import com.example.hipolito.hospedai.model.Hospedagem
import com.example.hipolito.hospedai.model.Hotel
import com.example.hipolito.hospedai.util.HospedaiConstants
import com.example.hipolito.hospedai.util.SecurityPreferences
import kotlinx.android.synthetic.main.fragment_hospedagens.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospedagensFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var apiService: APIService
    private lateinit var securityPreferences: SecurityPreferences

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater!!.inflate(R.layout.fragment_hospedagens, container, false)
        initComponents()

        return mView
    }

    private fun initComponents() {
        securityPreferences = SecurityPreferences(context)
        apiService = APIService(getToken())

        getHospedagens()
    }

    private fun getHospedagens(){
        val hospedagensCall = apiService.hospedagemEndPoint.getHospedagens(getHotelSelecionado())

        hospedagensCall.enqueue(object: Callback<MutableList<Hospedagem>>{
            override fun onFailure(call: Call<MutableList<Hospedagem>>?, t: Throwable?) {
                Toast.makeText(context, "Failure: " + t!!.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MutableList<Hospedagem>>?, response: Response<MutableList<Hospedagem>>?) {
                if (response!!.isSuccessful){
                    if (response.body().isNotEmpty()){
                        exibirLista(response.body())
                    }else{
                        Toast.makeText(context, "Sem Hospedagens!", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "Erro: " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun exibirLista(hospedagensList: MutableList<Hospedagem>){
        val hospedagensRVAdapter = HospedagensRVAdapter(context, activity as AppCompatActivity, hospedagensList)

        rvHospedagensFragment.adapter = hospedagensRVAdapter

        val linearLayoutManager = LinearLayoutManager(context as Activity, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.scrollToPosition(0)

        rvHospedagensFragment.layoutManager = linearLayoutManager
        rvHospedagensFragment.setHasFixedSize(true)
    }

    private fun getHotelSelecionado(): Long{
        return securityPreferences.getSavedLong(HospedaiConstants.KEY.HOTEL_SELECIONADO)
    }

    private fun getToken(): String{
        return securityPreferences.getSavedString(HospedaiConstants.KEY.TOKEN_LOGADO)
    }
}
