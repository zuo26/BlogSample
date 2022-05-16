package com.blog.demo40

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blog.a.R


class EspressoUiActivity : AppCompatActivity() {

    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_espresso_layout)

        startForResult = registerForActivityResult(StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                findViewById<TextView>(R.id.tv_intent).text = result.data?.getStringExtra("tag") ?: "null"
            }
        }

        test1(findViewById(R.id.btn_test1), findViewById(R.id.tv_test1))
        test2(findViewById(R.id.listview), findViewById(R.id.tv_test2))
        test5(findViewById(R.id.btn_intent), findViewById(R.id.tv_intent))
        test3(findViewById(R.id.btn_intent))
        test6(findViewById(R.id.recycler_view))
        test7(findViewById(R.id.btn_visible))
        test8(findViewById(R.id.btn_remove))
        test9(findViewById(R.id.btn_open_dialog), findViewById(R.id.tv_show_dialog_item))
        setUpAutoCompleteTextView(findViewById(R.id.auto_complete_text_view))
    }

    private fun test1(btn: Button, tv: TextView) {
        tv.text = getString(R.string.txt_simple)
        btn.setOnClickListener {
            tv.text = getString(R.string.txt_espresso)
        }
    }

    private fun test2(list: ListView, tv: TextView) {
        list.adapter = ArrayAdapter(this, R.layout.list_item_layout1, DATE)
        list.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> tv.text = DATE[position]}
    }

    private fun test3(btn: Button) {
        btn.setOnClickListener {
            val intent = Intent("custom.intent.action.demo40").apply {
                putExtra("type", 20)
                putExtra("tag", "zuo")
            }
            startForResult.launch(intent)
//            startActivity(intent) // 注意与startForResult冲突
        }
    }

    private fun test4(tv: TextView) {
        tv.text = intent.getStringExtra("tag") ?: "null"
    }

    private fun test5(btn: Button, tv: TextView) {
        btn.setOnClickListener {
            startForResult.launch(Intent("custom.intent.action.demo40"))
        }
    }

    private fun test6(recyclerView: RecyclerView) {
        recyclerView.also {
            it.layoutManager = LinearLayoutManager(applicationContext)
            it.adapter = CustomAdapter(DATE, this)
        }
    }

    private fun test7(btn: Button) {
        btn.setOnClickListener {
            btn.visibility = View.INVISIBLE
        }
    }

    private fun test8(btn: Button) {
        val parent = findViewById<RelativeLayout>(R.id.base_relative_layout)
        btn.setOnClickListener {
            parent.removeView(it)
        }
    }

    private fun test9(btn: Button, tv: TextView) {
        btn.setOnClickListener {
            openDialog(DATE, tv)
        }
    }

    private fun openDialog(items : Array<String>, tv: TextView) {
        AlertDialog.Builder(this).apply {
            setTitle("EspressoTest")
            setItems(
                items
            ) { _, which: Int ->
                tv.text = items[which]
            }
            show()
        }
    }

    private fun setUpAutoCompleteTextView(autoComplete: AutoCompleteTextView) {
        val array = resources.getStringArray(R.array.bodies_of_water)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, array)
        autoComplete.setAdapter(adapter)
    }

    companion object {
        val DATE = arrayOf(
            "item1",
            "item2",
            "item3",
            "item4",
            "item5"
        )
    }
}

