package com.example.drawit

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder


//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val brushBtn: ImageButton = findViewById(R.id.brush)
        brushBtn.setOnClickListener {
            showBrushsizeChooserDialog()
        }
        val erase: ImageButton = findViewById(R.id.eraser)
        erase.setOnClickListener {
            drawingView?.eraser()

        }
//        val currentBackgroundColor = 0
//        val colorbtn:ImageButton =findViewById(R.id.color)
//        colorbtn.setOnClickListener{
//            ColorPickerDialogBuilder
//                .with(this)
//                .setTitle("Choose color")
//                .initialColor(currentBackgroundColor)
//                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//                .density(12)
//                .setOnColorSelectedListener { selectedColor ->
//                    Toast.makeText(this,"colorPicked",Toast.LENGTH_LONG)
//                }
//                .setPositiveButton(
//                    "ok"
//                ) { dialog, selectedColor, allColors -> changeBackgroundColor(selectedColor) }
//                .setNegativeButton(
//                    "cancel"
//                ) { dialog, which -> }
//                .build()
//                .show()
//        }
//
    }

    private fun changeBackgroundColor(selectedColor: Int) {
        val color = 1;
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

}
