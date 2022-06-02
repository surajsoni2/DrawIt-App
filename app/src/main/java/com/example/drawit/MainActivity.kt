package com.example.drawit

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
 import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    var customProgressDialog: Dialog? =null
    var isImagesaved: Boolean = false
    var savedPath: String? = null
    // for gallery part
    val openGalleryLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK && result.data!=null){
            val imageBg: ImageView = findViewById(R.id.iv_bg)
            imageBg.setImageURI(result.data?.data)
        }
    }

    val requestPermission: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
        permissions.entries.forEach{
            val permissionName = it.key
            val isGranted = it.value

            if (isGranted && permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
//                    Toast.makeText(this@MainActivity,"Permisssion Granted now you can read the storage files",Toast.LENGTH_SHORT).show()
                    val pickIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    openGalleryLauncher.launch(pickIntent)


            }else{
                if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this@MainActivity,
                        "Oops! You denied the permission.",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(10.toFloat())

        //Brushsize chabge button code starts here
        val brushBtn: ImageButton = findViewById(R.id.brush)
        brushBtn.setOnClickListener {
            showBrushsizeChooserDialog()
        }

        //Undo Button Code Starts here
        val undo: ImageButton = findViewById(R.id.undo)
        undo.setOnClickListener {
            drawingView?.onClickUndo()
        }

        //Redo Button Code Starts here
        val redo: ImageButton = findViewById(R.id.redo)
        redo.setOnClickListener {
            drawingView?.onClickRedo()
        }

        //Gallery and image selecting btn start here
        val gallerybtn: ImageButton = findViewById(R.id.gallerybtn)



        gallerybtn.setOnClickListener{
            requestStoragePermission()
        }

        //New File button code starts here
        val newFilebtn: ImageButton = findViewById(R.id.newfilebtn)
        fun showWarningBeforeNew(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure?")
                .setMessage("Please Confirm and Save your previous file, It will delete all the content of the previous file")
                .setPositiveButton("Ok"){
                        dialog, which->
                    // Creating a new file
                    drawingView?.newFile()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel"){
                        dialog,_->dialog.dismiss()
                }
                .show()
        }
        newFilebtn.setOnClickListener {
            showWarningBeforeNew()
        }

        // Save button Code Starts Here
        val savebtn: ImageButton = findViewById(R.id.save)
        savebtn.setOnClickListener {
            if (isReadStorageAllowed()){
                showProgressDialog()
                lifecycleScope.launch{
                    val flDrawingView:FrameLayout = findViewById(R.id.fl_drawing_container)
//                    val myBitmap: Bitmap = getBitmapFromView(flDrawingView)
                    saveBitmapFile(getBitmapFromView(flDrawingView))
                }
            }

        }

        val sharebtn: ImageButton = findViewById(R.id.share)
        sharebtn.setOnClickListener {
            shareImage(savedPath!!)
        }
    }

     private fun showBrushsizeChooserDialog() {
            val brushDialog = Dialog(this)
            brushDialog.setContentView(R.layout.dialog_brush_size)
            brushDialog.setTitle("Brush size: ")

            val extrasmallBtn = brushDialog.findViewById<ImageButton>(R.id.extra_small_brush)
            extrasmallBtn.setOnClickListener{
                drawingView?.setSizeForBrush(2.toFloat())
                brushDialog.dismiss()
            }

            val smallBtn = brushDialog.findViewById<ImageButton>(R.id.small_brush)
            smallBtn.setOnClickListener{
                drawingView?.setSizeForBrush(5.toFloat())
                brushDialog.dismiss()
            }

            val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.medium_brush)
            mediumBtn.setOnClickListener{
                drawingView?.setSizeForBrush(10.toFloat())
                brushDialog.dismiss()
            }

            val largeBtn = brushDialog.findViewById<ImageButton>(R.id.large_brush)
            largeBtn.setOnClickListener{
                drawingView?.setSizeForBrush(15.toFloat())
                brushDialog.dismiss()
            }

            brushDialog.show()
            val extralargeBtn = brushDialog.findViewById<ImageButton>(R.id.extra_large_brush)
            extralargeBtn.setOnClickListener{
                drawingView?.setSizeForBrush(30.toFloat())
                brushDialog.dismiss()
            }
            brushDialog.show()
        }

     fun changeColorToGreen(view:View){
            drawingView?.changeColor(Color.GREEN)

    }
     fun changeColorToYellow(view:View){
            drawingView?.changeColor(Color.YELLOW)
    }
     fun changeColorToSkyBlue(view:View){
         drawingView?.changeColor(Color.parseColor("#87CEEB"))
     }
     fun changeColorToRed(view: View) {
            drawingView?.changeColor(Color.RED)
    }

    private fun getBitmapFromView(view: View): Bitmap{

        val returnedBitmap = Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if(bgDrawable!=null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return returnedBitmap
    }

    private suspend fun saveBitmapFile(mBitmap: Bitmap?):String{
        var result = ""
        withContext(Dispatchers.IO){
            if(mBitmap!=null){
                try{
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                    val storagePath = getExternalFilesDir(Environment.DIRECTORY_DCIM)
                    val f = File(storagePath?.absoluteFile.toString()
                                + File.separator+"DrawITApp_"+System.currentTimeMillis() / 1000 + ".jpg"
                    )
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result = f.absolutePath
                    savedPath = result
                    runOnUiThread{
                        cancelProgressDialog()
                        if (result.isNotEmpty()){
                            Toast.makeText(this@MainActivity,"File saved succesfully :$result",Toast.LENGTH_LONG).show()
                            isImagesaved = true
                        }else{
                            Toast.makeText(this@MainActivity,"Something went wrong while saving th file.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e: Exception){
                    result = ""
                    e.printStackTrace()

                }
            }
        }
        return result
    }

    private fun showRationale(title: String,message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            showRationale("Kids Drawing App","Kids Drawing app needs to access your external storage. You can Allow it from\n" +
                    "Setting->Permissions->Files & Storage")
        }else{
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
            //TODO add external storagge permission
        }

    }
    private fun isReadStorageAllowed():Boolean{
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun showProgressDialog(){
        customProgressDialog = Dialog(this)
        customProgressDialog?.setContentView(R.layout.dialog_custom_dialog)
        customProgressDialog?.show()
    }
    private fun cancelProgressDialog(){
        if (customProgressDialog != null){
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

// Share function
    private fun shareImage(result: String){

        MediaScannerConnection.scanFile(this, arrayOf(result), null){
            path, uri->
            val shareIntent = Intent()
            shareIntent.action =Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
            shareIntent.type = "image/png"
            startActivity(Intent.createChooser(shareIntent,"Share"))
        }
    }

}

