package com.example.normalapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Objects

class BasementActivity : AppCompatActivity() {
    private val dataset: ArrayList<Array<String>> = ArrayList()
    var dateAndTime = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basement)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val homeButton = findViewById<Button>(R.id.homeButton)
        val addChildButton = findViewById<ImageButton>(R.id.addChildButton)

        val loginIntent = Intent(this, LoginActivity::class.java)
        val signUpIntent = Intent(this, RegisterActivity::class.java)
        val homeIntent = Intent(this, MainActivity::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBasement)
        val gridLayoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.isScrollContainer = true

        val customAdapter: CustomAdapter? = CustomAdapter(dataset, this)
        recyclerView.adapter = customAdapter

        recyclerView.adapter

        val swipeToDeleteCallback = SwipeToDeleteCallback(this, dataset, recyclerView)

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        addChildButton.setOnClickListener {
            val alert: AlertDialog.Builder = AlertDialog.Builder(this)

            alert.setTitle("Новый рабо... Ой, ребенок")
            alert.setMessage("Введите данные ребенка")

            val name = EditText(this)
            name.id = View.generateViewId()
            val date = TextView(this)
            date.id = View.generateViewId()
            val dateButton = Button(this)
            dateButton.id = View.generateViewId()
            dateButton.text = "Выбрать дату"

            val mainLayout = ConstraintLayout(this)
            mainLayout.id = View.generateViewId()

            val nameLayout = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            val dateLayout = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            val dateButtonLayout = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            nameLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            nameLayout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            dateLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            dateLayout.topToBottom = name.id
            dateButtonLayout.topToBottom = date.id
            dateButtonLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID

            name.layoutParams = nameLayout
            date.layoutParams = dateLayout
            dateButton.layoutParams = dateButtonLayout

            var d =
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dateAndTime.set(Calendar.YEAR, year)
                    dateAndTime.set(Calendar.MONTH, monthOfYear)
                    dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    date.text = DateUtils.formatDateTime(
                        this,
                        dateAndTime.timeInMillis,
                        DateUtils.FORMAT_SHOW_YEAR
                    )
                }

            fun setDate(v: View?) {
                DatePickerDialog(
                    this@BasementActivity, d,
                    dateAndTime[Calendar.YEAR],
                    dateAndTime[Calendar.MONTH],
                    dateAndTime[Calendar.DAY_OF_MONTH]
                )
                    .show()
            }

            dateButton.setOnClickListener {
                setDate(mainLayout)
            }

            mainLayout.addView(name)
            mainLayout.addView(date)
            mainLayout.addView(dateButton)

            alert.setView(mainLayout)

            alert.setPositiveButton(
                "Добавить",
                DialogInterface.OnClickListener { dialog, whichButton ->
                    var age =
                        Calendar.getInstance().get(Calendar.YEAR) - dateAndTime.get(Calendar.YEAR)

                    if ((Calendar.getInstance()
                            .get(Calendar.MONTH) < dateAndTime.get(Calendar.MONTH))
                        || (Calendar.getInstance()
                            .get(Calendar.MONTH) == dateAndTime.get(Calendar.MONTH)
                                && Calendar.getInstance()
                            .get(Calendar.DAY_OF_MONTH) < dateAndTime.get(Calendar.DAY_OF_MONTH))
                    ) {
                        age -= 1
                    }

                    dataset.add(arrayOf(name.text.toString(), age.toString(), date.text.toString()))
                    recyclerView.adapter?.notifyItemInserted(dataset.size - 1)

                    dateAndTime.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                    dateAndTime.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
                    dateAndTime.set(
                        Calendar.DAY_OF_MONTH,
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    )
                })

            alert.setNegativeButton("Отмена",
                DialogInterface.OnClickListener { dialog, whichButton -> })

            alert.show()
        }

        loginButton.setOnClickListener {
            startActivity(loginIntent)
        }

        signUpButton.setOnClickListener {
            startActivity(signUpIntent)
        }

        homeButton.setOnClickListener {
            startActivity(homeIntent)
        }
    }
}

class CustomAdapter(private val dataSet: ArrayList<Array<String>>, private val context: Context) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {
    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView
        val ageTextView: TextView
        val dateTextView: TextView
        val editButton: Button


