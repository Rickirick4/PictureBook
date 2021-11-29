package com.rickirick.fragmentartbook

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.rickirick.fragmentartbook.adapter.ArtAdapter
import com.rickirick.fragmentartbook.databinding.FragmentListBinding
import com.rickirick.fragmentartbook.model.Model
import com.rickirick.fragmentartbook.roomdb.Dao
import com.rickirick.fragmentartbook.roomdb.Database
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


private var _binding: FragmentListBinding? = null
private val binding get() = _binding!!
private val cDisposable = CompositeDisposable()
private lateinit var db : Database
private lateinit var dao : Dao



class ListFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext().applicationContext, Database::class.java, "Models").build()
        dao = db.Dao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFromSQL()
    }

    fun getFromSQL(){
        cDisposable.add(
            dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handlerResponse)
        )
    }

    private fun handlerResponse(artList : List<Model>){

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        val adapter = ArtAdapter(artList)
        binding.recyclerView.adapter = adapter

    }

}