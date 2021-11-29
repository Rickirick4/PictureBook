package com.rickirick.picturebook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.rickirick.picturebook.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding
private lateinit var pictureList : ArrayList<Picture>
private lateinit var pictureAdapter : PictureAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        pictureList = ArrayList<Picture>()
        pictureAdapter = PictureAdapter(pictureList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        try {

            val database = openOrCreateDatabase("Pictures", MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM arts", null)
            val pictureNameIx = cursor.getColumnIndex("picturename")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                val name = cursor.getString(pictureNameIx)
                val id = cursor.getInt(idIx)
                val picture = Picture(name, id)
                pictureList.add(picture)
            }

            pictureAdapter.notifyDataSetChanged()

            cursor.close()

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.picture_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_picture_item){
            val intent = Intent(this@MainActivity,DetailsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


}