        init {
            nameTextView = view.findViewById(R.id.nameTextView)
            ageTextView = view.findViewById(R.id.ageTextView)
            dateTextView = view.findViewById(R.id.dateTextView)
            editButton = view.findViewById(R.id.editButton)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.text_row_item, viewGroup, false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder , position: Int) {
        viewHolder.nameTextView.text = dataSet[position][0]
        viewHolder.ageTextView.text = dataSet[position][1]
        viewHolder.dateTextView.text = dataSet[position][2]

        viewHolder.editButton.setOnClickListener {
            var dateAndTime = Calendar.getInstance()
            val alert: AlertDialog.Builder = AlertDialog.Builder(context)

            alert.setTitle("Изменение инфы о ребенке")
            alert.setMessage("Измените данные ребенка")

            val name = EditText(context)
            name.id = View.generateViewId()
            val date = TextView(context)
            date.id = View.generateViewId()
            val dateButton = Button(context)
            dateButton.id = View.generateViewId()
            dateButton.text = "Выбрать дату"

            name.setText(dataSet[position][0])
            date.text = dataSet[position][2]

            val mainLayout = ConstraintLayout(context)
            mainLayout.id = View.generateViewId()

            val nameLayout = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            val dateLayout = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            val dateButtonLayout = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

            nameLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            nameLayout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            dateLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            dateLayout.topToBottom = name.id
            dateButtonLayout.topToBottom = date.id
            dateButtonLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID

            name.layoutParams = nameLayout
            date.layoutParams = dateLayout
            dateButton.layoutParams = dateButtonLayout

            var d =
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dateAndTime.set(Calendar.YEAR, year)
                    dateAndTime.set(Calendar.MONTH, monthOfYear)
                    dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    date.text = DateUtils.formatDateTime(context, dateAndTime.timeInMillis, DateUtils.FORMAT_SHOW_YEAR)
                }

            fun setDate(v: View?) {
                DatePickerDialog(
                    context, d,
                    dateAndTime[Calendar.YEAR],
                    dateAndTime[Calendar.MONTH],
                    dateAndTime[Calendar.DAY_OF_MONTH]
                )
                    .show()
            }

            dateButton.setOnClickListener {
                setDate(mainLayout)
            }

            mainLayout.addView(name)
            mainLayout.addView(date)
            mainLayout.addView(dateButton)

            alert.setView(mainLayout)

            alert.setPositiveButton("Изменить", DialogInterface.OnClickListener { dialog, whichButton ->
                var age = Calendar.getInstance().get(Calendar.YEAR) - dateAndTime.get(Calendar.YEAR)

                if ((Calendar.getInstance().get(Calendar.MONTH) < dateAndTime.get(Calendar.MONTH))
                    || (Calendar.getInstance().get(Calendar.MONTH) == dateAndTime.get(Calendar.MONTH)
                            && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < dateAndTime.get(Calendar.DAY_OF_MONTH))) {
                    age -= 1
                }

                dataSet[position] = arrayOf(name.text.toString(), age.toString(), date.text.toString())
                this.notifyItemChanged(position)

                dateAndTime.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                dateAndTime.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
                dateAndTime.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            })

            alert.setNegativeButton("Отмена",
                DialogInterface.OnClickListener { dialog, whichButton -> })

            alert.show()
        }
    }

    override fun getItemCount() = dataSet.size

    private fun bind(view: CustomViewHolder) {

    }
}

class SwipeToDeleteCallback internal constructor(
    private var mContext: Context,
    private val dataset: ArrayList<Array<String>>,
    private val recyclerView: RecyclerView,
) :
    ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColor: Int = Color.parseColor("#b80f0a")
    private val deleteDrawable: Drawable?
    private val intrinsicWidth: Int
    private val intrinsicHeight: Int

    init {
        mClearPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_launcher_background)
        intrinsicWidth = Objects.requireNonNull(deleteDrawable)!!.intrinsicWidth
        intrinsicHeight = deleteDrawable!!.intrinsicHeight
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.DOWN)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dY == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.top.toFloat(),
                itemView.bottom.toFloat(),
                intrinsicWidth.toFloat(),
                itemView.bottom  + dY
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        mBackground.color = backgroundColor
        mBackground.setBounds(
            itemView.left,
            itemView.bottom,
            intrinsicWidth,
            itemView.bottom + dY.toInt()
        )
        mBackground.draw(c)
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconTop = itemView.bottom + deleteIconMargin
        val deleteIconLeft = itemView.left
        val deleteIconRight = itemView.left + intrinsicWidth
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        deleteDrawable!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        dataset.removeAt(viewHolder.adapterPosition)
        recyclerView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
    }
}