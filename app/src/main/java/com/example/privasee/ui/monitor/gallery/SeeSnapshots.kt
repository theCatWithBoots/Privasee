package com.example.privasee.ui.monitor.gallery

import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.privasee.R
import com.example.privasee.databinding.SeeSnapshotsBinding
import java.io.File


class SeeSnapshots : AppCompatActivity() {

    private lateinit var binding: SeeSnapshotsBinding
    private var imageRecycler: RecyclerView?=null
    private var progressBar: ProgressBar?=null
    private lateinit var allPictures: List<SharedStoragePhoto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SeeSnapshotsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageRecycler = findViewById(R.id.image_recycler)
        progressBar = findViewById(R.id.recycler_progress)


        imageRecycler?.layoutManager = GridLayoutManager(this, 3)
        imageRecycler?.setHasFixedSize(true)

        if(ContextCompat.checkSelfPermission(
                this@SeeSnapshots,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@SeeSnapshots,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        }

            progressBar?.visibility = View.VISIBLE
            //get all images from storage
            allPictures = getAllImages()
           // val currentImage= allPictures?.get(1)
            //set adapter to recycler
            imageRecycler?.adapter = ImageAdapter(this,allPictures)
            progressBar?.visibility = View.GONE
    }

    private fun getAllImages(): List<SharedStoragePhoto>{

        val collection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: Uri.fromFile(File((getOutputDirectory()).toString() + "/Snapshots"))

        val allImageUri = (File((getOutputDirectory()).toString() + "/Snapshots")).toString()

        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )

        val photos = mutableListOf<SharedStoragePhoto>()
        contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val imagePath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while(cursor.moveToNext()) {
                val path = cursor.getString(imagePath)
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val stringSplit = path.substring(path.lastIndexOf("/")+1); //split file path, take last(file)

                val newPath = path.replace("/$stringSplit", "")

                if(newPath == allImageUri){
                    photos.add(SharedStoragePhoto(path, id, displayName, width, height, contentUri))
                }
            }
            photos.toList()
        } ?: listOf()

        return photos
    }


    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {mFile->
            File(mFile,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(getIntent())
    }



}