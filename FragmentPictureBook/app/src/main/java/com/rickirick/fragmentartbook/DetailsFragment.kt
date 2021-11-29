package com.rickirick.fragmentartbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rickirick.fragmentartbook.model.Model
import com.rickirick.fragmentartbook.roomdb.Dao
import com.rickirick.fragmentartbook.roomdb.Database
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_details.*
import java.io.ByteArrayOutputStream


private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
private lateinit var permissionLauncher: ActivityResultLauncher<String>
var selectedPicture : Uri? = null
var selectedBitmap : Bitmap? = null
private lateinit var db : Database
private lateinit var dao : Dao
val compositeDisposable = CompositeDisposable()
var artFromMain : Model? = null


class DetailsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db = Room.databaseBuilder(requireContext().applicationContext, Database::class.java, "Models").build()

        dao = db.Dao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView.setOnClickListener { selectImage(view) }
        button.setOnClickListener { uploadBtn(view) }


        arguments?.let {
            val info = DetailsFragmentArgs.fromBundle(it).info
            if (info.equals("new")){
                nameText.setText("")
                commentText.setText("")
                button.visibility = View.VISIBLE

                val selectImage = BitmapFactory.decodeResource(context?.resources,R.drawable.selecimage)
                imageView.setImageBitmap(selectImage)
            }else {
                button.visibility = View.GONE

                val selectedID = DetailsFragmentArgs.fromBundle(it).id
                compositeDisposable.add(dao.getArtById(selectedID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handlerResponseOld))

            }
        }

    }

    private fun handlerResponseOld(model : Model){
        artFromMain = model
        nameText.setText(model.pictureName)
        commentText.setText(model.comment)
        model.image.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            imageView.setImageBitmap(bitmap)
        }
    }


    private fun makeSmallerBitmap (image: Bitmap, maxSize : Int) : Bitmap{
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1 ){
            width = maxSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        }else{
            height = maxSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)

    }

    fun selectImage(view: View){

        activity?.let {
            if (ContextCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                   if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                       Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                           //require permission
                           permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                       }).show()
                   }else{
                       //require permission
                       permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                   }
            }else{
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver,
                                selectedPicture!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(selectedBitmap)
                        }else{
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,
                                selectedPicture)
                            imageView.setImageBitmap(selectedBitmap)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result){
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(requireContext().applicationContext, "Permission Needed!", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun uploadBtn(view: View){

        val pictureName = nameText.text.toString()
        val commentText = commentText.text.toString()



        if (selectedBitmap != null){
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            val model = Model(pictureName,commentText, byteArray)

            compositeDisposable.add(
                dao.insert(model)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handlerResponse)
            )

        }
    }

    private fun handlerResponse(){
        val action = DetailsFragmentDirections.actionDetailsFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

}
